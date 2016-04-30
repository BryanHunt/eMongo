package org.eclipselabs.emongo.metatype.components;

import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.MongoIdFactory;

public interface MetaTypeConfiguration
{
  String PROP_ADMIN_PID = "metatype.pid=" + MongoAdmin.PID;
  String PROP_CLIENT_PID = "metatype.factory.pid=" + MongoProvider.PID;
  String PROP_ID_FACTORY_PID = "metatype.factory.pid=" + MongoIdFactory.PID;
  String PROP_MONITOR_PID = "metatype.factory.pid=" + MongoAdmin.MONITOR_PID;
}
