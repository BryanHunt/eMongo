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

package org.eclipselabs.mongo.junit.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.bson.Document;
import org.eclipselabs.emongo.MongoIdFactory;
import org.eclipselabs.emongo.junit.util.MongoDatabaseLocator;
import org.eclipselabs.eunit.junit.utils.ServiceConfigurator;
import org.eclipselabs.eunit.junit.utils.ServiceLocator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.mongodb.client.MongoCollection;

/**
 * @author bhunt
 * 
 */
public class TestMongoIdFactory
{
  private static Dictionary<String, Object> config = new Hashtable<>();

  static
  {
    config.put(MongoIdFactory.PROP_DATABASE_FILTER, "junit");
    config.put(MongoIdFactory.PROP_COLLECTION, "junit_id");
  }
  
  private MongoIdFactory mongoId;
  private MongoDatabaseLocator databaseLocator = new MongoDatabaseLocator("localhost", 27017, "junit", "junit", false, true);
  private ServiceLocator<MongoIdFactory> mongoIdFactoryLocator = new ServiceConfigurator<MongoIdFactory>(MongoIdFactory.class, MongoIdFactory.PID, config);

  @Rule
	public RuleChain chain = RuleChain.outerRule(databaseLocator).around(mongoIdFactoryLocator);


	@Before
	public void setUp()
	{
		mongoId = mongoIdFactoryLocator.getService();
	}

	@Test
	public void testGetNextId() throws IOException
	{
		assertThat(mongoId.getNextId(), is(0L));
    assertThat(mongoId.getNextId(), is(1L));
    assertThat(mongoId.getNextId(), is(2L));
		MongoCollection<Document> collection = databaseLocator.getDatabase().getCollection("junit_id");
		Document result = collection.find(new Document("_id", "0")).first();
		assertThat((Long) result.get("_nextId"), is(3L));
	}
}
