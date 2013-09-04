/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.ArrayList;
import java.util.Collection;

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
	private MongoClient mongoClient;
	private MongoClientOptions options;
	private Collection<ServerAddress> serverAddresses;

	public MongoClientProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

	public MongoClientOptions getMongoClientOptions()
	{
		return options;
	}

	public Collection<ServerAddress> getServerAddresses()
	{
		return serverAddresses;
	}

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ArrayList<ServerAddress> serverAddresses)
	{
		this.options = options;
		this.serverAddresses = serverAddresses;
		return mongoClient;
	}

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ServerAddress serverAddress)
	{
		this.options = options;
		this.serverAddresses = new ArrayList<>(1);
		serverAddresses.add(serverAddress);
		return mongoClient;
	}
}
