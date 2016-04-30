package org.eclilpselabs.emongo.monitor.influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class InfluxPublisher implements MongoServerStatsPublisher
{
  public @interface PublisherConfig
  {
    String uri();
    int timeout() default 5000;
  }
  
  private CloseableHttpClient client;
  private HttpPost request;
  private Map<String, Map<String, Object>> previousValuesByHost = new HashMap<>();
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
    
    statsThatNeedDeltas.add("extra_info_page_faults");
    statsThatNeedDeltas.add("network_bytesIn");
    statsThatNeedDeltas.add("network_bytesOut");
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
    log(LogService.LOG_DEBUG, "Publishing MongoDB stats to InfluxDB: '" + request.getURI() + "'");
    log(LogService.LOG_DEBUG, stats.toJson());
    StringBuilder metrics = new StringBuilder();

    String host = stats.getString("host");
    
   createMetrics(metrics, "", host, stats, System.currentTimeMillis() * 1000);
    log(LogService.LOG_DEBUG, metrics.toString());

    EntityBuilder builder = EntityBuilder.create();
    builder.setText(metrics.toString());
    request.setEntity(builder.build());

    try(CloseableHttpResponse response = client.execute(request))
    {
      log(LogService.LOG_DEBUG, "Response from publish: " + response.getStatusLine().getStatusCode() + " '" + response.getStatusLine().getReasonPhrase() + "'");
      
      if(response.getStatusLine().getStatusCode() >= 400)
      {
        StringBuilder reason = new StringBuilder();
        
        try(BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())))
        {
          String line = null;
          
          while((line = in.readLine()) != null)
            reason.append(line);
        }
        
        log(LogService.LOG_WARNING, "Failed to publish: " + response.getStatusLine().getStatusCode() + " '" + response.getStatusLine().getReasonPhrase() + "'\n " + reason.toString());
        
        if(response.getStatusLine().getStatusCode() == 400)
          log(LogService.LOG_WARNING, "Influx did not like the format of the following stats:\n" + metrics.toString());
      }
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
  
  private void createMetrics(StringBuilder metrics, String hierarchyName, String host, Document stats, long timestamp)
  {
    for(String key : stats.keySet())
      createMetrics(metrics, hierarchyName, key, host, stats, timestamp);
  }
  
  private void createMetrics(StringBuilder metrics, String hierarchyName, String name, String host, Document stats, long timestamp)
  {
    Object target = stats.get(name);
    
    if(target instanceof Date)
      return;
    
    if(target instanceof Document)
    {
      String nextHierarchy = hierarchyName.isEmpty() ? name :  hierarchyName + "_" + name; 
      createMetrics(metrics, nextHierarchy, host, (Document) target, timestamp);
    }
    else
    {
      String metricName = hierarchyName.isEmpty() ? name :  hierarchyName + "_" + name;
      createMetric(metrics, metricName, host, target, timestamp);
    }
  }
  
  private void createMetric(StringBuilder buffer, String name, String host, Object value, long timestamp)
  {
    if(value == null)
      return;
    
    if(value instanceof Collection)
      return;

    boolean treatAsString = !(value instanceof Integer) && !(value instanceof Boolean) && !(value instanceof Long) && !(value instanceof Float) && !(value instanceof Double);
    
    if(buffer.length() > 0)
      buffer.append('\n');
    
    buffer.append(name);
    buffer.append(",host=");
    buffer.append(host);
    buffer.append(' ');
    buffer.append("value=");
    
    if(treatAsString) 
      buffer.append('"');
    
    if(statsThatNeedDeltas.contains(name))
    {
      Map<String, Object> previousValues = previousValuesByHost.get(host);
      
      if(previousValues == null)
      {
        previousValues = new HashMap<>();
        previousValuesByHost.put(host, previousValues);
      }
      
      Object previousValue = previousValues.get(name);
      
      if(previousValue == null)
        previousValue = value;

      previousValues.put(name, value);
      
      if(value instanceof Integer)
        value = (Integer) value - (Integer) previousValue;
      else if(value instanceof Long)
        value = (Long) value - (Long) previousValue;
      else if(value instanceof Float)
        value = (Float) value - (Float) previousValue;
      else if(value instanceof Double)
        value = (Double) value - (Double) previousValue;
    }
    
    buffer.append(value);
    
    if(treatAsString)
      buffer.append('"');
    else if(value instanceof Integer || value instanceof Long)
      buffer.append('i');
    
    buffer.append(' ');
    buffer.append(timestamp * 1000);    
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
