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
	private Map<String, DatabaseConfigurationProvider> databaseConfigurationProvidersByDatabaseURI = new ConcurrentHashMap<String, DatabaseConfigurationProvider>();
	private Map<String, ServiceRegistration<MongoDatabaseProvider>> serviceRegistrationsByDatabaseURI = new ConcurrentHashMap<String, ServiceRegistration<MongoDatabaseProvider>>();

	public void activate(ComponentContext context)
	{
		this.context = context;
	}

	public void bindDatabaseConfigurationProvider(DatabaseConfigurationProvider databaseConfigurationProvider)
	{
		databaseConfigurationProvidersByDatabaseURI.put(databaseConfigurationProvider.getURI(), databaseConfigurationProvider);
		registerMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void unbindDatabaseConfigurationProvider(DatabaseConfigurationProvider databaseConfigurationProvider)
	{
		databaseConfigurationProvidersByDatabaseURI.remove(databaseConfigurationProvider.getURI());
		unregisterMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void bindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		for (String clientURI : mongoProvider.getURIs())
			mongoClientProvidersByClientURI.put(clientURI, mongoProvider);

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
		for (Entry<String, DatabaseConfigurationProvider> entry : databaseConfigurationProvidersByDatabaseURI.entrySet())
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

	private void registerMongoDatabaseProvider(DatabaseConfigurationProvider databaseConfigurationProvider)
	{
		int trimIndex = databaseConfigurationProvider.getURI().lastIndexOf("/");
		String clientURI = databaseConfigurationProvider.getURI().substring(0, trimIndex);
		MongoClientProvider mongoClientProvider = mongoClientProvidersByClientURI.get(clientURI);

		if (mongoClientProvider != null)
			registerMongoDatabaseProvider(mongoClientProvider, databaseConfigurationProvider);
	}

	private void registerMongoDatabaseProvider(MongoClientProvider mongoClientProvider, DatabaseConfigurationProvider databaseConfigurationProvider)
	{
		MongoDatabaseProviderComponent mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent(databaseConfigurationProvider, mongoClientProvider);
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
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

	private void unregisterMongoDatabaseProvider(DatabaseConfigurationProvider databaseConfigurationProvider)
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
