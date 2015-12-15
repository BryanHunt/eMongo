package org.eclipselabs.emongo.metatype;

import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoIdFactory;

public interface MetaTypeConfiguration
{
  String PROP_ADMIN_PID = "metatype.pid=" + MongoAdmin.PID;
  String PROP_CLIENT_PID = "metatype.factory.pid=" + MongoClientProvider.PID;
  String PROP_ID_FACTORY_PID = "metatype.factory.pid=" + MongoIdFactory.PID;
  String PROP_MONITOR_PID = "metatype.factory.pid=" + MongoAdmin.MONITOR_PID;
}
