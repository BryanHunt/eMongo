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

package org.eclipselabs.emongo.metatype.components;

import org.eclipselabs.emongo.MongoAdmin;
import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.metatype.AttributeDefinitionImpl;
import org.eclipselabs.emongo.metatype.DatabaseSelectorMetatypeProvider;
import org.eclipselabs.emongo.metatype.ObjectClassDefinitionImpl;
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
@Component(service = MetaTypeProvider.class, property = {MetaTypeConfiguration.PROP_MONITOR_PID})
public class MongoDatabaseMonitorMetaTypeProvider extends DatabaseSelectorMetatypeProvider
{
	@Override
	public ObjectClassDefinition getObjectClassDefinition(String arg0, String arg1)
	{
		AttributeDefinitionImpl updateInterval = new AttributeDefinitionImpl("updateInterval", "Update Interval", AttributeDefinition.INTEGER)
		{
			@Override
			public String validate(String value)
			{
				return validateUpdateInterval(value);
			}
		};

		updateInterval.setDescription("The interval in which to sample the database stats in minutes");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoAdmin.MONITOR_PID, "MongoDB Monitor", "MongoDB Monitor Configuration");
		ocd.addRequiredAttribute(createDatabaseSelector());
		ocd.addOptionalAttribute(updateInterval);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void bindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		super.bindMongoProvider(serviceReference);
	}

	public void unbindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		super.unbindMongoProvider(serviceReference);
	}
	
  private String validateUpdateInterval(String stringValue)
  {
    try
    {
      int value = Integer.parseInt(stringValue);

      if (value < 1)
        return "The update interval must be > 0";

      return null;
    }
    catch (NumberFormatException e)
    {
      return "The update interval must be an integer > 0";
    }
  }


}
