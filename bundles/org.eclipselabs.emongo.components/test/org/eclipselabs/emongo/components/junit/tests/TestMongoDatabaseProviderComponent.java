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
import org.eclipselabs.emongo.components.MongoDatabaseProviderComponent.DatabaseConfig;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@SuppressWarnings("restriction")
public class TestMongoDatabaseProviderComponent
{
	private Map<String, Object> properties;
	private String databaseName;
	private String alias;
	private String[] clientURIs;

	private MongoDatabaseProviderComponent mongoDatabaseProviderComponent;
	private MongoClientProvider mongoClientProvider;

	@Before
	public void setUp()
	{
		databaseName = "junit";
		alias = "alias";

		properties = new HashMap<String, Object>();
		properties.put(MongoDatabaseProvider.PROP_DATABASE, databaseName);
		properties.put(MongoDatabaseProvider.PROP_ALIAS, alias);

		clientURIs = new String[] { "mongodb://localhost" };

		mongoClientProvider = mock(MongoClientProvider.class);
		when(mongoClientProvider.getURIs()).thenReturn(clientURIs);
		mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent();
		mongoDatabaseProviderComponent.bindMongoClientProvider(mongoClientProvider);
	}

	@Test
	public void testActivate() throws Exception
	{
		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));
		assertThat(mongoDatabaseProviderComponent.getURI(), is(clientURIs[0] + "/" + databaseName));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullAlias() throws Exception
	{
		properties.put(MongoDatabaseProvider.PROP_ALIAS, null);
		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyAlias() throws Exception
	{
		properties.put(MongoDatabaseProvider.PROP_ALIAS, "");
		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullDatabaseName() throws Exception
	{
		properties.put(MongoDatabaseProvider.PROP_DATABASE, null);
		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyDatabaseName() throws Exception
	{
		properties.put(MongoDatabaseProvider.PROP_DATABASE, "");
		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));
	}

	@Test
	public void testGetDB() throws Exception
	{
		String databaseName = "junit";
		MongoDatabase db = mock(MongoDatabase.class);
		MongoClient mongoClient = mock(MongoClient.class);

		when(mongoClientProvider.getMongoClient()).thenReturn(mongoClient);
		when(mongoClient.getDatabase(databaseName)).thenReturn(db);

		mongoDatabaseProviderComponent.activate(aQute.lib.converter.Converter.cnv(DatabaseConfig.class, properties));

		assertThat(mongoDatabaseProviderComponent.getDatabase(), is(sameInstance(db)));
		verify(mongoClientProvider).getMongoClient();
		verify(mongoClient).getDatabase(databaseName);
	}
}
