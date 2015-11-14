/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipselabs.emongo.components.MongoClientProviderComponent;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author bhunt
 * 
 */
public class MongoClientProviderComponentTestHarness extends MongoClientProviderComponent
{
	private MongoClient mongoClient;
	private Collection<ServerAddress> serverAddresses;
	private List<MongoCredential> credentials;
	private MongoClientOptions options;

	public MongoClientProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

	public Collection<ServerAddress> getServerAddresses()
	{
		return serverAddresses;
	}

	public List<MongoCredential> getCredentials()
	{
	  return credentials;
	}
	
  public MongoClientOptions getMongoClientOptions()
  {
    return options;
  }

	@Override
	protected MongoClient createMongoClient(ArrayList<ServerAddress> serverAddresses, List<MongoCredential> credentials, MongoClientOptions options)
	{
		this.serverAddresses = serverAddresses;
		this.credentials = credentials;
		this.options = options;
		return mongoClient;
	}

	@Override
	protected MongoClient createMongoClient(ServerAddress serverAddress, List<MongoCredential> credentials, MongoClientOptions options)
	{
		this.serverAddresses = new ArrayList<>(1);
		this.credentials = credentials;
		this.options = options;
		serverAddresses.add(serverAddress);
		return mongoClient;
	}
}
