package org.eclilpselabs.emongo.monitor.influxdb;

import java.io.IOException;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.eclipselabs.emongo.MongoServerStatsPublisher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class InfluxPublisher implements MongoServerStatsPublisher
{
  private CloseableHttpClient client;
  private HttpPost request;

  @Activate
  public void activate()
  {
    client = HttpClients.createDefault();
    request = new HttpPost("http://localhost:8086/write?db=mongodb");
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
    Document memStats = (Document) stats.get("mem");
    StringBuilder data = new StringBuilder();
    data.append("virtual_memory");
    data.append(' ');
    data.append("value=");
    data.append(memStats.get("virtual"));
    data.append(' ');
    data.append(System.currentTimeMillis() * 1000);
    
    System.out.println("influx: " + data.toString());
    EntityBuilder builder = EntityBuilder.create();
    builder.setText(data.toString());
    request.setEntity(builder.build());

    try
    {
      CloseableHttpResponse response = client.execute(request);
      response.close();
    } 
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
