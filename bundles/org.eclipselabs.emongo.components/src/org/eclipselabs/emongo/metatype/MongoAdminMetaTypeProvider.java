package org.eclipselabs.emongo.metatype;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

@Component(service = MetaTypeProvider.class, property = {"metatype.pid=org.eclipselabs.emongo.admin"})
public class MongoAdminMetaTypeProvider implements MetaTypeProvider
{
  @Override
  public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
  {
    AttributeDefinitionImpl mongodbBinDir = new AttributeDefinitionImpl("mongodbBinDir", "MongoDB Location", AttributeDefinition.STRING);
    mongodbBinDir.setDescription("The install location (bin directory) for MongoDB");
    
    ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(ConfigurationProperties.ADMIN_PID, "MongoDB Admin", "MongoDB Admin Configuration");
    ocd.addRequiredAttribute(mongodbBinDir);
    
    return ocd;
  }

  @Override
  public String[] getLocales()
  {
    return null;
  }
}
