package org.eclipselabs.emongo.monitor.elasticsearch;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.eclipselabs.emongo.MongoServerStatsPublisher;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ElasticSearchPublisher implements MongoServerStatsPublisher
{
  public @interface PublisherConfig
  {
    String uri();
    int timeout() default 5000;
  }
  
  private CloseableHttpClient client;
  private HttpPost request;
  private Map<String, Object> previousValues = new HashMap<>();
  private Set<String> statsThatNeedDeltas = new HashSet<>();
  private AtomicReference<LogService> logService = new AtomicReference<>();
  
  @Activate
  public void activate(PublisherConfig config)
  {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(config.timeout())
        .setConnectTimeout(config.timeout())
        .setSocketTimeout(config.timeout())
        .build();
    
    client = HttpClients.createDefault();
    request = new HttpPost(config.uri());
    request.setConfig(requestConfig);
    
    statsThatNeedDeltas.add("extra_info.l_page_faults");
    statsThatNeedDeltas.add("network.l_bytesIn");
    statsThatNeedDeltas.add("network.l_bytesOut");
  }

  @Deactivate
  public void deactivate() throws IOException
  {
    if (client != null)
      client.close();
  }

  @Override
  public void publishStats(Document stats)
  {
    log(LogService.LOG_DEBUG, "Publishing MongoDB stats to ElasticSearch: '" + request.getURI() + "'");
    String metrics = buildMetrics(stats);    
    log(LogService.LOG_DEBUG, metrics);
    
    EntityBuilder builder = EntityBuilder.create();
    builder.setText(metrics);
    request.setEntity(builder.build());

    try(CloseableHttpResponse response = client.execute(request))
    {
      log(LogService.LOG_DEBUG, "Response from publish: " + response.getStatusLine().getStatusCode() + " '" + response.getStatusLine().getReasonPhrase() + "'");
      
      if(response.getStatusLine().getStatusCode() >= 400)
        log(LogService.LOG_WARNING, "Failed to publish: " + response.getStatusLine().getStatusCode() + " '" + response.getStatusLine().getReasonPhrase() + "'");
        
    } 
    catch (IOException e)
    {
      log(LogService.LOG_WARNING, "HTTP POST threw unexpected exception", e);
    }
  }
  
  @Reference(cardinality=ReferenceCardinality.OPTIONAL, policy=ReferencePolicy.DYNAMIC)
  public void bindLogService(LogService logService)
  {
    this.logService.set(logService);
  }
  
  public void unbindLogService(LogService logService)
  {
    this.logService.compareAndSet(logService, null);
  }
  
  private String buildMetrics(Document stats)
  {
    log(LogService.LOG_DEBUG, "Mapping field names");    
    Document mappedStats = mapFieldNames(stats);

    log(LogService.LOG_DEBUG, "Building deltas");    
    buildDeltas(mappedStats);

    mappedStats.put("ts_timestamp", new Date().getTime());
    
    return new JSONObject(mappedStats).toString();
  }
  
  private void buildDeltas(Document stats)
  {
    statsThatNeedDeltas.forEach((stat) -> {
      String[] split = stat.split("\\.");
      String[] hierarchy = new String[split.length - 1];
      System.arraycopy(split, 0, hierarchy, 0, hierarchy.length);
      String name = split[hierarchy.length];
      
      Document target = stats;
      
      for(String level : hierarchy)
        target = target.get(level, Document.class);
      
      Object value = target.get(name);
      Object previousValue = previousValues.get(name);
       
      if(previousValue == null)
      {
        previousValue = value;
        previousValues.put(name, value);
      }

      previousValues.put(name, value);
      
      if(value instanceof Integer)
        value = (Integer) value - (Integer) previousValue;
      else if(value instanceof Long)
        value = (Long) value - (Long) previousValue;
      else if(value instanceof Float)
        value = (Float) value - (Float) previousValue;
      else if(value instanceof Double)
        value = (Double) value - (Double) previousValue;

      target.put(name, value);
    });    
  }

  private Document mapFieldNames(Document stats)
  {
    Document mappedStats = new Document();
    
    for(Entry<String, Object> item: stats.entrySet())
    {
      Object value = item.getValue();

      if(value instanceof Document)
        mappedStats.put(item.getKey(), mapFieldNames((Document) value));
      else if(value instanceof Integer || value instanceof Long)
        mappedStats.put("l_" + item.getKey(), value);
      else if(value instanceof Float || value instanceof Double)
        mappedStats.put("d_" + item.getKey(), value);
      else if(value instanceof String)
        mappedStats.put("t_" + item.getKey(), value);
      else if(value instanceof Date)
        mappedStats.put("ts_" + item.getKey(), ((Date) value).getTime());
    }
    
    return mappedStats;
  }
  
  private void log(int level, String message)
  {
    LogService logService = this.logService.get();
    
    if(logService != null)
      logService.log(level, message);
  }

  private void log(int level, String message, Exception e)
  {
    LogService logService = this.logService.get();
    
    if(logService != null)
      logService.log(level, message, e);
  }
}
