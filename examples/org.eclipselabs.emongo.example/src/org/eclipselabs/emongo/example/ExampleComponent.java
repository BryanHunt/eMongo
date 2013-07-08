/**
 * 
 */

package org.eclipselabs.emongo.example;

import org.eclipselabs.emongo.MongoDatabaseLocator;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * @author bhunt
 * 
 */
public class ExampleComponent
{
	private MongoDatabaseLocator databaseLocator;

	public void activate()
	{
		DB database = databaseLocator.getDatabaseByURI("mongodb://localhost/example");
		DBCollection collection = database.getCollection("items");
		collection.drop();

		for (int i = 0; i < 10; i++)
			collection.insert(new BasicDBObject("x", i));

		DBCursor cursor = collection.find();

		while (cursor.hasNext())
			System.out.print(cursor.next().get("x") + " ");

		System.out.println();
	}

	public void bindDatabaseLocator(MongoDatabaseLocator databaseLocator)
	{
		this.databaseLocator = databaseLocator;
	}
}
