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

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

import java.io.IOException;

import org.bson.Document;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.MongoIdFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@Component(service = MongoIdFactory.class, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = {"org.eclipselabs.emongo.idFactory"})
public class MongoIdFactoryComponent extends AbstractComponent implements MongoIdFactory
{
  public @interface IdConfig
  {
    String collectionName();
  }

	private volatile String collectionName;
	private volatile String uri;

	private volatile MongoCollection<Document> collection;
	private volatile MongoDatabaseProvider mongoDatabaseProvider;

	private static final String ID = "_id";
	private static final String LAST_ID = "_lastId";

	public static String validateCollectionName(String value)
	{
		if (value == null || value.isEmpty())
			return "The collection was not specified as part of the component configuration";

		return null;
	}

	@Activate
	public void activate(IdConfig config)
	{
		collectionName = config.collectionName();
		handleIllegalConfiguration(validateCollectionName(collectionName));

		uri = mongoDatabaseProvider.getURI() + "/" + collectionName;

		MongoDatabase db = mongoDatabaseProvider.getDatabase();
		collection = db.getCollection(collectionName).withWriteConcern(WriteConcern.MAJORITY);
		Document object = collection.find(eq(ID, "0")).first();

		if (object == null)
			collection.insertOne(new Document(ID, "0").append(LAST_ID, Long.valueOf(0)));
	}

	@Override
	public String getCollectionURI()
	{
		return uri;
	}

	@Override
	public Long getNextId() throws IOException
	{
		if (collection == null)
			return null;

		Document result = collection.findOneAndUpdate(eq(ID, "0"), inc(LAST_ID, 1));
		return result.getLong(LAST_ID);
	}

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  public void bindLogService(LogService logService)
  {
    super.bindLogService(logService);;
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }

  @Reference(unbind = "-")
	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		this.mongoDatabaseProvider = mongoDatabaseProvider;
	}
}
