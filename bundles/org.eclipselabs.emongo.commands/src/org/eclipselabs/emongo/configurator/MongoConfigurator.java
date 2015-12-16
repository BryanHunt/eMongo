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

import org.apache.felix.service.command.Descriptor;
import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author bhunt
 * 
 */
@Component(service = Object.class, property = {"osgi.command.scope=mongodb", "osgi.command.function=configureDatabase", "osgi.command.function=configureMonitor"})
public class MongoConfigurator
{
	private volatile ConfigurationAdmin configurationAdmin;

	@Descriptor("configure a mongodb database")
	public void configureDatabase(@Descriptor("client id") String clientId, @Descriptor("database URI") String uri) throws ConfigurationException
	{
		try
		{
			Configuration config = configurationAdmin.getConfiguration(MongoProvider.PID, null);

			Dictionary<String, Object> properties = config.getProperties();

			if (properties == null)
				properties = new Hashtable<String, Object>();

			properties.put(MongoProvider.PROP_CLIENT_ID, clientId);
			properties.put(MongoProvider.PROP_URI, uri);
			config.update(properties);
		}
		catch (IOException e)
		{
			throw new ConfigurationException(e);
		}
	}

	@Descriptor("configure a mongodb monitor")
	public void configureMonitor(@Descriptor("client id") String clientId, @Descriptor("update interval") Integer updateInterval) throws ConfigurationException
	{
    try
    {
      Configuration config = configurationAdmin.getConfiguration(MongoAdmin.MONITOR_PID, null);

      Dictionary<String, Object> properties = config.getProperties();

      if (properties == null)
        properties = new Hashtable<String, Object>();

      properties.put("updateInterval", updateInterval);
      properties.put("MongoDatabaseProvider.target", "(" + MongoProvider.PROP_CLIENT_ID + "=" + clientId + ")");
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
