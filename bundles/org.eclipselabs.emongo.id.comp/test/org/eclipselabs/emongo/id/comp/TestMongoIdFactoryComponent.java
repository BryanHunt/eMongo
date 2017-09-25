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

package org.eclipselabs.emongo.id.comp;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.id.MongoIdFactory;
import org.eclipselabs.emongo.id.comp.MongoIdFactoryComponent;
import org.eclipselabs.emongo.id.comp.MongoIdFactoryComponent.IdConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.mongodb.CommandResult;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@SuppressWarnings("restriction")
public class TestMongoIdFactoryComponent
{
	private MongoProvider mongoClientProvider;
	private MongoIdFactoryComponent mongoIdFactoryComponent;
	private Map<String, Object> properties;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	private CommandResult commandResult;
	private String collectionName;
	private FindIterable<Document> cursor;

	@SuppressWarnings("unchecked")
  @Before
	public void setUp()
	{
		collectionName = "elements";
		properties = new HashMap<String, Object>();
		properties.put(MongoIdFactory.PROP_COLLECTION, collectionName);

		mongoIdFactoryComponent = new MongoIdFactoryComponent();

		db = mock(MongoDatabase.class);
		collection = mock(MongoCollection.class);
		commandResult = mock(CommandResult.class);
		mongoClientProvider = mock(MongoProvider.class);
		cursor = mock(FindIterable.class);
		
		when(mongoClientProvider.getURIs()).thenReturn(new String[] {"mongodb://localhost/junit"});
		when(mongoClientProvider.getMongoDatabase()).thenReturn(db);
		when(db.getCollection("elements")).thenReturn(collection);
		when(collection.withWriteConcern(any(WriteConcern.class))).thenReturn(collection);
		when(collection.find(any(Document.class))).thenReturn(cursor);
		when(cursor.first()).thenReturn(null);
//		when(collection.getNamespace().).thenReturn(db);
//		when(db.getLastError()).thenReturn(commandResult);
		when(commandResult.ok()).thenReturn(true);
	}

	@Test
	public void testActivate() throws Exception
	{
		mongoIdFactoryComponent.bindMongoClientProvider(mongoClientProvider);
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));

		ArgumentCaptor<Document> argument = ArgumentCaptor.forClass(Document.class);
		verify(collection).find(any(Document.class));
		verify(cursor).first();
		verify(collection).insertOne(argument.capture());
		assertThat((Long) argument.getValue().get("_nextId"), is(0L));
	}

	@Test
	public void testActivateWithExistingId() throws Exception
	{
		when(cursor.first()).thenReturn(new Document());
		mongoIdFactoryComponent.bindMongoClientProvider(mongoClientProvider);
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));

		verify(collection).find(any(Document.class));
		verify(collection).withWriteConcern(any(WriteConcern.class));
		verify(cursor).first();
		verifyNoMoreInteractions(collection);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullCollectionName() throws Exception
	{
		properties.put(MongoIdFactory.PROP_COLLECTION, null);
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyCollectionName() throws Exception
	{
		properties.put(MongoIdFactory.PROP_COLLECTION, "");
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));
	}

	@Test
	public void testGetNextId() throws Exception
	{
		mongoIdFactoryComponent.bindMongoClientProvider(mongoClientProvider);
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));

		Document result = new Document("_nextId", Long.valueOf(1));
		when(collection.findOneAndUpdate(any(Document.class), any(Document.class))).thenReturn(result);
		assertThat(mongoIdFactoryComponent.getNextId(), is(1L));
	}

	public void testGetNextIdWithoutInit() throws Exception
	{
		mongoIdFactoryComponent.activate(aQute.lib.converter.Converter.cnv(IdConfig.class, properties));
		assertThat(mongoIdFactoryComponent.getNextId(), is(nullValue()));
	}
}
