/**
 * 
 */

package org.eclipselabs.emongo.junit.config;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.emongo.MongoProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author bhunt
 * 
 */
@Component(immediate = true)
public class LocalConfigurator
{
	private volatile ConfigurationAdmin configurationAdmin;

	@Activate
	public void activate() throws IOException
	{
		Configuration config = configurationAdmin.createFactoryConfiguration(MongoProvider.PID, null);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();

		properties.put(MongoProvider.PROP_CLIENT_ID, "junit");
		properties.put(MongoProvider.PROP_URI, "mongodb://localhost/junit");
		properties.put("type", "mongo");
		config.update(properties);
	}

	@Reference(unbind = "-")
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin)
	{
		this.configurationAdmin = configurationAdmin;
	}
}
