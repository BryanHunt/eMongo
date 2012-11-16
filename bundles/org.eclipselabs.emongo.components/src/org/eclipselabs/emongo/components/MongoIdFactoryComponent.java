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

import org.eclipselabs.emongo.DatabaseLocator;
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
	private volatile DatabaseLocator databaseLocator;
	private volatile DBCollection collection;
	private volatile DBObject query;
	private volatile DBObject update;
	private volatile DB db;

	private static final String ID = "_id";
	private static final String LAST_ID = "lastId";

	@Override
	public String getNextId() throws IOException
	{
		DBObject result = collection.findAndModify(query, null, null, false, update, true, false);

		if (!db.getLastError().ok())
			throw new IOException("Failed to update the id counter for collection: '" + collection.getName() + "'");

		return result.get(LAST_ID).toString();
	}

	public void configure(Map<String, Object> parameters)
	{
		String uri = (String) parameters.get(PROP_URI);

		// The URI will be of the form: mongodb://host[:port]/db/collection
		// When the string is split on / the URI must have 5 parts

		String[] segments = uri.split("/");

		if (segments.length != 5)
			handleIllegalConfiguration("The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/db'");

		String collectionName = segments[4];

		db = databaseLocator.getDatabase(uri);

		if (db == null)
			handleIllegalConfiguration("Could not find database for URI: '" + uri + "'");

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

	public void bindDatabaseLocator(DatabaseLocator databaseLocator)
	{
		this.databaseLocator = databaseLocator;
	}
}
