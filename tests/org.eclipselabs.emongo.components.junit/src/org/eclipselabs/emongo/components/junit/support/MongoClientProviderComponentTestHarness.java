/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipselabs.emongo.components.MongoClientProviderComponent;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author bhunt
 * 
 */
public class MongoClientProviderComponentTestHarness extends MongoClientProviderComponent
{
	public MongoClientProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

	public ServerAddress getServerAddress()
	{
		return serverAddress;
	}

	public List<ServerAddress> getServerAddresses()
	{
		return serverAddresses;
	}

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ArrayList<ServerAddress> serverAddresses)
	{
		this.serverAddresses = serverAddresses;
		return mongoClient;
	}

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ServerAddress serverAddress)
	{
		this.serverAddress = serverAddress;
		return mongoClient;
	}

	private MongoClient mongoClient;
	private ServerAddress serverAddress;
	private List<ServerAddress> serverAddresses;
}
