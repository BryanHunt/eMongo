/*******************************************************************************
 * Copyright (c) 2013 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.log.meta;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emeta.AttributeDefinitionImpl;
import org.eclipselabs.emeta.IntegerAttributeDefinitionImpl;
import org.eclipselabs.emeta.ObjectClassDefinitionImpl;
import org.eclipselabs.emongo.client.MongoProvider;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {"metatype.factory.pid=org.eclipselabs.emongo.log"})
public class MongoLogMetaTypeProvider implements MetaTypeProvider
{
  private static final String PROP_DATABASE_FILTER = "MongoDatabaseProvider.target";
  private static final String PROP_COLLECTION = "collection";
  private static final String PROP_MAX_LEVEL = "maxLevel";

	private Set<String> mongoDatabaseProviders = new CopyOnWriteArraySet<String>();

	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
	{
		AttributeDefinitionImpl databaseAlias = new AttributeDefinitionImpl(PROP_DATABASE_FILTER, "Database", AttributeDefinition.STRING);
		databaseAlias.setDescription("The MongoDB database ID");

		String[] databases = new String[mongoDatabaseProviders.size()];
		String[] targetFilters = new String[mongoDatabaseProviders.size()];

		mongoDatabaseProviders.toArray(databases);

		for (int i = 0; i < databases.length; i++)
			targetFilters[i] = "(" + MongoProvider.PROP_CLIENT_ID + "=" + databases[i] + ")";

		databaseAlias.setOptionLabels(databases);
		databaseAlias.setOptionValues(targetFilters);

		if (!mongoDatabaseProviders.isEmpty())
			databaseAlias.setDefaultValue(new String[] { mongoDatabaseProviders.iterator().next() });

		AttributeDefinitionImpl collection = new AttributeDefinitionImpl(PROP_COLLECTION, "Collection", AttributeDefinition.STRING);
		collection.setDescription("The collection name");
		collection.setDefaultValue(new String[] { "log" });

		IntegerAttributeDefinitionImpl maxLevel = new IntegerAttributeDefinitionImpl(PROP_MAX_LEVEL, "Log Level");
		maxLevel.setDescription("The maximum log level to store in the database");
		maxLevel.setOptionLabels(new String[] { "Error", "Warning", "Info", "Debug" });
		maxLevel.setOptionValues(new String[] { "1", "2", "3", "4" });
		maxLevel.setDefaultValue(new String[] { "1" });

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl("org.eclipselabs.emongo.log", "MongoDB Loging", "MongoDB Log Listener Configuration");
		ocd.addRequiredAttribute(databaseAlias);
		ocd.addRequiredAttribute(collection);
		ocd.addRequiredAttribute(maxLevel);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void bindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		mongoDatabaseProviders.add((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
	}

	public void unbindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		mongoDatabaseProviders.remove((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
	}
}
