/**
 * 
 */

package org.eclipselabs.emongo.config;

import org.eclipselabs.emongo.MongoClientProvider;

/**
 * @author bhunt
 * 
 */
public interface ConfigurationProperties
{
	/**
	 * The factory PID of the client provider component. Use this constant when configuring a client
	 * provider component with ConfigurationAdmin.
	 */
	String CLIENT_PID = "org.eclipselabs.emongo.clientProvider";

	/**
	 * The factory PID of the id factory component. Use this constant when configuring an id factory
	 * component with ConfigurationAdmin.
	 */
	String ID_FACTORY_PID = "org.eclipselabs.emongo.idFactory";

	/**
	 * The factory PID of the database configuration provider component. Use this constant when
	 * configuring a database component with ConfigurationAdmin.
	 */
	String PROP_DATABASE_PID = "org.eclipselabs.emongo.databaseConfigurationProvider";

	/**
	 * The service property key for the unique client ID used on both the client provider and database
	 * configuration provider.
	 */
	String PROP_CLIENT_ID = MongoClientProvider.PROP_CLIENT_ID;

	/**
	 * The service property key for the database alias when configuring the database provider.
	 */
	String PROP_ALIAS = "alias";

	/**
	 * The service property key for the MongoDB database name when configuring the database provider.
	 */
	String PROP_DATABASE = "database";

	/**
	 * The service property key for the MongoDB user when configuring the database provider. Used for
	 * authentication (optional).
	 */
	String PROP_USER = "user";

	/**
	 * The service property key for the MongoDB password when configuring the database provider. Used
	 * for authentication (optional).
	 */
	String PROP_PASSWORD = "password";
}
