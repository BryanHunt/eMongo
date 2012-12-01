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

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.eunit.junit.utils.ServiceTestHarness;
import org.junit.Test;

import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */
public class TestMongoProvider extends ServiceTestHarness
{
	@Test
	public void testCreateMongo()
	{
		MongoClient mongo = mongoProvider.getMongoClient();
		assertThat(mongo.getAddress().getHost(), is("localhost"));
		assertThat(mongo.getAddress().getPort(), is(27017));
	}

	void bindMongoProvider(MongoClientProvider service)
	{
		mongoProvider = service;
	}

	private static MongoClientProvider mongoProvider;
}
