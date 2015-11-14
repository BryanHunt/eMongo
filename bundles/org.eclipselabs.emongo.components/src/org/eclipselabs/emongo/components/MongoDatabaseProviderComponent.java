/**
 * 
 */

package org.eclipselabs.emongo.components;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@Component(service = MongoDatabaseProvider.class, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = {"org.eclipselabs.emongo.databaseProvider"})
public class MongoDatabaseProviderComponent extends AbstractComponent implements MongoDatabaseProvider
{
  public @interface DatabaseConfig
  {
    String alias();
    String databaseName();
  }

	private volatile String alias;
	private volatile String databaseName;
	private String uri;
	private MongoClientProvider mongoClientProvider;

	public static String validateAlias(String value)
	{
		if (value == null || value.isEmpty())
			return "The database alias was not found in the configuration properties";

		return null;
	}

	public static String validateDatabaseName(String value)
	{
		if (value == null || value.isEmpty())
			return "The MongoDB database name was not found in the configuration properties";

		return null;
	}

	@Activate
	public void activate(DatabaseConfig config)
	{
		alias = config.alias();
		handleIllegalConfiguration(validateAlias(alias));

		databaseName = config.databaseName();
		handleIllegalConfiguration(validateDatabaseName(databaseName));

		uri = mongoClientProvider.getURIs()[0] + "/" + databaseName;
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public MongoDatabase getDatabase()
	{
	  return mongoClientProvider.getMongoClient().getDatabase(databaseName);
	}

  @Reference(cardinality = ReferenceCardinality.OPTIONAL)
  public void bindLogService(LogService logService)
  {
    super.bindLogService(logService);;
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }

  @Reference(unbind = "-")
	public void bindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		this.mongoClientProvider = mongoClientProvider;
	}
}
