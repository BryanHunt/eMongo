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

import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.DatabaseLocator;
import org.eclipselabs.emongo.MongoClientProvider;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public class DatabaseLocatorComponent implements DatabaseLocator
{
	private Map<String, MongoClientProvider> mongoProvidersByURI = new HashMap<String, MongoClientProvider>();
	private Map<String, DatabaseAuthenticationProvider> databaseAuthenticationProvidersByURI = new HashMap<String, DatabaseAuthenticationProvider>();
	private Map<String, DB> databasesByURI = new HashMap<String, DB>();

	@Override
	public DB getDatabase(String uri)
	{
		if (!uri.startsWith("mongodb://"))
			throw new IllegalArgumentException("URI: '" + uri + "' does not start with mongodb://");

		int dbStart = uri.indexOf('/', 10);
		int dbEnd = uri.indexOf('/', dbStart + 1);

		if (dbStart == -1)
			throw new IllegalArgumentException("URI: '" + uri + "' does not specify a database name");

		if (dbEnd == -1)
			dbEnd = uri.length();

		String clientURI = uri.substring(0, dbStart + 1);
		String dbURI = uri.substring(0, dbEnd);
		String databaseName = uri.substring(dbStart + 1);

		synchronized (databasesByURI)
		{
			DB database = databasesByURI.get(uri);

			if (database != null)
				return database;

			MongoClientProvider mongoProvider = null;

			synchronized (mongoProvidersByURI)
			{
				mongoProvider = mongoProvidersByURI.get(clientURI);
			}

			if (mongoProvider == null)
				return null;

			database = mongoProvider.getMongoClient().getDB(databaseName);
			databasesByURI.put(dbURI, database);

			synchronized (databaseAuthenticationProvidersByURI)
			{
				DatabaseAuthenticationProvider databaseAuthenticationProvider = databaseAuthenticationProvidersByURI.get(dbURI);

				if (databaseAuthenticationProvider != null)
					database.authenticate(databaseAuthenticationProvider.getUser(), databaseAuthenticationProvider.getPassword().toCharArray());
			}

			return database;
		}
	}

	public void bindDatabaseAuthenticationProvider(DatabaseAuthenticationProvider databaseAuthenticationProvider)
	{
		synchronized (databaseAuthenticationProvidersByURI)
		{
			databaseAuthenticationProvidersByURI.put(databaseAuthenticationProvider.getURI(), databaseAuthenticationProvider);
		}
	}

	public void unbindDatabaseAuthenticationProvider(DatabaseAuthenticationProvider databaseAuthenticationProvider)
	{
		synchronized (databaseAuthenticationProvidersByURI)
		{
			databaseAuthenticationProvidersByURI.remove(databaseAuthenticationProvider.getURI());
		}
	}

	public void bindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		synchronized (mongoProvidersByURI)
		{
			for (String uri : mongoProvider.getURIs())
				mongoProvidersByURI.put(uri, mongoProvider);
		}
	}

	public void unbindMongoClientProvider(MongoClientProvider mongoProvider)
	{
		synchronized (mongoProvidersByURI)
		{
			for (String uri : mongoProvider.getURIs())
				mongoProvidersByURI.remove(uri);
		}
	}
}
