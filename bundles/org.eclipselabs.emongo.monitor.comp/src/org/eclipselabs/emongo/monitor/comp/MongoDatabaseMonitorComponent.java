package org.eclipselabs.emongo.monitor.comp;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.MongoServerStatsPublisher;
import org.eclipselabs.emongo.admin.MongoAdmin;
import org.eclipselabs.emongo.comp.AbstractComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = MongoAdmin.MONITOR_PID)
public class MongoDatabaseMonitorComponent extends AbstractComponent implements Runnable
{
  public @interface MonitorConfig
  {
    int updateInterval() default 5;
  }

  public static final String PROP_UPDATE_INTERVAL = "updateInterval";
  public static final String PROP_DATABASE_FILTER = "MongoDatabaseProvider.target";

  private volatile boolean done = false;
  private volatile Collection<MongoProvider> mongoClientProviders = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private Collection<MongoServerStatsPublisher> serverStatsPublishers = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private int updateInterval;
  private Thread thread;

  @Activate
  public void activate(MonitorConfig config)
  {
    updateInterval = config.updateInterval() * 60 * 1000; // convert config value from minutes to milliseconds
    thread = new Thread(this, "MongoDB Monitor");
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
    log(LogService.LOG_INFO, "MongoDB stats monitor is starting");

    while (!done)
    {
      for (MongoProvider mongoClientProvider : mongoClientProviders)
      {
        try
        {
          Document result = mongoClientProvider.getMongoDatabase().runCommand(new Document("serverStatus", 1));

          serverStatsPublishers.forEach((publisher) -> {
            try
            {
              publisher.publishStats(result);
            }
            catch (Exception e)
            {
              log(LogService.LOG_WARNING, "MongoDB stats publisher threw unexpected exception", e);
            }
          });
        }
        catch (Exception e)
        {
          log(LogService.LOG_WARNING, "MongoDB stats monitor caught unexpected exception", e);
        }
      }
      try
      {
        Thread.sleep(updateInterval);
      }
      catch (InterruptedException e)
      {}
    }

    log(LogService.LOG_INFO, "MongoDB stats monitor is terminating");
  }

  @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC, target = "(clientId=monitor)")
  public void bindMongoClientProvider(MongoProvider mongoClientProvider)
  {
    mongoClientProviders.add(mongoClientProvider);
  }

  public void unbindMongoClientProvider(MongoProvider mongoClientProvider)
  {
    mongoClientProviders.remove(mongoClientProvider);
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void bindServerStatsPublisher(MongoServerStatsPublisher serverStatsPublisher)
  {
    serverStatsPublishers.add(serverStatsPublisher);
  }

  public void unbindServerStatsPublisher(MongoServerStatsPublisher serverStatsPublisher)
  {
    serverStatsPublishers.remove(serverStatsPublisher);
  }

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  public void bindLogService(LogService logService)
  {
    super.bindLogService(logService);
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }
}
