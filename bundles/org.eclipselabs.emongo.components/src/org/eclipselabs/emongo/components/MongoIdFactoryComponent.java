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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private volatile String uri;
	private volatile String collectionName;

	private volatile DBCollection collection;
	private volatile DBObject query;
	private volatile DBObject update;
	private Map<String, MongoDatabaseProvider> mongoDatabaseProvidersByAlias = new ConcurrentHashMap<String, MongoDatabaseProvider>();

	private static final String ID = "_id";
	private static final String LAST_ID = "_lastId";

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
		uri = (String) properties.get(PROP_URI);

		if (alias == null || alias.isEmpty())
			handleIllegalConfiguration("The alias was not specified as part of the component configuration");

		if (uri == null || uri.isEmpty())
			handleIllegalConfiguration("The MongoDB uri was not found in the configuration properties");

		// The URI will be of the form: mongodb://host[:port]/db/collection
		// When the string is split on / the URI must have 5 parts

		String[] segments = uri.split("/");

		if (segments.length != 5)
			handleIllegalConfiguration("The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/db/collection'");

		collectionName = segments[4];

		MongoDatabaseProvider mongoDatabaseProvider = mongoDatabaseProvidersByAlias.get(alias);

		if (mongoDatabaseProvider != null)
			init(mongoDatabaseProvider);
	}

	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		if (mongoDatabaseProvider.getAlias().equals(alias))
			init(mongoDatabaseProvider);
		else
			mongoDatabaseProvidersByAlias.put(mongoDatabaseProvider.getAlias(), mongoDatabaseProvider);
	}

	public void unbindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		mongoDatabaseProvidersByAlias.remove(mongoDatabaseProvider.getAlias());
	}

	private void init(MongoDatabaseProvider mongoDatabaseProvider)
	{
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
