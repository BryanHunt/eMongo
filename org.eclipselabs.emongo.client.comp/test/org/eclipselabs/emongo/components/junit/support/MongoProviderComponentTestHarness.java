/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import org.eclipselabs.emongo.client.comp.MongoProviderComponent;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author bhunt
 * 
 */
public class MongoProviderComponentTestHarness extends MongoProviderComponent
{
	private MongoClient mongoClient;
	private MongoClientOptions.Builder optionsBuilder;

	public MongoProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

  public MongoClientOptions.Builder getMongoClientOptions()
  {
    return optionsBuilder;
  }

	@Override
	protected MongoClient createMongoClient(String uri, MongoClientOptions.Builder optionsBuilder)
	{
		this.optionsBuilder = optionsBuilder;
		return mongoClient;
	}
}
