/**
 * 
 */

package org.eclipselabs.emongo.metatype;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emongo.config.MongoDatabaseConfigurationProvider;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
public class MongoIdFactoryMetaTypeProvider implements MetaTypeProvider
{
	Set<String> databases = new CopyOnWriteArraySet<String>();

	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String arg0, String arg1)
	{
		AttributeDefinitionImpl database = new AttributeDefinitionImpl("alias", "Database", AttributeDefinition.STRING);
		database.setCardinality(1);
		database.setDescription("The MongoDB database");
		database.setOptionLabels(databases.toArray(new String[0]));
		database.setOptionValues(databases.toArray(new String[0]));

		if (!databases.isEmpty())
			database.setDefaultValue(new String[] { databases.iterator().next() });

		AttributeDefinitionImpl collection = new AttributeDefinitionImpl("collection", "Collection", AttributeDefinition.STRING);
		collection.setDescription("The MongoDB collection within the database");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl("org.eclipselabs.emongo.idFactory", "MongoDB ID", "MongoDB ID Provider Configuration");
		ocd.addAttribute(database);
		ocd.addAttribute(collection);

		return ocd;
	}

	public void bindMongoDatabaseConfigurationProvider(MongoDatabaseConfigurationProvider mongoDatabaseConfigurationProvider)
	{
		databases.add(mongoDatabaseConfigurationProvider.getAlias());
	}

	public void unbindMongoDatabaseConfigurationProvider(MongoDatabaseConfigurationProvider mongoDatabaseConfigurationProvider)
	{
		databases.remove(mongoDatabaseConfigurationProvider.getAlias());
	}
}
