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

import org.bson.Document;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@Component
public class ExampleComponent
{
	private volatile MongoDatabaseProvider mongoDatabaseProvider;

	@Activate
	public void activate()
	{
		MongoDatabase database = mongoDatabaseProvider.getDatabase();
		MongoCollection<Document> collection = database.getCollection("items");
		collection.drop();

		for (int i = 0; i < 10; i++)
			collection.insertOne(new Document("x", i));

		FindIterable<Document> documents = collection.find();

		for (Document document : documents)
			System.out.print(document.get("x") + " ");

		System.out.println();
		collection.drop();
	}

	@Reference(unbind = "-")
	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		this.mongoDatabaseProvider = mongoDatabaseProvider;
	}
}
