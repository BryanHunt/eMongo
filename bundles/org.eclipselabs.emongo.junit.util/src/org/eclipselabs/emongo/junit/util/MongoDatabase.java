/*******************************************************************************
 * Copyright (c) 2011 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.junit.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.eunit.junit.utils.ServiceLocator;

import com.mongodb.DB;

/**
 * This class is intended to be used as a JUnit @Rule. It will verify that
 * the IMongoDB service exists, and it will delete all collections in the specified
 * database after each test runs.
 * 
 * Example usage: <br>
 * <br>
 * 
 * <pre>
 * &#064;Rule
 * public MongoDatabase database = new MongoDatabase();
 * </pre>
 * 
 * @author bhunt
 * 
 */
public class MongoDatabase extends ServiceLocator<MongoDatabaseProvider>
{
	private DB db;
	private String baseURI;

	/**
	 * Connects to the "junit" database on localhost:27017
	 */
	public MongoDatabase()
	{
		this("junit");
	}

	/**
	 * Connects to the specified database on localhost:27017
	 * 
	 * @param database the name of the database to use for unit testing
	 */
	public MongoDatabase(String database)
	{
		this("localhost", database);
	}

	/**
	 * Connects to the specified database on the specified host using the default port 27017
	 * 
	 * @param hostname the host running MongoDB
	 * @param database the name of the database to use for unit testing
	 */
	public MongoDatabase(String hostname, String database)
	{
		this(hostname, 27017, database, null);
	}

	/**
	 * Connects to the specified database on the specified host and port
	 * 
	 * @param hostname the host running MongoDB
	 * @param port the port MongoDB is listening on
	 * @param database the name of the database to use for unit testing
	 * @param alias the alias configured on the MongoDB provider service
	 */
	public MongoDatabase(String hostname, int port, String database, String alias)
	{
		super(MongoDatabaseProvider.class, (alias != null ? "(alias=" + alias + ")" : null));
		baseURI = "mongodb://" + hostname + (port == 27017 ? "" : ":" + port) + "/" + database;
	}

	/**
	 * Creates a URI for accessing a collection using the database connection as a base URI
	 * and appending the collection as the next segment.
	 * 
	 * @param collection the collection name
	 * @return a URI referencing a MongoDB collection
	 */
	public URI createCollectionURI(String collection)
	{
		try
		{
			return new URI(baseURI.toString() + "/" + collection + "/");
		}
		catch (URISyntaxException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Creates a URI for accessing an object using the database connection as a base URI
	 * and appending the collection and object id as the next segments.
	 * 
	 * @param collection
	 * @param id
	 * @return
	 */
	public URI createObjectURI(String collection, String id)
	{
		try
		{
			return new URI(baseURI + "/" + collection + "/" + id);
		}
		catch (URISyntaxException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Provides access to the MongoDB driver for the database this instance is connected to.
	 * 
	 * @return the low-level MongoDB driver interface
	 */
	public DB getMongoDB()
	{
		return db;
	}

	@Override
	protected void before() throws Throwable
	{
		super.before();
		db = getService().getDB();
		assertThat("No database configured for: " + baseURI, db, is(notNullValue()));
	}

	@Override
	protected void after()
	{
		if (db != null)
		{
			try
			{
				for (String collectionName : db.getCollectionNames())
				{
					if (!"system.indexes".equals(collectionName))
						db.getCollection(collectionName).drop();
				}
			}
			catch (Exception e)
			{
				fail("Failed to clean up database: " + baseURI + "\n\n" + e.getMessage());
			}
		}

		super.after();
	}
}
