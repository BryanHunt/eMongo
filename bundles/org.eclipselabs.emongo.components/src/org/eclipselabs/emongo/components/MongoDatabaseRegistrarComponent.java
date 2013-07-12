/*******************************************************************************
 * Copyright (c) 2012 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.components;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

/**
 * @author bhunt
 * 
 */
public class MongoDatabaseRegistrarComponent
{
	private volatile ComponentContext context;
	private Map<String, MongoClientProvider> mongoClientProvidersByClientURI = new ConcurrentHashMap<String, MongoClientProvider>();
	private Map<String, MongoAuthenticatedDatabaseConfigurationProvider> databaseConfigurationProvidersByDatabaseURI = new ConcurrentHashMap<String, MongoAuthenticatedDatabaseConfigurationProvider>();
	private Map<String, ServiceRegistration<MongoDatabaseProvider>> serviceRegistrationsByDatabaseURI = new ConcurrentHashMap<String, ServiceRegistration<MongoDatabaseProvider>>();

	public void activate(ComponentContext context)
	{
		this.context = context;

		for (MongoAuthenticatedDatabaseConfigurationProvider provider : databaseConfigurationProvidersByDatabaseURI.values())
			registerMongoDatabaseProvider(provider);
	}

	public void bindMongoDatabaseConfigurationProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		databaseConfigurationProvidersByDatabaseURI.put(databaseConfigurationProvider.getURI(), databaseConfigurationProvider);

		if (context != null)
			registerMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void unbindMongoDatabaseConfigurationProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		databaseConfigurationProvidersByDatabaseURI.remove(databaseConfigurationProvider.getURI());
		unregisterMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void bindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		for (String clientURI : mongoProvider.getURIs())
			mongoClientProvidersByClientURI.put(clientURI, mongoProvider);

		if (context != null)
			registerMongoDatabaseProvider(mongoProvider);
	}

	public void unbindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		for (String clientURI : mongoProvider.getURIs())
			mongoClientProvidersByClientURI.remove(clientURI);

		unregisterMongoDatabaseProvider(mongoProvider);
	}

	private void registerMongoDatabaseProvider(MongoClientProvider mongoClientProvider)
	{
		for (Entry<String, MongoAuthenticatedDatabaseConfigurationProvider> entry : databaseConfigurationProvidersByDatabaseURI.entrySet())
		{
			for (String clientURI : mongoClientProvider.getURIs())
			{
				if (entry.getKey().startsWith(clientURI))
				{
					registerMongoDatabaseProvider(mongoClientProvider, entry.getValue());
					return;
				}
			}
		}
	}

	private void registerMongoDatabaseProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		int trimIndex = databaseConfigurationProvider.getURI().lastIndexOf("/");
		String clientURI = databaseConfigurationProvider.getURI().substring(0, trimIndex);
		MongoClientProvider mongoClientProvider = mongoClientProvidersByClientURI.get(clientURI);

		if (mongoClientProvider != null)
			registerMongoDatabaseProvider(mongoClientProvider, databaseConfigurationProvider);
	}

	private void registerMongoDatabaseProvider(MongoClientProvider mongoClientProvider, MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		MongoDatabaseProviderComponent mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent(databaseConfigurationProvider, mongoClientProvider);
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MongoDatabaseProvider.PROP_ALIAS, databaseConfigurationProvider.getAlias());
		ServiceRegistration<MongoDatabaseProvider> serviceRegistration = context.getBundleContext().registerService(MongoDatabaseProvider.class, mongoDatabaseProviderComponent, properties);
		serviceRegistrationsByDatabaseURI.put(databaseConfigurationProvider.getURI(), serviceRegistration);
	}

	private void unregisterMongoDatabaseProvider(MongoClientProvider mongoClientProvider)
	{
		for (String dbURI : databaseConfigurationProvidersByDatabaseURI.keySet())
		{
			for (String clientURI : mongoClientProvider.getURIs())
			{
				if (dbURI.startsWith(clientURI))
				{
					unregisterMongoDatabaseProvider(dbURI);
					return;
				}
			}
		}
	}

	private void unregisterMongoDatabaseProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		unregisterMongoDatabaseProvider(databaseConfigurationProvider.getURI());
	}

	private void unregisterMongoDatabaseProvider(String dbURI)
	{
		ServiceRegistration<MongoDatabaseProvider> serviceRegistration = serviceRegistrationsByDatabaseURI.remove(dbURI);

		if (serviceRegistration != null)
			serviceRegistration.unregister();
	}
}
