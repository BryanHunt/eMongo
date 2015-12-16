/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipselabs.emongo.components.MongoProviderComponent;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author bhunt
 * 
 */
public class MongoClientProviderComponentTestHarness extends MongoProviderComponent
{
	private MongoClient mongoClient;
	private Collection<ServerAddress> serverAddresses;
	private MongoClientOptions options;

	public MongoClientProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

	public Collection<ServerAddress> getServerAddresses()
	{
		return serverAddresses;
	}

  public MongoClientOptions getMongoClientOptions()
  {
    return options;
  }

	@Override
	protected MongoClient createMongoClient(ArrayList<ServerAddress> serverAddresses, MongoClientOptions options)
	{
		this.serverAddresses = serverAddresses;
		this.options = options;
		return mongoClient;
	}

	@Override
	protected MongoClient createMongoClient(ServerAddress serverAddress, MongoClientOptions options)
	{
		this.serverAddresses = new ArrayList<>(1);
		this.options = options;
		serverAddresses.add(serverAddress);
		return mongoClient;
	}
}
