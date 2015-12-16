package org.eclipselabs.emongo.components;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.MongoServerStatsPublisher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = MongoAdmin.MONITOR_PID)
public class MongoDatabaseMonitorComponent implements Runnable
{
  public @interface MonitorConfig
  {
    int updateInterval() default 5;
  }

  public static final String PROP_UPDATE_INTERVAL = "updateInterval";

  public static final String PROP_DATABASE_FILTER = "MongoDatabaseProvider.target";

  private volatile MongoProvider mongoClientProvider;
  private volatile Collection<MongoServerStatsPublisher> serverStatsPublishers = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private int updateInterval;
  private volatile boolean done = false;
  private Thread thread;

  public static String validateUpdateInterval(String stringValue)
  {
    try
    {
      int value = Integer.parseInt(stringValue);
      
      if (value < 1)
        return "The update interval must be > 0";

      return null;
    } 
    catch (NumberFormatException e)
    {
      return "The update interval must be an integer > 0";
    }
  }

  @Activate
  public void activate(MonitorConfig config)
  {
    updateInterval = config.updateInterval() * 60 * 1000; // convert config value from minutes to milliseconds
    thread = new Thread(this);
    thread.start();
  }

  @Deactivate
  public void deactivate()
  {
    done = true;
    thread.interrupt();
  }

  @Override
  public void run()
  {
    while (!done)
    {
      Document result = mongoClientProvider.getMongoDatabase().runCommand(new Document("serverStatus", 1));
      serverStatsPublishers.forEach((publisher) -> {publisher.publishStats(result);});
      
      try
      {
        Thread.sleep(updateInterval);
      } catch (InterruptedException e)
      {}
    }
  }

  @Reference(unbind = "-")
  public void bindMongoClientProvider(MongoProvider mongoClientProvider)
  {
    this.mongoClientProvider = mongoClientProvider;
  }
  
  @Reference(cardinality = ReferenceCardinality.MULTIPLE)
  public void bindServerStatsPublisher(MongoServerStatsPublisher serverStatsPublisher)
  {
    serverStatsPublishers.add(serverStatsPublisher);
  }
  
  public void unbindServerStatsPublisher(MongoServerStatsPublisher serverStatsPublisher)
  {
    serverStatsPublishers.remove(serverStatsPublisher);
  }
}
