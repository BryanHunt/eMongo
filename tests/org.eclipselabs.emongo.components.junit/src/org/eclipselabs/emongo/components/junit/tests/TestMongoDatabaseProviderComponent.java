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

package org.eclipselabs.emongo.components.junit.tests;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.components.MongoDatabaseProviderComponent;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */
public class TestMongoDatabaseProviderComponent
{
	private Map<String, Object> properties;
	private String databaseName;
	private String alias;
	private String user;
	private String password;
	private String[] clientURIs;

	private MongoDatabaseProviderComponent mongoDatabaseProviderComponent;
	private MongoClientProvider mongoClientProvider;

	@Before
	public void setUp()
	{
		databaseName = "junit";
		alias = "alias";
		user = "user";
		password = "password";

		properties = new HashMap<String, Object>();
		properties.put(MongoDatabaseProvider.PROP_DATABASE, databaseName);
		properties.put(MongoDatabaseProvider.PROP_ALIAS, alias);
		properties.put(MongoDatabaseProvider.PROP_USER, user);
		properties.put(MongoDatabaseProvider.PROP_PASSWORD, password);

		clientURIs = new String[] { "mongodb://localhost" };

		mongoClientProvider = mock(MongoClientProvider.class);
		when(mongoClientProvider.getURIs()).thenReturn(clientURIs);
		mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent();
		mongoDatabaseProviderComponent.bindMongoClientProvider(mongoClientProvider);
	}

	@Test
	public void testActivate()
	{
		mongoDatabaseProviderComponent.activate(properties);
		assertThat(mongoDatabaseProviderComponent.getURI(), is(clientURIs[0] + "/" + databaseName));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullAlias()
	{
		properties.put(MongoDatabaseProvider.PROP_ALIAS, null);
		mongoDatabaseProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyAlias()
	{
		properties.put(MongoDatabaseProvider.PROP_ALIAS, "");
		mongoDatabaseProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullDatabaseName()
	{
		properties.put(MongoDatabaseProvider.PROP_DATABASE, null);
		mongoDatabaseProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyDatabaseName()
	{
		properties.put(MongoDatabaseProvider.PROP_DATABASE, "");
		mongoDatabaseProviderComponent.activate(properties);
	}

	@Test
	public void testGetDB()
	{
		String databaseName = "junit";
		DB db = mock(DB.class);
		MongoClient mongoClient = mock(MongoClient.class);

		when(mongoClientProvider.getMongoClient()).thenReturn(mongoClient);
		when(mongoClient.getDB(databaseName)).thenReturn(db);

		mongoDatabaseProviderComponent.activate(properties);

		assertThat(mongoDatabaseProviderComponent.getDB(), is(sameInstance(db)));
		verify(mongoClientProvider).getMongoClient();
		verify(mongoClient).getDB(databaseName);
	}
}
