package org.eclipselabs.emongo.components.junit.tests

import static org.mockito.Mockito.*

import org.eclipselabs.emongo.components.DatabaseLocatorComponent
import org.eclipselabs.emongo.components.junit.support.MongoClientProviderComponentTestHarness
import com.mongodb.MongoClient
import com.mongodb.DB
import org.eclipselabs.emongo.MongoClientProvider
import java.util.HashMap

describe DatabaseLocatorComponent
{
	fact "getDatabase returns valid database"
	{
		val db = mock(typeof(DB))
		val mongoClient = mock(typeof(MongoClient))
		val mongoClientProvider = new MongoClientProviderComponentTestHarness(mongoClient)
		val properties = new HashMap<String, Object>()
		
		properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost")
		mongoClientProvider.activate(properties)
		when(mongoClient.getDB("db")).thenReturn(db)
		
		subject.bindMongoClientProvider(mongoClientProvider)
		subject.getDatabase("mongodb://localhost/db") should be db
	}
	
	fact "getDatabase returns null without provider"
	{
		subject.getDatabase("mongodb://localhost/db") should be null
	}

	fact "waitForDatabase returns null without provider"
	{
		subject.waitForDatabase("mongodb://localhost/db", 100) should be null
	}
}