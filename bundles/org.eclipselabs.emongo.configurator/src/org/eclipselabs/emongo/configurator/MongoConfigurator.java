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

package org.eclipselabs.emongo.configurator;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.config.ConfigurationProperties;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author bhunt
 * 
 */
public class MongoConfigurator
{
	private static volatile MongoConfigurator mongoConfigurator;

	private volatile ConfigurationAdmin configurationAdmin;

	public static MongoConfigurator getInstance()
	{
		return mongoConfigurator;
	}

	void activate()
	{
		mongoConfigurator = this;
	}

	void deactivate()
	{
		mongoConfigurator = null;
	}

	public void configureClient(String clientId, String clientURI) throws ConfigurationException
	{
		try
		{
			Configuration config = configurationAdmin.getConfiguration(ConfigurationProperties.CLIENT_PID, null);

			Dictionary<String, Object> properties = config.getProperties();

			if (properties == null)
				properties = new Hashtable<String, Object>();

			properties.put(MongoClientProvider.PROP_CLIENT_ID, clientId);
			properties.put(MongoClientProvider.PROP_URI, clientURI);
			config.update(properties);
		}
		catch (IOException e)
		{
			throw new ConfigurationException(e);
		}
	}

	public void configureDatabase(String clientId, String databaseName, String alias, String user, String password) throws ConfigurationException
	{
		try
		{
			Configuration config = configurationAdmin.getConfiguration(ConfigurationProperties.DATABASE_PID, null);

			Dictionary<String, Object> properties = config.getProperties();

			if (properties == null)
				properties = new Hashtable<String, Object>();

			properties.put(MongoDatabaseProvider.PROP_DATABASE, databaseName);
			properties.put(MongoDatabaseProvider.PROP_ALIAS, alias);
			properties.put(MongoDatabaseProvider.PROP_USER, user);
			properties.put(MongoDatabaseProvider.PROP_PASSWORD, password);
			properties.put(MongoDatabaseProvider.PROP_CLIENT_FILTER, "(" + MongoClientProvider.PROP_CLIENT_ID + "=" + clientId + ")");
			config.update(properties);
		}
		catch (IOException e)
		{
			throw new ConfigurationException(e);
		}
	}

	void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		this.configurationAdmin = configurationAdmin;
	}
}
