package org.eclipselabs.emongo.components.junit.tests

import static org.mockito.Mockito.*
import static org.hamcrest.Matchers.*

import org.eclipselabs.emongo.components.MongoClientProviderComponent
import java.util.Map
import java.util.HashMap
import org.eclipselabs.emongo.components.junit.support.MongoClientProviderComponentTestHarness
import com.mongodb.MongoClient
import org.eclipselabs.emongo.MongoClientProvider

describe MongoClientProviderComponent
{
	val Map<String, Object> properties = new HashMap<String, Object>()
	val MongoClient mongoClient = mock(typeof(MongoClient))
	val MongoClientProviderComponent subject = new MongoClientProviderComponentTestHarness(mongoClient)
	
	context "single database instance"
	{
		before
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost")
		}
		
		fact "configuration parameters are available through the API"
		{
			subject.activate(properties)
			subject.URIs.size should be 1
			subject.URIs.get(0) should be "mongodb://localhost"
		}
	}
	
	context "database replica set"
	{
		before
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost:27001,mongodb://localhost:27002, mongodb://localhost:27003 ,mongodb://localhost:27004 , mongodb://localhost:27005")
		}
		
		fact "configuration parameters are available through the API"
		{
			subject.activate(properties)
			subject.URIs.size should be 5
		}
	}
}