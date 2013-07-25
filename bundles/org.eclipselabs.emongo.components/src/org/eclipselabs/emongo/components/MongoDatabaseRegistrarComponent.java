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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
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
	private Map<String, MongoClientProvider> mongoClientProvidersByClientId = new HashMap<String, MongoClientProvider>();
	private Map<String, Set<MongoAuthenticatedDatabaseConfigurationProvider>> databaseConfigurationProvidersByClientId = new HashMap<String, Set<MongoAuthenticatedDatabaseConfigurationProvider>>();
	private Map<MongoAuthenticatedDatabaseConfigurationProvider, ServiceRegistration<MongoDatabaseProvider>> serviceRegistrationsByDatabaseConfiguration = new ConcurrentHashMap<MongoAuthenticatedDatabaseConfigurationProvider, ServiceRegistration<MongoDatabaseProvider>>();

	public void activate(ComponentContext context)
	{
		this.context = context;

		for (Collection<MongoAuthenticatedDatabaseConfigurationProvider> providers : databaseConfigurationProvidersByClientId.values())
			for (MongoAuthenticatedDatabaseConfigurationProvider provider : providers)
				registerMongoDatabaseProvider(provider);
	}

	public void bindMongoDatabaseConfigurationProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		synchronized (databaseConfigurationProvidersByClientId)
		{
			Set<MongoAuthenticatedDatabaseConfigurationProvider> databaseConfigurationProviders = databaseConfigurationProvidersByClientId.get(databaseConfigurationProvider.getClientId());

			if (databaseConfigurationProviders == null)
			{
				databaseConfigurationProviders = new HashSet<MongoAuthenticatedDatabaseConfigurationProvider>();
				databaseConfigurationProvidersByClientId.put(databaseConfigurationProvider.getClientId(), databaseConfigurationProviders);
			}

			databaseConfigurationProviders.add(databaseConfigurationProvider);
		}

		if (context != null)
			registerMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void unbindMongoDatabaseConfigurationProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		synchronized (databaseConfigurationProvidersByClientId)
		{
			Set<MongoAuthenticatedDatabaseConfigurationProvider> databaseConfigurationProviders = databaseConfigurationProvidersByClientId.get(databaseConfigurationProvider.getAlias());

			if (databaseConfigurationProviders != null)
				databaseConfigurationProviders.remove(databaseConfigurationProvider);
		}

		unregisterMongoDatabaseProvider(databaseConfigurationProvider);
	}

	public void bindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		synchronized (mongoClientProvidersByClientId)
		{
			mongoClientProvidersByClientId.put(mongoClientProvider.getClientId(), mongoClientProvider);
		}

		if (context != null)
			registerMongoDatabaseProvider(mongoClientProvider);
	}

	public void unbindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		synchronized (mongoClientProvidersByClientId)
		{
			if (mongoClientProvidersByClientId.get(mongoClientProvider.getClientId()) == mongoClientProvider)
				mongoClientProvidersByClientId.remove(mongoClientProvider.getClientId());
		}

		unregisterMongoDatabaseProvider(mongoClientProvider);
	}

	private void registerMongoDatabaseProvider(MongoClientProvider mongoClientProvider)
	{
		synchronized (databaseConfigurationProvidersByClientId)
		{
			Set<MongoAuthenticatedDatabaseConfigurationProvider> databaseConfigurationProviders = databaseConfigurationProvidersByClientId.get(mongoClientProvider.getClientId());

			if (databaseConfigurationProviders != null)
			{
				for (MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider : databaseConfigurationProviders)
					registerMongoDatabaseProvider(mongoClientProvider, databaseConfigurationProvider);
			}
		}
	}

	private void registerMongoDatabaseProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		synchronized (mongoClientProvidersByClientId)
		{
			MongoClientProvider mongoClientProvider = mongoClientProvidersByClientId.get(databaseConfigurationProvider.getClientId());

			if (mongoClientProvider != null)
				registerMongoDatabaseProvider(mongoClientProvider, databaseConfigurationProvider);
		}
	}

	private void registerMongoDatabaseProvider(MongoClientProvider mongoClientProvider, MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		MongoDatabaseProviderComponent mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent(databaseConfigurationProvider, mongoClientProvider);
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MongoDatabaseProvider.PROP_ALIAS, databaseConfigurationProvider.getAlias());
		ServiceRegistration<MongoDatabaseProvider> serviceRegistration = context.getBundleContext().registerService(MongoDatabaseProvider.class, mongoDatabaseProviderComponent, properties);
		serviceRegistrationsByDatabaseConfiguration.put(databaseConfigurationProvider, serviceRegistration);
	}

	private void unregisterMongoDatabaseProvider(MongoClientProvider mongoClientProvider)
	{
		synchronized (databaseConfigurationProvidersByClientId)
		{
			Set<MongoAuthenticatedDatabaseConfigurationProvider> databaseConfigurationProviders = databaseConfigurationProvidersByClientId.get(mongoClientProvider.getClientId());

			if (databaseConfigurationProviders != null)
			{
				for (MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider : databaseConfigurationProviders)
					unregisterMongoDatabaseProvider(databaseConfigurationProvider);
			}
		}
	}

	private void unregisterMongoDatabaseProvider(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider)
	{
		ServiceRegistration<MongoDatabaseProvider> serviceRegistration = serviceRegistrationsByDatabaseConfiguration.remove(databaseConfigurationProvider);

		if (serviceRegistration != null)
			serviceRegistration.unregister();
	}
}
