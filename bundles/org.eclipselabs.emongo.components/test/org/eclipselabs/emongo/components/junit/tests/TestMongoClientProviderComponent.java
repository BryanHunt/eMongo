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

package org.eclipselabs.emongo.components.junit.tests;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.components.MongoProviderComponent.ClientConfig;
import org.eclipselabs.emongo.components.junit.support.MongoClientProviderComponentTestHarness;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */

@SuppressWarnings("restriction")
public class TestMongoClientProviderComponent
{
	private MongoClient mongoClient;
	private MongoClientProviderComponentTestHarness mongoClientProviderComponent;
	private String[] uris = new String[] { "mongodb://localhost", "mongodb://www.google.com", "mongodb://www.apple.com" };
  private Map<String, Object> properties;
	
	@Before
	public void setUp()
	{
		mongoClient = mock(MongoClient.class);
		mongoClientProviderComponent = new MongoClientProviderComponentTestHarness(mongoClient);

		properties = new HashMap<>();
    properties.put(MongoProvider.PROP_CLIENT_ID, "junit");
    properties.put(MongoProvider.PROP_URI , "mongodb://localhost, mongodb://www.google.com, mongodb://www.apple.com");
    properties.put("credentials", new String[0]);
    properties.put("connectionsPerHost", 100);
    properties.put("heartbeatFrequency", 10000);
    properties.put("minHeartbeatFrequency", 500);
    properties.put("requiredReplicaSetName", "");
    properties.put("threadsAllowedToBlockForConnectionMultiplier", 5);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testActivateWithSingleURI() throws Exception
	{
	  properties.put(MongoProvider.PROP_URI , "mongodb://localhost");
	  
		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));

		assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]))));
		assertThat(mongoClientProviderComponent.getServerAddresses().iterator().next().getHost(), is("localhost"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testActivateWithMultipleURI() throws Exception
	{
		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));

		assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]), equalTo(uris[1]), equalTo(uris[2]))));
		assertThat(mongoClientProviderComponent.getServerAddresses().size(), is(3));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyURI() throws Exception
	{
    properties.put(MongoProvider.PROP_URI , "");

		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithBadURIScheme() throws Exception
	{
    properties.put(MongoProvider.PROP_URI , "mongodd://localhost");

		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithTooManuURISegments() throws Exception
	{
    properties.put(MongoProvider.PROP_URI , "mongodd://localhost/database/");

		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));
	}

  @Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyClientId() throws Exception
	{
    properties.put(MongoProvider.PROP_CLIENT_ID, "");
    properties.put(MongoProvider.PROP_URI , "mongodb://localhost");

		mongoClientProviderComponent.activate(aQute.lib.converter.Converter.cnv(ClientConfig.class, properties));
	}
}
