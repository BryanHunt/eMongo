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

import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.components.MongoDatabaseMonitorComponent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {MetaTypeConfiguration.PROP_MONITOR_PID})
public class MongoDatabaseMonitorMetaTypeProvider implements MetaTypeProvider
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
		AttributeDefinitionImpl database = new AttributeDefinitionImpl(MongoDatabaseMonitorComponent.PROP_DATABASE_FILTER, "Database", AttributeDefinition.STRING);
		database.setDescription("The MongoDB database");

		String[] databaseAliases = new String[databases.size()];
		String[] targetFilters = new String[databases.size()];

		databases.toArray(databaseAliases);

		for (int i = 0; i < databaseAliases.length; i++)
			targetFilters[i] = "(" + MongoClientProvider.PROP_CLIENT_ID + "=" + databaseAliases[i] + ")";

		database.setOptionLabels(databaseAliases);
		database.setOptionValues(targetFilters);

		if (!databases.isEmpty())
			database.setDefaultValue(new String[] { databases.iterator().next() });

		AttributeDefinitionImpl updateInterval = new AttributeDefinitionImpl(MongoDatabaseMonitorComponent.PROP_UPDATE_INTERVAL, "Update Interval", AttributeDefinition.INTEGER)
		{
			@Override
			public String validate(String value)
			{
				return MongoDatabaseMonitorComponent.validateUpdateInterval(value);
			}
		};

		updateInterval.setDescription("The interval in which to sample the database stats in minutes");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoAdmin.MONITOR_PID, "MongoDB Monitor", "MongoDB Monitor Configuration");
		ocd.addRequiredAttribute(database);
		ocd.addOptionalAttribute(updateInterval);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	public void bindMongoClientProvider(ServiceReference<MongoClientProvider> serviceReference)
	{
		databases.add((String) serviceReference.getProperty(MongoClientProvider.PROP_CLIENT_ID));
	}

	public void unbindMongoClientProvider(ServiceReference<MongoClientProvider> serviceReference)
	{
		databases.remove((String) serviceReference.getProperty(MongoClientProvider.PROP_CLIENT_ID));
	}
}
