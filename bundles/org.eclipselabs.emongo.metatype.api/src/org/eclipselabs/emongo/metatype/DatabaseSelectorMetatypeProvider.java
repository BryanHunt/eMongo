package org.eclipselabs.emongo.metatype;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emongo.MongoProvider;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;

public abstract class DatabaseSelectorMetatypeProvider implements MetaTypeProvider
{
  Set<String> databases = new CopyOnWriteArraySet<String>();

  @Override
  public String[] getLocales()
  {
    return null;
  }

  protected AttributeDefinitionImpl createDatabaseSelector()
  {
    AttributeDefinitionImpl database = new AttributeDefinitionImpl("MongoProvider.target", "Database", AttributeDefinition.STRING);
    database.setDescription("The MongoDB database");

    String[] databaseAliases = new String[databases.size()];
    String[] targetFilters = new String[databases.size()];

    databases.toArray(databaseAliases);

    for (int i = 0; i < databaseAliases.length; i++)
      targetFilters[i] = "(" + MongoProvider.PROP_CLIENT_ID + "=" + databaseAliases[i] + ")";

    database.setOptionLabels(databaseAliases);
    database.setOptionValues(targetFilters);

    if (!databases.isEmpty())
      database.setDefaultValue(new String[] { databases.iterator().next() });

    return database;
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC )
  public void bindMongoProvider(ServiceReference<MongoProvider> serviceReference)
  {
    databases.add((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
  }

  public void unbindMongoProvider(ServiceReference<MongoProvider> serviceReference)
  {
    databases.remove((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
  }
}
