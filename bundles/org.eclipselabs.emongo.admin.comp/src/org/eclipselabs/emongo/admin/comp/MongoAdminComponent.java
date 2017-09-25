package org.eclipselabs.emongo.admin.comp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipselabs.emongo.admin.MongoAdmin;
import org.eclipselabs.emongo.comp.AbstractComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = MongoAdmin.class, configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = MongoAdmin.PID)
public class MongoAdminComponent extends AbstractComponent implements MongoAdmin
{
  private File mongodump;
  private File mongorestore;
  
  public @interface Config
  {
    String mongodbBinDir() default "";
  }
  
  @Activate
  public void activate(Config config)
  {
    mongodump = new File(config.mongodbBinDir(), "mongodump");
    mongorestore = new File(config.mongodbBinDir(), "mongorestore");
  }
  
  @Override
  public boolean dumpDatabase(String host, String databaseName, File location) throws IOException, InterruptedException
  {
    List<String> command = new ArrayList<>();
    command.add(mongodump.getAbsolutePath());
    command.add("-h");
    command.add(host);
    command.add("-d");
    command.add(databaseName);
    command.add("-o");
    command.add(location.getAbsolutePath());

    Process process = new ProcessBuilder(command).start();
    return process.waitFor() == 0;
  }

  @Override
  public boolean restoreDatabase(String host, String databaseName, File location) throws IOException, InterruptedException
  {
    List<String> command = new ArrayList<>();
    command.add(mongorestore.getAbsolutePath());
    command.add("-h");
    command.add(host);
    command.add("-d");
    command.add(databaseName);
    command.add(new File(location, databaseName).getAbsolutePath());

    Process process = new ProcessBuilder(command).start();
    return process.waitFor() == 0;    
  }
}
