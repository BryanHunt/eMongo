package org.eclipselabs.emongo.components.junit.tests

import static org.mockito.Mockito.*
import static org.hamcrest.Matchers.*

import org.eclipselabs.emongo.components.MongoClientProviderComponent
import java.util.HashMap
import org.eclipselabs.emongo.components.junit.support.MongoClientProviderComponentTestHarness
import com.mongodb.MongoClient
import org.eclipselabs.emongo.MongoClientProvider
import org.osgi.service.log.LogService

describe MongoClientProviderComponent
{
	val properties = new HashMap<String, Object>()
	val mongoClient = mock(typeof(MongoClient))
	val subject = new MongoClientProviderComponentTestHarness(mongoClient)
	
	context "single database instance"
	{
		before properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost")
		
		fact "configuration parameters are available through the API"
		{
			subject.activate(properties)
			subject.URIs.size should be 1
			subject.URIs.get(0) should be "mongodb://localhost"
		}
		
		fact "a single MongoClient is created on activation"
		{
			subject.activate(properties)
			subject.mongoClient should be mongoClient
			val MongoClientProviderComponentTestHarness target = subject as MongoClientProviderComponentTestHarness
			target.serverAddress.host should be "localhost"
			target.serverAddresses should be null
		}
	}
	
	context "database replica set"
	{
		before properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost:27001,mongodb://localhost:27002, mongodb://localhost:27003 ,mongodb://localhost:27004 , mongodb://localhost:27005")
		
		fact "configuration parameters are available through the API"
		{
			subject.activate(properties)
			subject.URIs.size should be 5
		}
	}
	
	context "configuration exceptions without logging"
	{
		fact "configure throws exception when URI is missing"
		{
			properties.put(MongoClientProvider::PROP_URI, null)
			subject.activate(properties) throws IllegalStateException
		}

		fact "configuration throws exception when URI is empty"
		{
			properties.put(MongoClientProvider::PROP_URI, "")
			subject.activate(properties) throws IllegalStateException
		}
		
		fact "configuration throws exception when URI does not start with mongodb://"
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodd://localhost")
			subject.activate(properties) throws IllegalStateException			
		}
		
		fact "configuration throws exception when URI is not properly formatted"
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost/")
			subject.activate(properties) throws IllegalStateException			
		}
	}

	context "configuration exceptions with logging"
	{
		val logService = mock(typeof(LogService))
		before subject.bindLogService(logService)

		fact "configure throws exception when URI is missing"
		{
			properties.put(MongoClientProvider::PROP_URI, null)
			subject.activate(properties) throws IllegalStateException
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}

		fact "configuration throws exception when URI is empty"
		{
			properties.put(MongoClientProvider::PROP_URI, "")
			subject.activate(properties) throws IllegalStateException
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
		
		fact "configuration throws exception when URI does not start with mongodb://"
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodd://localhost")
			subject.activate(properties) throws IllegalStateException			
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
		
		fact "configuration throws exception when URI is not properly formatted"
		{
			properties.put(MongoClientProvider::PROP_URI, "mongodb://localhost/")
			subject.activate(properties) throws IllegalStateException			
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
	}

}