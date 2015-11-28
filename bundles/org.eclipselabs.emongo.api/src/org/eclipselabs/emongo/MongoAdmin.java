package org.eclipselabs.emongo;

import java.io.File;

public interface MongoAdmin
{
  void dumpDatabase(String databaseName, File location);
  void restoreDatabase(String databaseName, File location);
}
