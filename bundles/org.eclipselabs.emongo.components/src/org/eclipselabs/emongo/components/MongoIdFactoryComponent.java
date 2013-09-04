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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.MongoIdFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author bhunt
 * 
 */
public class MongoIdFactoryComponent extends AbstractComponent implements MongoIdFactory
{
	private volatile String alias;
	private volatile String collectionName;
	private volatile String uri;

	private volatile DBCollection collection;
	private volatile DBObject query;
	private volatile DBObject update;
	private Map<String, MongoDatabaseProvider> mongoDatabaseProvidersByAlias = new HashMap<String, MongoDatabaseProvider>();

	private static final String ID = "_id";
	private static final String LAST_ID = "_lastId";

	@Override
	public String getCollectionURI()
	{
		return uri;
	}

	@Override
	public String getNextId() throws IOException
	{
		if (collection == null)
			return null;

		DBObject result = collection.findAndModify(query, null, null, false, update, true, false);

		if (!collection.getDB().getLastError().ok())
			throw new IOException("Failed to update the id counter for collection: '" + collection.getName() + "'");

		return result.get(LAST_ID).toString();
	}

	public void activate(Map<String, Object> properties)
	{
		alias = (String) properties.get(PROP_ALIAS);

		if (alias == null || alias.isEmpty())
			handleIllegalConfiguration("The alias was not specified as part of the component configuration");

		collectionName = (String) properties.get(PROP_COLLECTION);

		if (collectionName == null || collectionName.isEmpty())
			handleIllegalConfiguration("The collection was not specified as part of the component configuration");

		MongoDatabaseProvider mongoDatabaseProvider = null;

		synchronized (mongoDatabaseProvidersByAlias)
		{
			mongoDatabaseProvider = mongoDatabaseProvidersByAlias.get(alias);
		}

		if (mongoDatabaseProvider != null)
			init(mongoDatabaseProvider);
	}

	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		if (mongoDatabaseProvider.getAlias().equals(alias))
			init(mongoDatabaseProvider);
		else
		{
			synchronized (mongoDatabaseProvidersByAlias)
			{
				mongoDatabaseProvidersByAlias.put(mongoDatabaseProvider.getAlias(), mongoDatabaseProvider);
			}
		}
	}

	public void unbindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		synchronized (mongoDatabaseProvidersByAlias)
		{
			if (mongoDatabaseProvidersByAlias.get(mongoDatabaseProvider.getAlias()) == mongoDatabaseProvider)
				mongoDatabaseProvidersByAlias.remove(mongoDatabaseProvider.getAlias());
		}
	}

	private void init(MongoDatabaseProvider mongoDatabaseProvider)
	{
		uri = mongoDatabaseProvider.getURI() + "/" + collectionName;

		DB db = mongoDatabaseProvider.getDB();
		collection = db.getCollection(collectionName);
		query = new BasicDBObject(ID, "0");
		update = new BasicDBObject("$inc", new BasicDBObject(LAST_ID, Long.valueOf(1)));

		DBObject object = collection.findOne(query);

		if (object == null)
		{
			DBObject initialId = new BasicDBObject();
			initialId.put(ID, "0");
			initialId.put(LAST_ID, Long.valueOf(0));
			collection.insert(initialId);

			if (!db.getLastError().ok())
				handleIllegalConfiguration("Could not initialize the id counter for collection: '" + collection.getName() + "'");
		}
	}
}
