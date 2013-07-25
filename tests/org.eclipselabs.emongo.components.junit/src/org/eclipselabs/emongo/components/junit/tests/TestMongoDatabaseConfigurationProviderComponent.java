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
	private String clientId;
	private String databaseName;
	private String factory;
	private String alias;
	private String user;
	private String password;

	@Before
	public void setUp()
	{
		clientId = "junit";
		databaseName = "junit";
		factory = "factory";
		alias = "alias";
		user = "user";
		password = "password";

		properties = new HashMap<String, Object>();
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_CLIENT_ID, clientId);
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_DATABASE, databaseName);
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
		assertThat(mongoDatabaseConfigurationProviderComponent.getClientId(), is(clientId));
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
	public void testActivateWithNullDatabaseName()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_DATABASE, null);
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyDatabaseName()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_DATABASE, "");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithNullClientId()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_CLIENT_ID, null);
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}

	@Test(expected = IllegalStateException.class)
	public void testActivateWithEmptyClientId()
	{
		properties.put(MongoAuthenticatedDatabaseConfigurationProvider.PROP_CLIENT_ID, "");
		mongoDatabaseConfigurationProviderComponent.activate(properties);
	}
}
