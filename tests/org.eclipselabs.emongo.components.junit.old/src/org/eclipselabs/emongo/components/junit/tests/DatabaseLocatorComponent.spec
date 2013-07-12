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
	val db = mock(typeof(DB))
	val mongoClient = mock(typeof(MongoClient))
	val mongoClientProvider = new MongoClientProviderComponentTestHarness(mongoClient)
	val properties = new HashMap<String, Object>()

	before
	{
		properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost")
		mongoClientProvider.activate(properties)
		when(mongoClient.getDB("db")).thenReturn(db)		
	}
	
	context "normal operation"
	{
		fact "getDatabase returns valid database without collection specified"
		{
			subject.bindMongoClientProvider(mongoClientProvider)
			subject.getDatabase("mongodb://localhost/db") should be db
		}
		
		fact "getDatabase returns valid database with collection specified"
		{
			subject.bindMongoClientProvider(mongoClientProvider)
			subject.getDatabase("mongodb://localhost/db/collection") should be db
		}
	
		fact "waitForDatabase returns valid database without collection specified"
		{
			subject.bindMongoClientProvider(mongoClientProvider)
			subject.waitForDatabase("mongodb://localhost/db", 0) should be db
		}
	
		fact "waitForDatabase returns valid database with collection specified"
		{
			subject.bindMongoClientProvider(mongoClientProvider)
			subject.waitForDatabase("mongodb://localhost/db/collection", 0) should be db
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
	
	context "bad uri"
	{
		fact "getDatabase throws exception if the uri does not start with mongodb://"
		{
			subject.getDatabase("mongodd://localhost/db") throws IllegalArgumentException
		}
	
		fact "getDatabase throws exception if the uri is missing the database"
		{
			subject.getDatabase("mongodb://localhost") throws IllegalArgumentException
		}
	
		fact "getDatabase throws exception if the database name is empty"
		{
			subject.getDatabase("mongodb://localhost/") throws IllegalArgumentException
		}
	
		fact "waitForDatabase throws exception if the uri does not start with mongodb://"
		{
			subject.waitForDatabase("mongodd://localhost/db", 100) throws IllegalArgumentException
		}
	}
}