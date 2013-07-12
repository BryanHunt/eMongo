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
	private MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider;
	private MongoClientProvider mongoClientProvider;

	/**
	 * @param databaseConfigurationProvider
	 * @param mongoClientProvider
	 */
	public MongoDatabaseProviderComponent(MongoAuthenticatedDatabaseConfigurationProvider databaseConfigurationProvider, MongoClientProvider mongoClientProvider)
	{
		super();
		this.databaseConfigurationProvider = databaseConfigurationProvider;
		this.mongoClientProvider = mongoClientProvider;
	}

	@Override
	public String getAlias()
	{
		return databaseConfigurationProvider.getAlias();
	}

	@Override
	public DB getDB()
	{
		DB db = mongoClientProvider.getMongoClient().getDB(databaseConfigurationProvider.getDatabaseName());

		if (databaseConfigurationProvider.getUser() != null)
			db.authenticate(databaseConfigurationProvider.getUser(), databaseConfigurationProvider.getPassword().toCharArray());

		return db;
	}
}
