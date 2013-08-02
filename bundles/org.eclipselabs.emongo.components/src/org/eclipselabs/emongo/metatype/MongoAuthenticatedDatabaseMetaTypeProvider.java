/**
 * 
 */

package org.eclipselabs.emongo.metatype;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emongo.MongoClientProvider;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
public class MongoAuthenticatedDatabaseMetaTypeProvider implements MetaTypeProvider
{
	private Set<String> mongoClientProviders = new CopyOnWriteArraySet<String>();

	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
	{
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl("client_id", "Client", AttributeDefinition.STRING);
		clientId.setDescription("The MongoDB database client.");
		clientId.setOptionLabels(mongoClientProviders.toArray(new String[0]));
		clientId.setOptionValues(mongoClientProviders.toArray(new String[0]));

		if (!mongoClientProviders.isEmpty())
			clientId.setDefaultValue(new String[] { mongoClientProviders.iterator().next() });

		AttributeDefinitionImpl alias = new AttributeDefinitionImpl("alias", "Alias", AttributeDefinition.STRING);
		alias.setDescription("The alias of the MongoDB database.");

		AttributeDefinitionImpl database = new AttributeDefinitionImpl("database", "Database", AttributeDefinition.STRING);
		database.setDescription("The name MongoDB database.");

		AttributeDefinitionImpl user = new AttributeDefinitionImpl("user", "User", AttributeDefinition.STRING);
		database.setDescription("The user id to use for authenticating to the MongoDB server (optional).");

		AttributeDefinitionImpl password = new AttributeDefinitionImpl("password", "Password", AttributeDefinition.PASSWORD);
		database.setDescription("The user password to use for authenticating to the MongoDB server (optional).");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl("org.eclipselabs.emongo.databaseConfigurationProvider", "MongoDB Database", "MongoDB Database Configuration");
		ocd.addAttribute(clientId);
		ocd.addAttribute(alias);
		ocd.addAttribute(database);
		ocd.addAttribute(user);
		ocd.addAttribute(password);

		return ocd;
	}

	public void bindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		mongoClientProviders.add(mongoClientProvider.getClientId());
	}

	public void unbindMongoClientProvider(MongoClientProvider mongoClientProvider)
	{
		mongoClientProviders.remove(mongoClientProvider.getClientId());
	}
}
