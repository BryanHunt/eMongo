package org.eclipselabs.emongo.stats.example;

import org.bson.Document;
import org.eclipselabs.emongo.MongoServerStatsPublisher;
import org.osgi.service.component.annotations.Component;

@Component(service = MongoServerStatsPublisher.class)
public class ExampleServerStatsPublisherComponent implements MongoServerStatsPublisher
{
  @Override
  public void publishStats(Document stats)
  {
    Document memoryStats = (Document) stats.get("mem");
    System.out.println(stats.getDate("localTime") + " " + stats.getString("host") + " " + memoryStats.getInteger("resident") + " " + memoryStats.getInteger("virtual") + " " + memoryStats.getInteger("mapped"));
  }
}
