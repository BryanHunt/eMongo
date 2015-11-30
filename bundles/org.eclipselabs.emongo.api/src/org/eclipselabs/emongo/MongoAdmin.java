package org.eclipselabs.emongo;

import java.io.File;
import java.io.IOException;

public interface MongoAdmin
{
  boolean dumpDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
  boolean restoreDatabase(String host, String databaseName, File location) throws IOException, InterruptedException;
}
