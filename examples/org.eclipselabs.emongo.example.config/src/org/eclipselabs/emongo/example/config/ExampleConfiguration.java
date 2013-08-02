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

package org.eclipselabs.emongo.example.config;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.config.MongoDatabaseConfigurationProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author bhunt
 * 
 */
public class ExampleConfiguration
{
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		try
		{
			Configuration config = configurationAdmin.getConfiguration(MongoClientProvider.PROP_FACTORY_ID, null);

			Dictionary<String, Object> properties = config.getProperties();

			if (properties == null)
				properties = new Hashtable<String, Object>();

			properties.put(MongoClientProvider.PROP_CLIENT_ID, "example");
			properties.put(MongoClientProvider.PROP_URI, "mongodb://localhost");
			config.update(properties);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			Configuration config = configurationAdmin.getConfiguration(MongoDatabaseConfigurationProvider.PROP_FACTORY_ID, null);

			Dictionary<String, Object> properties = config.getProperties();

			if (properties == null)
				properties = new Hashtable<String, Object>();

			properties.put(MongoDatabaseConfigurationProvider.PROP_CLIENT_ID, "example");
			properties.put(MongoDatabaseConfigurationProvider.PROP_DATABASE, "example");
			properties.put(MongoDatabaseConfigurationProvider.PROP_ALIAS, "example");
			config.update(properties);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
