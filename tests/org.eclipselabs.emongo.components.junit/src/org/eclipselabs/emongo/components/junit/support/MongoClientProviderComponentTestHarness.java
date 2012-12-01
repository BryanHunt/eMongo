/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.ArrayList;

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

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ArrayList<ServerAddress> serverAddresses)
	{
		return mongoClient;
	}

	@Override
	protected MongoClient createMongoClient(MongoClientOptions options, ServerAddress serverAddress)
	{
		return mongoClient;
	}

	private MongoClient mongoClient;
}
