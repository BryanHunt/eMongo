package org.eclilpselabs.emongo.monitor.influxdb;

import java.io.IOException;
import java.util.Date;

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

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class InfluxPublisher implements MongoServerStatsPublisher
{
  public @interface PublisherConfig
  {
    String uri();
  }
  
  private CloseableHttpClient client;
  private HttpPost request;
  
  @Activate
  public void activate(PublisherConfig config)
  {
    client = HttpClients.createDefault();
    request = new HttpPost(config.uri());
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
    StringBuilder metrics = new StringBuilder();

    createMetrics(metrics, "", stats, System.currentTimeMillis());
    EntityBuilder builder = EntityBuilder.create();
    builder.setText(metrics.toString());
    request.setEntity(builder.build());

    try(CloseableHttpResponse response = client.execute(request))
    {} 
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void createMetrics(StringBuilder metrics, String hierarchyName, Document stats, long timestamp)
  {
    for(String key : stats.keySet())
      createMetrics(metrics, hierarchyName, key, stats, timestamp);
  }
  
  private void createMetrics(StringBuilder metrics, String hierarchyName, String name, Document stats, long timestamp)
  {
    Object target = stats.get(name);
    
    if(target instanceof Date)
      return;
    
    if(target instanceof Document)
    {
      String nextHierarchy = hierarchyName.isEmpty() ? name :  hierarchyName + "_" + name; 
      createMetrics(metrics, nextHierarchy, (Document) target, timestamp);
    }
    else
    {
      String metricName = hierarchyName.isEmpty() ? name :  hierarchyName + "_" + name;
      createMetric(metrics, metricName, target, timestamp);
    }
  }
  
  private void createMetric(StringBuilder buffer, String name, Object value, long timestamp)
  {
    if(value == null)
      return;
    
    if(buffer.length() > 0)
      buffer.append('\n');
    
    buffer.append(name);
    buffer.append(' ');
    buffer.append("value=");
    
    if(value instanceof String)
      buffer.append('"');
    
    buffer.append(value);
    
    if(value instanceof String)
      buffer.append('"');
    else if(value instanceof Integer || value instanceof Long)
      buffer.append('i');
    
    buffer.append(' ');
    buffer.append(timestamp * 1000);    
  }
}
