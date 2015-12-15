package org.eclipselabs.emongo;

import java.io.File;
import java.io.IOException;

public interface MongoAdmin
{
  String PID = "org.eclipselabs.emongo.admin";
  String MONITOR_PID = "org.eclipselabs.emongo.monitor.databaseMonitor";
  
  boolean dumpDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
  boolean restoreDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
}
