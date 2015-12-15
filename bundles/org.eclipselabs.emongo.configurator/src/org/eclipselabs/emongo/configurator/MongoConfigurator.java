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
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author bhunt
 * 
 */
@Component(service = Object.class, property = {"osgi.command.scope=mongodb", 
    "osgi.command.function=configureClient", 
    "osgi.command.function=configureDatabase",
    "osgi.command.function=configureMonitor"})
public class MongoConfigurator
{
	private static volatile MongoConfigurator mongoConfigurator;

	private volatile ConfigurationAdmin configurationAdmin;

	public static MongoConfigurator getInstance()
	{
		return mongoConfigurator;
	}

	@Activate
	void activate()
	{
		mongoConfigurator = this;
	}

	@Deactivate
	void deactivate()
	{
		mongoConfigurator = null;
	}

	/**
	 * Configures a MongoClientProvider
	 * @param clientId the client id
	 * @param clientURI the client URI
	 * @throws ConfigurationException if there is a problem configuring the service
	 */
	public void configureClient(String clientId, String clientURI) throws ConfigurationException
	{
		try
		{
			Configuration config = configurationAdmin.getConfiguration("org.eclipselabs.emongo.clientProvider", null);

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

	public void configureMonitor(String databaseAlias, Integer updateInterval) throws ConfigurationException
	{
    try
    {
      Configuration config = configurationAdmin.getConfiguration("org.eclipselabs.emongo.monitor.databaseMonitor", null);

      Dictionary<String, Object> properties = config.getProperties();

      if (properties == null)
        properties = new Hashtable<String, Object>();

      properties.put("updateInterval", updateInterval);
      properties.put("MongoDatabaseProvider.target", "(" + MongoClientProvider.PROP_CLIENT_ID + "=" + databaseAlias + ")");
      config.update(properties);
    }
    catch (IOException e)
    {
      throw new ConfigurationException(e);
    }	  
	}
	
	@Reference(unbind = "-")
	void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		this.configurationAdmin = configurationAdmin;
	}
}
