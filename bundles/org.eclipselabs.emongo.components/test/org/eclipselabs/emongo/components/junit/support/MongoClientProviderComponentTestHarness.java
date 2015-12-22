/**
 * 
 */

package org.eclipselabs.emongo.components.junit.support;

import java.util.Collection;

import org.eclipselabs.emongo.components.MongoProviderComponent;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author bhunt
 * 
 */
public class MongoClientProviderComponentTestHarness extends MongoProviderComponent
{
	private MongoClient mongoClient;
	private MongoClientOptions options;

	public MongoClientProviderComponentTestHarness(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

  public MongoClientOptions getMongoClientOptions()
  {
    return options;
  }

	@Override
	protected MongoClient createMongoClient(Collection<String> uris, MongoClientOptions options)
	{
		this.options = options;
		return mongoClient;
	}
}
