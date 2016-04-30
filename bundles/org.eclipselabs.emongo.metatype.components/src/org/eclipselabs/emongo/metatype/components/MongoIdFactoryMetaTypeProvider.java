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

import org.eclipselabs.emongo.MongoIdFactory;
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
@Component(service = MetaTypeProvider.class, property = {MetaTypeConfiguration.PROP_ID_FACTORY_PID})
public class MongoIdFactoryMetaTypeProvider extends DatabaseSelectorMetatypeProvider
{
	@Override
	public ObjectClassDefinition getObjectClassDefinition(String arg0, String arg1)
	{
		AttributeDefinitionImpl collection = new AttributeDefinitionImpl(MongoIdFactory.PROP_COLLECTION, "Collection", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return validateCollectionName(value);
			}
		};

		collection.setDescription("The MongoDB collection within the database");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoIdFactory.PID, "MongoDB ID", "MongoDB ID Provider Configuration");
		ocd.addRequiredAttribute(createDatabaseSelector());
		ocd.addRequiredAttribute(collection);

		return ocd;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC )
	public void bindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		super.bindMongoProvider(serviceReference);
	}

	public void unbindMongoProvider(ServiceReference<MongoProvider> serviceReference)
	{
		super.unbindMongoProvider(serviceReference);
	}
	
  private String validateCollectionName(String value)
  {
    if (value == null || value.isEmpty())
      return "The collection was not specified as part of the component configuration";
  
    return null;
  }
}
