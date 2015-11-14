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

package org.eclipselabs.emongo.metatype;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.components.MongoDatabaseProviderComponent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {"metatype.factory.pid=org.eclipselabs.emongo.databaseProvider"})
public class MongoDatabaseMetaTypeProvider implements MetaTypeProvider
{
	private Set<String> mongoClientProviders = new CopyOnWriteArraySet<String>();

	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
	{
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl(MongoDatabaseProvider.PROP_CLIENT_FILTER, "Client", AttributeDefinition.STRING);
		clientId.setDescription("The MongoDB database client ID");

		String[] clients = new String[mongoClientProviders.size()];
		String[] targetFilters = new String[mongoClientProviders.size()];

		mongoClientProviders.toArray(clients);

		for (int i = 0; i < clients.length; i++)
			targetFilters[i] = "(" + MongoClientProvider.PROP_CLIENT_ID + "=" + clients[i] + ")";

		clientId.setOptionLabels(clients);
		clientId.setOptionValues(targetFilters);

		if (!mongoClientProviders.isEmpty())
			clientId.setDefaultValue(new String[] { mongoClientProviders.iterator().next() });

		AttributeDefinitionImpl alias = new AttributeDefinitionImpl(MongoDatabaseProvider.PROP_ALIAS, "Alias", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoDatabaseProviderComponent.validateAlias(value);
			}
		};

		alias.setDescription("The alias of the MongoDB database.");

		AttributeDefinitionImpl database = new AttributeDefinitionImpl(MongoDatabaseProvider.PROP_DATABASE, "Database", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoDatabaseProviderComponent.validateDatabaseName(value);
			}
		};

		database.setDescription("The name MongoDB database.");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(ConfigurationProperties.DATABASE_PID, "MongoDB Database", "MongoDB Database Configuration");
		ocd.addAttribute(clientId);
		ocd.addAttribute(alias);
		ocd.addAttribute(database);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	public void bindMongoClientProvider(ServiceReference<MongoClientProvider> serviceReference)
	{
		mongoClientProviders.add((String) serviceReference.getProperty(MongoClientProvider.PROP_CLIENT_ID));
	}

	public void unbindMongoClientProvider(ServiceReference<MongoClientProvider> serviceReference)
	{
		mongoClientProviders.remove((String) serviceReference.getProperty(MongoClientProvider.PROP_CLIENT_ID));
	}
}
