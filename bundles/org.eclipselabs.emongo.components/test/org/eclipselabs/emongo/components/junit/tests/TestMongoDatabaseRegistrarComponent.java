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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.components.MongoAuthenticatedDatabaseConfigurationProvider;
import org.eclipselabs.emongo.components.MongoDatabaseRegistrarComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

/**
 * @author bhunt
 * 
 */
public class TestMongoDatabaseRegistrarComponent
{
	private String clientId;
	private String alias;
	private String databaseName;
	private ComponentContext componentContext;
	private BundleContext bundleContext;
	private ServiceRegistration<MongoDatabaseProvider> serviceRegistration;
	private MongoClientProvider mongoClientProvider;
	private MongoAuthenticatedDatabaseConfigurationProvider mongoAuthenticatedDatabaseConfigurationProvider;

	private MongoDatabaseRegistrarComponent mongoDatabaseRegistrarComponent;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp()
	{
		clientId = "junit";
		alias = "junit";
		String clientURI = "mongodb://locahost";
		databaseName = "junit";
		String[] uris = new String[] { clientURI };

		componentContext = mock(ComponentContext.class);
		bundleContext = mock(BundleContext.class);
		serviceRegistration = mock(ServiceRegistration.class);
		mongoClientProvider = mock(MongoClientProvider.class);
		mongoAuthenticatedDatabaseConfigurationProvider = mock(MongoAuthenticatedDatabaseConfigurationProvider.class);

		when(mongoClientProvider.getURIs()).thenReturn(uris);
		when(mongoClientProvider.getClientId()).thenReturn(clientId);

		when(mongoAuthenticatedDatabaseConfigurationProvider.getClientId()).thenReturn(clientId);
		when(mongoAuthenticatedDatabaseConfigurationProvider.getDatabaseName()).thenReturn(databaseName);
		when(mongoAuthenticatedDatabaseConfigurationProvider.getAlias()).thenReturn(alias);

		when(componentContext.getBundleContext()).thenReturn(bundleContext);
		when(bundleContext.registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), any(Dictionary.class))).thenReturn(serviceRegistration);

		mongoDatabaseRegistrarComponent = new MongoDatabaseRegistrarComponent();
	}

	@Test
	public void testActivate()
	{
		mongoDatabaseRegistrarComponent.activate(componentContext);
		verifyNoMoreInteractions(componentContext, mongoClientProvider, mongoAuthenticatedDatabaseConfigurationProvider);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testActivateClientConfig()
	{
		mongoDatabaseRegistrarComponent.activate(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoClientProvider(mongoClientProvider);

		verify(mongoClientProvider, atLeastOnce()).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoDatabaseConfigurationProvider(mongoAuthenticatedDatabaseConfigurationProvider);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getDatabaseName();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getAlias();
		verify(componentContext).getBundleContext();

		ArgumentCaptor<Dictionary> argument = ArgumentCaptor.forClass(Dictionary.class);
		verify(bundleContext).registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), argument.capture());
		assertThat((String) argument.getValue().get("alias"), is(alias));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testClientActivateConfig()
	{
		mongoDatabaseRegistrarComponent.bindMongoClientProvider(mongoClientProvider);

		verify(mongoClientProvider).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.activate(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoDatabaseConfigurationProvider(mongoAuthenticatedDatabaseConfigurationProvider);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getDatabaseName();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getAlias();
		verify(componentContext).getBundleContext();

		ArgumentCaptor<Dictionary> argument = ArgumentCaptor.forClass(Dictionary.class);
		verify(bundleContext).registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), argument.capture());
		assertThat((String) argument.getValue().get("alias"), is(alias));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testClientConfigActivate()
	{
		mongoDatabaseRegistrarComponent.bindMongoClientProvider(mongoClientProvider);

		verify(mongoClientProvider).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoDatabaseConfigurationProvider(mongoAuthenticatedDatabaseConfigurationProvider);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.activate(componentContext);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getAlias();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getDatabaseName();
		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verify(componentContext).getBundleContext();

		ArgumentCaptor<Dictionary> argument = ArgumentCaptor.forClass(Dictionary.class);
		verify(bundleContext).registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), argument.capture());
		assertThat((String) argument.getValue().get("alias"), is(alias));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testActivateConfigClient()
	{
		mongoDatabaseRegistrarComponent.activate(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoDatabaseConfigurationProvider(mongoAuthenticatedDatabaseConfigurationProvider);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.bindMongoClientProvider(mongoClientProvider);

		verify(mongoClientProvider, atLeastOnce()).getClientId();
		verify(componentContext).getBundleContext();

		ArgumentCaptor<Dictionary> argument = ArgumentCaptor.forClass(Dictionary.class);
		verify(bundleContext).registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), argument.capture());
		assertThat((String) argument.getValue().get("alias"), is(alias));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConfigClientActivate()
	{
		mongoDatabaseRegistrarComponent.bindMongoDatabaseConfigurationProvider(mongoAuthenticatedDatabaseConfigurationProvider);

		verify(mongoAuthenticatedDatabaseConfigurationProvider, atLeastOnce()).getClientId();
		verifyNoMoreInteractions(componentContext, mongoClientProvider, mongoAuthenticatedDatabaseConfigurationProvider);

		mongoDatabaseRegistrarComponent.bindMongoClientProvider(mongoClientProvider);

		verify(mongoClientProvider, atLeastOnce()).getClientId();
		verifyNoMoreInteractions(componentContext);

		mongoDatabaseRegistrarComponent.activate(componentContext);

		verify(componentContext).getBundleContext();

		ArgumentCaptor<Dictionary> argument = ArgumentCaptor.forClass(Dictionary.class);
		verify(bundleContext).registerService(eq(MongoDatabaseProvider.class), any(MongoDatabaseProvider.class), argument.capture());
		assertThat((String) argument.getValue().get("alias"), is(alias));
	}
}
