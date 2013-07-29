/**
 * 
 */

package org.eclipselabs.emongo.junit.config;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.MongoClientProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author bhunt
 * 
 */
public class LocalConfigurator
{
	private volatile ConfigurationAdmin configurationAdmin;

	public void activate() throws IOException
	{
		Configuration config = configurationAdmin.createFactoryConfiguration("org.eclipselabs.emongo.clientProvider", null);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();

		properties.put(MongoClientProvider.PROP_CLIENT_ID, "junit");
		properties.put(MongoClientProvider.PROP_URI, "mongodb://localhost");
		properties.put("type", "mongo");
		config.update(properties);

		config = configurationAdmin.createFactoryConfiguration("org.eclipselabs.emongo.databaseConfigurationProvider", null);
		properties = new Hashtable<String, Object>();
		properties.put("client_id", "junit");
		properties.put("alias", "junit");
		properties.put("database", "junit");
		config.update(properties);
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		this.configurationAdmin = configurationAdmin;
	}
}
