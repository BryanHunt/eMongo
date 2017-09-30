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

import org.eclipselabs.emongo.client.MongoProvider;
import org.eclipselabs.emongo.components.junit.support.MongoProviderComponentTestHarness;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */

public class TestMongoProviderComponent
{
  private MongoClient mongoClient;
  private MongoProviderComponentTestHarness mongoClientProviderComponent;
  private String[] uris = new String[] { "mongodb://localhost", "mongodb://www.google.com", "mongodb://www.apple.com" };
  private Map<String, Object> properties;

  @Before
  public void setUp()
  {
    mongoClient = mock(MongoClient.class);
    mongoClientProviderComponent = new MongoProviderComponentTestHarness(mongoClient);

    properties = new HashMap<>();
    properties.put(MongoProvider.PROP_CLIENT_ID, "junit");
    properties.put(MongoProvider.PROP_URI, "mongodb://localhost, mongodb://www.google.com, mongodb://www.apple.com");
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
    properties.put(MongoProvider.PROP_URI, "mongodb://localhost");

    mongoClientProviderComponent.activate(properties);

    assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]))));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testActivateWithMultipleURI() throws Exception
  {
    mongoClientProviderComponent.activate(properties);

    assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]), equalTo(uris[1]), equalTo(uris[2]))));
  }

  @Test(expected = IllegalStateException.class)
  public void testActivateWithEmptyURI() throws Exception
  {
    properties.put(MongoProvider.PROP_URI, "");

    mongoClientProviderComponent.activate(properties);
  }

  @Test(expected = IllegalStateException.class)
  public void testActivateWithBadURIScheme() throws Exception
  {
    properties.put(MongoProvider.PROP_URI, "mongodd://localhost");

    mongoClientProviderComponent.activate(properties);
  }

  @Test(expected = IllegalStateException.class)
  public void testActivateWithTooManuURISegments() throws Exception
  {
    properties.put(MongoProvider.PROP_URI, "mongodd://localhost/database/");

    mongoClientProviderComponent.activate(properties);
  }
}
