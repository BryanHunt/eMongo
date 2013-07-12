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

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.components.junit.support.MongoClientProviderComponentTestHarness;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;

/**
 * @author bhunt
 * 
 */
public class TestMongoClientProviderComponent
{
	private Map<String, Object> properties;
	private MongoClient mongoClient;
	private MongoClientProviderComponentTestHarness mongoClientProviderComponent;
	private String[] uris = new String[] { "mongodb://localhost", "mongodb://www.google.com", "mongodb://www.apple.com" };

	@Before
	public void setUp()
	{
		mongoClient = mock(MongoClient.class);
		properties = new HashMap<String, Object>();
		mongoClientProviderComponent = new MongoClientProviderComponentTestHarness(mongoClient);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testActivateWithSingleURI()
	{
		properties.put(MongoClientProvider.PROP_URI, uris[0]);

		mongoClientProviderComponent.activate(properties);

		assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]))));
		assertThat(mongoClientProviderComponent.getServerAddresses().iterator().next().getHost(), is("localhost"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testActivateWithMultipleURI()
	{
		String uri = uris[0] + "," + uris[1] + " , " + uris[2];
		properties.put(MongoClientProvider.PROP_URI, uri);

		mongoClientProviderComponent.activate(properties);

		assertThat(mongoClientProviderComponent.getURIs(), is(arrayContainingInAnyOrder(equalTo(uris[0]), equalTo(uris[1]), equalTo(uris[2]))));
		assertThat(mongoClientProviderComponent.getServerAddresses().size(), is(3));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullURI()
	{
		properties.put(MongoClientProvider.PROP_URI, null);
		mongoClientProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyURI()
	{
		properties.put(MongoClientProvider.PROP_URI, "");
		mongoClientProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithBadURIScheme()
	{
		properties.put(MongoClientProvider.PROP_URI, "mongodd://localhost");
		mongoClientProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithTooManuURISegments()
	{
		properties.put(MongoClientProvider.PROP_URI, "mongodd://localhost/");
		mongoClientProviderComponent.activate(properties);
	}
}
