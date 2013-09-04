/**
 * 
 */

package org.eclipselabs.emongo.components;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public class MongoDatabaseProviderComponent implements MongoDatabaseProvider
{
	private MongoDatabaseConfigurationProvider databaseConfigurationProvider;
	private MongoClientProvider mongoClientProvider;
	private String uri;

	/**
	 * @param databaseConfigurationProvider
	 * @param mongoClientProvider
	 */
	public MongoDatabaseProviderComponent(MongoDatabaseConfigurationProvider databaseConfigurationProvider, MongoClientProvider mongoClientProvider)
	{
		super();
		this.databaseConfigurationProvider = databaseConfigurationProvider;
		this.mongoClientProvider = mongoClientProvider;
		uri = mongoClientProvider.getURIs()[0] + "/" + databaseConfigurationProvider.getDatabaseName();
	}

	@Override
	public String getAlias()
	{
		return databaseConfigurationProvider.getAlias();
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public DB getDB()
	{
		DB db = mongoClientProvider.getMongoClient().getDB(databaseConfigurationProvider.getDatabaseName());

		if (databaseConfigurationProvider.getUser() != null && !databaseConfigurationProvider.getUser().isEmpty())
			db.authenticate(databaseConfigurationProvider.getUser(), databaseConfigurationProvider.getPassword().toCharArray());

		return db;
	}
}
