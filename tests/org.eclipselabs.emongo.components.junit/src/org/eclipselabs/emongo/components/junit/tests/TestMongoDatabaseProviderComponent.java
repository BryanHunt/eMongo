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

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.components.MongoAuthenticatedDatabaseConfigurationProvider;
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
	private MongoDatabaseProviderComponent mongoDatabaseProviderComponent;
	private MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider;
	private MongoClientProvider mongoClientProvider;

	@Before
	public void setUp()
	{
		databaseConfigurationProvider = mock(MongoAuthenticatedDatabaseConfigurationProvider.class);
		mongoClientProvider = mock(MongoClientProvider.class);
		mongoDatabaseProviderComponent = new MongoDatabaseProviderComponent(databaseConfigurationProvider, mongoClientProvider);
	}

	@Test
	public void testGetAlias()
	{
		String alias = "junit";

		when(databaseConfigurationProvider.getAlias()).thenReturn(alias);

		assertThat(mongoDatabaseProviderComponent.getAlias(), is(alias));
	}

	public void testGetDB()
	{
		String databaseName = "junit";
		DB db = mock(DB.class);
		MongoClient mongoClient = mock(MongoClient.class);

		when(databaseConfigurationProvider.getDatabaseName()).thenReturn(databaseName);
		when(mongoClientProvider.getMongoClient()).thenReturn(mongoClient);
		when(mongoClient.getDB(databaseName)).thenReturn(db);

		assertThat(mongoDatabaseProviderComponent.getDB(), is(sameInstance(db)));
		verify(databaseConfigurationProvider).getDatabaseName();
		verify(mongoClientProvider).getMongoClient();
		verify(mongoClient).getDB(databaseName);
	}
}
