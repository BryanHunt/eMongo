/**
 * 
 */

package org.eclipselabs.emongo.components;

import java.util.Map;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public class MongoDatabaseProviderComponent extends AbstractComponent implements MongoDatabaseProvider
{
	private volatile String alias;
	private volatile String databaseName;
	private volatile String user;
	private volatile String password;
	private String uri;
	private MongoClientProvider mongoClientProvider;

	/**
	 * @param databaseConfigurationProvider
	 * @param mongoClientProvider
	 */
	public void activate(Map<String, Object> properties)
	{
		alias = (String) properties.get(PROP_ALIAS);
		databaseName = (String) properties.get(PROP_DATABASE);
		user = (String) properties.get(PROP_USER);
		password = (String) properties.get(PROP_PASSWORD);

		if (alias == null || alias.isEmpty())
			handleIllegalConfiguration("The database alias was not found in the configuration properties");

		if (databaseName == null || databaseName.isEmpty())
			handleIllegalConfiguration("The MongoDB database name was not found in the configuration properties");

		uri = mongoClientProvider.getURIs()[0] + "/" + databaseName;
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public DB getDB()
	{
		DB db = mongoClientProvider.getMongoClient().getDB(databaseName);

		if (user != null && !user.isEmpty())
			db.authenticate(user, password.toCharArray());

		return db;
	}

	public void bindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		this.mongoClientProvider = mongoClientProvider;
	}
}
