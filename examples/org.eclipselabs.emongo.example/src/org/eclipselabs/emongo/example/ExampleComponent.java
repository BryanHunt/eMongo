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

package org.eclipselabs.emongo.example;

import org.eclipselabs.emongo.MongoDatabaseProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * @author bhunt
 * 
 */
public class ExampleComponent
{
	private volatile MongoDatabaseProvider mongoDatabaseProvider;

	public void activate()
	{
		DB database = mongoDatabaseProvider.getDB();
		DBCollection collection = database.getCollection("items");
		collection.drop();

		for (int i = 0; i < 10; i++)
			collection.insert(new BasicDBObject("x", i));

		DBCursor cursor = collection.find();

		while (cursor.hasNext())
			System.out.print(cursor.next().get("x") + " ");

		System.out.println();
		collection.drop();
	}

	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		this.mongoDatabaseProvider = mongoDatabaseProvider;
	}
}
