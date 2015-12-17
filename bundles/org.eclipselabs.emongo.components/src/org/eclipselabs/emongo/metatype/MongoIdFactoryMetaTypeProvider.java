/*******************************************************************************
 * Copyright (c) 2013 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.metatype;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.MongoIdFactory;
import org.eclipselabs.emongo.components.MongoIdFactoryComponent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {MetaTypeConfiguration.PROP_ID_FACTORY_PID})
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
		AttributeDefinitionImpl database = new AttributeDefinitionImpl(MongoIdFactory.PROP_DATABASE_FILTER, "Database", AttributeDefinition.STRING);
		database.setDescription("The MongoDB database");

		String[] databaseAliases = new String[databases.size()];
		String[] targetFilters = new String[databases.size()];

		databases.toArray(databaseAliases);

		for (int i = 0; i < databaseAliases.length; i++)
			targetFilters[i] = "(" + MongoProvider.PROP_CLIENT_ID + "=" + databaseAliases[i] + ")";

		database.setOptionLabels(databaseAliases);
		database.setOptionValues(targetFilters);

		if (!databases.isEmpty())
			database.setDefaultValue(new String[] { databases.iterator().next() });

		AttributeDefinitionImpl collection = new AttributeDefinitionImpl(MongoIdFactory.PROP_COLLECTION, "Collection", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoIdFactoryComponent.validateCollectionName(value);
			}
		};

		collection.setDescription("The MongoDB collection within the database");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoIdFactory.PID, "MongoDB ID", "MongoDB ID Provider Configuration");
		ocd.addRequiredAttribute(database);
		ocd.addRequiredAttribute(collection);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC )
	public void bindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		databases.add((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
	}

	public void unbindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		databases.remove((String) serviceReference.getProperty(MongoProvider.PROP_CLIENT_ID));
	}
}
