package org.eclipselabs.emongo;

import java.io.File;
import java.io.IOException;

/**
 * This OSGi service provides administrative capabilities for managing MongoDB
 * 
 * @author bhunt
 * 
 */
public interface MongoAdmin
{
  /**
   * The OSGi configuration PID for the admin component
   */
  String PID = "org.eclipselabs.emongo.admin";
  
  /**
   * The OSGi configuration PID for the database monitor component
   */
  String MONITOR_PID = "org.eclipselabs.emongo.monitor.databaseMonitor";
  
  /**
   * Dumps the specified database using mongodump
   * 
   * @param host the MongoDB host name
   * @param databaseName the database to dump
   * @param location the directory to write the dump
   * @return true if the dump was successful
   * @throws IOException if there was a problem creating the dump
   * @throws InterruptedException if the dump process was interrupted
   */
  boolean dumpDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
  
  /**
   * Restores the specified database using mongorestore
   * 
   * @param host the MongoDB host name
   * @param databaseName the database to restore
   * @param location the directory containing the dump to restore
   * @return true if the restore was successful
   * @throws IOException if there was a problem restoring
   * @throws InterruptedException if the restore process was interrupted
   */
  boolean restoreDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
}
