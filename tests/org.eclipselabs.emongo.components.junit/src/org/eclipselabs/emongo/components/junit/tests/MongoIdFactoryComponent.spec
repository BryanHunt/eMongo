package org.eclipselabs.emongo.components.junit.tests

import static org.mockito.Mockito.*
import org.eclipselabs.emongo.components.MongoIdFactoryComponent
import java.util.Map
import java.util.HashMap
import org.eclipselabs.emongo.MongoIdFactory
import org.eclipselabs.emongo.DatabaseLocator
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import com.mongodb.CommandResult
import org.osgi.service.log.LogService

describe MongoIdFactoryComponent
{
	val Map<String, Object> properties = new HashMap<String, Object>()
	val String uri = "mongodb://localhost/db/collection"
	val DatabaseLocator databaseLocator = mock(typeof(DatabaseLocator))
	val DB db = mock(typeof(DB))
	val DBCollection collection = mock(typeof(DBCollection))
	val DBObject id = new BasicDBObject("_id", "0")

	before
	{
		subject.bindDatabaseLocator(databaseLocator)
		properties.put(MongoIdFactory::PROP_URI, uri)
	}
	
	context "normal operation"
	{			
		before
		{
			val CommandResult commandResult = mock(typeof(CommandResult))
			
			when(databaseLocator.getDatabase(uri)).thenReturn(db)
			when(db.getCollection("collection")).thenReturn(collection)
			when(db.getLastError()).thenReturn(commandResult)
			when(commandResult.ok()).thenReturn(true)
			
			id.put("lastId", Long::valueOf(0))
		}
		
		fact "configure must create an ID entry when none exists in the collection"
		{
			val DBObject query = new BasicDBObject("_id", "0")
			when(collection.findOne(query)).thenReturn(null)
	
			subject.configure(properties)
			verify(collection).insert(id)
		}
		
		fact "configure must use the an existing ID entry from the collection"
		{
			val DBObject query = new BasicDBObject("_id", "0")
			val DBObject update = new BasicDBObject("$inc", new BasicDBObject("lastId", Long::valueOf(1)))
			
			when(collection.findOne(query)).thenReturn(id)
			subject.configure(properties)
			verify(collection, never()).insert(id)
	
			id.put("lastId", Long::valueOf(1))
			when(collection.findAndModify(query, null, null, false, update, true, false)).thenReturn(id)
			subject.nextId should be "1"
		}
	}
	
	context "configuration exception without logging"
	{
		fact "configure throws exception when URI is missing the collection name"
		{
			properties.put(MongoIdFactory::PROP_URI, "mongodb://localhost/db/")
			subject.configure(properties) throws IllegalStateException
		}
	
		fact "configure throws exception when URI is missing the collection segment"
		{
			properties.put(MongoIdFactory::PROP_URI, "mongodb://localhost/db")
			subject.configure(properties) throws IllegalStateException
		}
		
		fact "configure throws exception when the database could not be found"
		{
			when(databaseLocator.getDatabase(uri)).thenReturn(null)
			subject.configure(properties) throws IllegalStateException			
		}
		
		fact "configure throws exception when getLastError() returns not ok"
		{
			val DBObject query = new BasicDBObject("_id", "0")
			id.put("lastId", Long::valueOf(0))
			val CommandResult commandResult = mock(typeof(CommandResult))
			
			when(databaseLocator.getDatabase(uri)).thenReturn(db)
			when(db.getCollection("collection")).thenReturn(collection)
			when(collection.findOne(query)).thenReturn(null)
			when(db.getLastError()).thenReturn(commandResult)
			when(commandResult.ok()).thenReturn(false)
	
			subject.configure(properties) throws IllegalStateException
			verify(collection).insert(id)
		}
	}
	
	context "configuration exception with logging"
	{
		val LogService logService = mock(typeof(LogService))
		before subject.bindLogService(logService)

		fact "configure throws exception when URI is missing the collection name"
		{
			properties.put(MongoIdFactory::PROP_URI, "mongodb://localhost/db/")
			subject.configure(properties) throws IllegalStateException
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
	
		fact "configure throws exception when URI is missing the collection segment"
		{
			properties.put(MongoIdFactory::PROP_URI, "mongodb://localhost/db")
			subject.configure(properties) throws IllegalStateException
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
		
		fact "configure throws exception when the database could not be found"
		{
			when(databaseLocator.getDatabase(uri)).thenReturn(null)
			subject.configure(properties) throws IllegalStateException			
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
		
		fact "configure throws exception when getLastError() returns not ok"
		{
			val DBObject query = new BasicDBObject("_id", "0")
			id.put("lastId", Long::valueOf(0))
			val CommandResult commandResult = mock(typeof(CommandResult))
			
			when(databaseLocator.getDatabase(uri)).thenReturn(db)
			when(db.getCollection("collection")).thenReturn(collection)
			when(collection.findOne(query)).thenReturn(null)
			when(db.getLastError()).thenReturn(commandResult)
			when(commandResult.ok()).thenReturn(false)
	
			subject.configure(properties) throws IllegalStateException
			verify(collection).insert(id)
			verify(logService).log(eq(LogService::LOG_ERROR), anyString())
		}
	}
}