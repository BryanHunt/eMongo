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

package org.eclipselabs.mongo.junit.integration.support;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.emongo.MongoIdFactory;
import org.eclipselabs.emongo.config.ConfigurationProperties;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author bhunt
 * 
 */
public class MongoConfiguration
{
	private ConfigurationAdmin configurationAdmin;

	void activate() throws IOException
	{
		Configuration config = configurationAdmin.getConfiguration(ConfigurationProperties.CLIENT_PID, null);

		Dictionary<String, Object> properties = config.getProperties();

		if (properties == null)
			properties = new Hashtable<String, Object>();

		properties.put(MongoClientProvider.PROP_CLIENT_ID, "junit");
		properties.put(MongoClientProvider.PROP_URI, "mongodb://localhost");
		config.update(properties);

		config = configurationAdmin.getConfiguration(ConfigurationProperties.DATABASE_PID, null);

		properties = config.getProperties();

		if (properties == null)
			properties = new Hashtable<String, Object>();

		properties.put(MongoDatabaseProvider.PROP_CLIENT_FILTER, "(" + MongoClientProvider.PROP_CLIENT_ID + "=junit)");
		properties.put(MongoDatabaseProvider.PROP_DATABASE, "junit");
		properties.put(MongoDatabaseProvider.PROP_ALIAS, "junit");
		config.update(properties);

		config = configurationAdmin.getConfiguration(ConfigurationProperties.ID_FACTORY_PID, null);

		properties = config.getProperties();

		if (properties == null)
			properties = new Hashtable<String, Object>();

		properties.put(MongoIdFactory.PROP_COLLECTION, "junit_id");
		config.update(properties);
	}

	void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		this.configurationAdmin = configurationAdmin;
	}
}
