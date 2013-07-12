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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.eclipselabs.emongo.components.MongoAuthenticatedDatabaseConfigurationProvider;
import org.eclipselabs.emongo.components.MongoDatabaseConfigurationProviderComponent;
import org.junit.Before;
import org.junit.Test;

/**
 * @author bhunt
 * 
 */
public class TestMongoDatabaseConfigurationProviderComponent
{
	private Map<String, Object> properties;
	private MongoDatabaseConfigurationProviderComponent mongoDatabaseConfigurationProviderComponent;
	private String databaseName;
	private String uri;
	private String factory;
	private String alias;
	private String user;
	private String password;

	@Before
	public void setUp()
	{
		databaseName = "junit";
		uri = "mongodb://localhost/" + databaseName;
		factory = "factory";
		alias = "alias";
		user = "user";
		password = "password";

		properties = new HashMap<String, Object>();
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, uri);
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_FACTORY_ID, factory);
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_ALIAS, alias);
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_USER, user);
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_PASSWORD, password);

		mongoDatabaseConfigurationProviderComponent = new MongoDatabaseConfigurationProviderComponent();
	}

	@Test
	public void testActivate()
	{
		mongoDatabaseConfigurationProviderComponent.activate(properties);

		assertThat(mongoDatabaseConfigurationProviderComponent.getAlias(), is(alias));
		assertThat(mongoDatabaseConfigurationProviderComponent.getURI(), is(uri));
		assertThat(mongoDatabaseConfigurationProviderComponent.getDatabaseName(), is(databaseName));
		assertThat(mongoDatabaseConfigurationProviderComponent.getUser(), is(user));
		assertThat(mongoDatabaseConfigurationProviderComponent.getPassword(), is(password));
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullAlias()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_ALIAS, null);
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyAlias()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_ALIAS, "");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullURI()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, null);
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyURI()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, "");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithBadURIScheme()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, "mongodd://localhost/db");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithMissingURISegments()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, "mongodb://localhost");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithTooManyURISegments()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, "mongodb://localhost/db/collection");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithURIMissingDatabaseName()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_URI, "mongodb://localhost/");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}
}
