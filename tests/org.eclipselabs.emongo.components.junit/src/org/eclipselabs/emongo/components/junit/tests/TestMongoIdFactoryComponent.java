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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.MongoIdFactory;
import org.eclipselabs.emongo.components.MongoIdFactoryComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author bhunt
 * 
 */
public class TestMongoIdFactoryComponent
{
	private MongoDatabaseProvider mongoDatabaseProvider;
	private MongoIdFactoryComponent mongoIdFactoryComponent;
	private Map<String, Object> properties;
	private String alias;
	private DB db;
	private DBCollection collection;
	private CommandResult commandResult;
	private String uri;

	@Before
	public void setUp()
	{
		alias = "junit";
		uri = "mongodb://localhost/junit/elements";
		properties = new HashMap<String, Object>();
		properties.put(MongoIdFactory.PROP_ALIAS, alias);
		properties.put(MongoIdFactory.PROP_URI, uri);

		mongoIdFactoryComponent = new MongoIdFactoryComponent();

		db = mock(DB.class);
		collection = mock(DBCollection.class);
		commandResult = mock(CommandResult.class);
		mongoDatabaseProvider = mock(MongoDatabaseProvider.class);

		when(mongoDatabaseProvider.getAlias()).thenReturn(alias);
		when(mongoDatabaseProvider.getDB()).thenReturn(db);
		when(db.getCollection("elements")).thenReturn(collection);
		when(collection.findOne(any(DBObject.class))).thenReturn(null);
		when(collection.getDB()).thenReturn(db);
		when(db.getLastError()).thenReturn(commandResult);
		when(commandResult.ok()).thenReturn(true);
	}

	@Test
	public void testBindBeforeActivate()
	{
		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);
		mongoIdFactoryComponent.activate(properties);

		ArgumentCaptor<DBObject> argument = ArgumentCaptor.forClass(DBObject.class);
		verify(collection).findOne(any(DBObject.class));
		verify(collection).insert(argument.capture());
		assertThat((Long) argument.getValue().get("_lastId"), is(0L));
	}

	@Test
	public void testActivateBeforeBind()
	{
		mongoIdFactoryComponent.activate(properties);
		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);

		ArgumentCaptor<DBObject> argument = ArgumentCaptor.forClass(DBObject.class);
		verify(collection).findOne(any(DBObject.class));
		verify(collection).insert(argument.capture());
		assertThat((Long) argument.getValue().get("_lastId"), is(0L));
	}

	@Test
	public void testActivateWithExistingId()
	{
		when(collection.findOne(any(DBObject.class))).thenReturn(new BasicDBObject());
		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);
		mongoIdFactoryComponent.activate(properties);

		verify(collection).findOne(any(DBObject.class));
		verifyNoMoreInteractions(collection);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithErrorNotOk()
	{
		when(commandResult.ok()).thenReturn(false);

		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullAlias()
	{
		properties.put(MongoIdFactory.PROP_ALIAS, null);
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyAlias()
	{
		properties.put(MongoIdFactory.PROP_ALIAS, "");
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullURI()
	{
		properties.put(MongoIdFactory.PROP_URI, null);
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyURI()
	{
		properties.put(MongoIdFactory.PROP_URI, "");
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithBadURIScheme()
	{
		properties.put(MongoIdFactory.PROP_URI, "mongodd://localhost/db");
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithMissingURISegments()
	{
		properties.put(MongoIdFactory.PROP_URI, "mongodb://localhost");
		mongoIdFactoryComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithTooManyURISegments()
	{
		properties.put(MongoIdFactory.PROP_URI, "mongodb://localhost/db/collection/item");
		mongoIdFactoryComponent.activate(properties);
	}

	@Test
	public void testGetNextId() throws IOException
	{
		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);
		mongoIdFactoryComponent.activate(properties);

		DBObject result = new BasicDBObject("_lastId", "1");
		when(collection.findAndModify(any(DBObject.class), isNull(DBObject.class), isNull(DBObject.class), booleanThat(is(false)), any(DBObject.class), booleanThat(is(true)), booleanThat(is(false))))
				.thenReturn(result);
		assertThat(mongoIdFactoryComponent.getNextId(), is("1"));
	}

	@Test(expected = IOException.class)
	public void testGetNextIdWithErrorNotOk() throws IOException
	{
		mongoIdFactoryComponent.bindMongoDatabaseProvider(mongoDatabaseProvider);
		mongoIdFactoryComponent.activate(properties);

		DBObject result = new BasicDBObject("_lastId", "1");
		when(collection.findAndModify(any(DBObject.class), isNull(DBObject.class), isNull(DBObject.class), booleanThat(is(false)), any(DBObject.class), booleanThat(is(true)), booleanThat(is(false))))
				.thenReturn(result);
		when(commandResult.ok()).thenReturn(false);

		mongoIdFactoryComponent.getNextId();
	}

	public void testGetNextIdWithoutInit() throws IOException
	{
		mongoIdFactoryComponent.activate(properties);
		assertThat(mongoIdFactoryComponent.getNextId(), is(nullValue()));
	}
}
