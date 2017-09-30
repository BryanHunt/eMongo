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

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.client.MongoProvider;
import org.eclipselabs.eunit.junit.utils.ServiceConfigurator;
import org.eclipselabs.eunit.junit.utils.ServiceLocator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */
public class TestMongoProvider
{
  private static Dictionary<String, Object> config = new Hashtable<>();

  static
  {
    config.put(MongoProvider.PROP_CLIENT_ID, "junit");
    config.put(MongoProvider.PROP_URI, "mongodb://localhost/junit");
  }

  @Rule
	public ServiceLocator<MongoProvider> mongoClientProviderLocator = new ServiceConfigurator<MongoProvider>(MongoProvider.class, MongoProvider.PID, config);

	private MongoProvider mongoProvider;

	@Before
	public void setUp()
	{
		mongoProvider = mongoClientProviderLocator.getService();
	}

	@Test
	public void testgetMongoClient()
	{
		MongoClient client = mongoProvider.getMongoClient();
		assertThat(client.getAddress().getHost(), is("localhost"));
		assertThat(client.getAddress().getPort(), is(27017));
		assertThat(mongoProvider.getMongoDatabase().getName(), is("junit"));
	}
}
