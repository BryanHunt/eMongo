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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipselabs.emongo.DatabaseLocator;
import org.eclipselabs.emongo.MongoClientProvider;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public class DatabaseLocatorComponent implements DatabaseLocator
{
	private Map<String, MongoClientProvider> mongoProvidersByURI = new ConcurrentHashMap<String, MongoClientProvider>();
	private Map<String, DatabaseAuthenticationProvider> databaseAuthenticationProvidersByURI = new ConcurrentHashMap<String, DatabaseAuthenticationProvider>();
	private Map<String, DB> databasesByURI = new ConcurrentHashMap<String, DB>();

	@Override
	public DB getDatabase(String uri)
	{
		return waitForDatabase(uri, 0);
	}

	@Override
	public DB waitForDatabase(String uri, long timeout)
	{
		if (!uri.startsWith("mongodb://"))
			throw new IllegalArgumentException("URI: '" + uri + "' does not start with mongodb://");

		int dbStart = uri.indexOf('/', 10) + 1;

		if (dbStart == 0)
			throw new IllegalArgumentException("URI: '" + uri + "' does not specify a database name");

		int dbEnd = uri.indexOf('/', dbStart);

		if (dbEnd == -1)
			dbEnd = uri.length();

		String clientURI = uri.substring(0, dbStart - 1);
		String dbURI = uri.substring(0, dbEnd);
		String databaseName = uri.substring(dbStart);

		DB database = databasesByURI.get(dbURI);

		if (database != null)
			return database;

		return createDatabase(dbURI, clientURI, databaseName, timeout);
	}

	public void bindDatabaseAuthenticationProvider(DatabaseAuthenticationProvider databaseAuthenticationProvider)
	{
		databaseAuthenticationProvidersByURI.put(databaseAuthenticationProvider.getURI(), databaseAuthenticationProvider);
	}

	public void unbindDatabaseAuthenticationProvider(DatabaseAuthenticationProvider databaseAuthenticationProvider)
	{
		databaseAuthenticationProvidersByURI.remove(databaseAuthenticationProvider.getURI());
	}

	public void bindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		for (String uri : mongoProvider.getURIs())
			mongoProvidersByURI.put(uri, mongoProvider);
	}

	public void unbindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		for (String uri : mongoProvider.getURIs())
			mongoProvidersByURI.remove(uri);
	}

	protected DB createDatabase(String dbURI, String clientURI, String databaseName, long timeout)
	{
		MongoClientProvider mongoProvider = waitForMongoClientProvider(clientURI, timeout);

		if (mongoProvider == null)
			return null;

		synchronized (mongoProvider)
		{
			DB database = databasesByURI.get(dbURI);

			if (database != null)
				return database;

			database = mongoProvider.getMongoClient().getDB(databaseName);
			databasesByURI.put(dbURI, database);

			DatabaseAuthenticationProvider databaseAuthenticationProvider = databaseAuthenticationProvidersByURI.get(dbURI);

			if (databaseAuthenticationProvider != null)
				database.authenticate(databaseAuthenticationProvider.getUser(), databaseAuthenticationProvider.getPassword().toCharArray());

			return database;
		}
	}

	protected MongoClientProvider waitForMongoClientProvider(String clientURI, long timeout)
	{
		MongoClientProvider mongoClientProvider;

		do
		{
			mongoClientProvider = mongoProvidersByURI.get(clientURI);
			timeout -= 10;

			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{}
		}
		while (mongoClientProvider == null && timeout > 0);

		return mongoClientProvider;
	}
}
