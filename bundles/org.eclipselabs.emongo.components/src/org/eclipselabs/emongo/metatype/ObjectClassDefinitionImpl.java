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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
public class ObjectClassDefinitionImpl implements ObjectClassDefinition
{
  private Collection<AttributeDefinition> allAttributeDefinitions = new ArrayList<AttributeDefinition>();  
	private Collection<AttributeDefinition> requiredAttributeDefinitions = new ArrayList<AttributeDefinition>();
  private Collection<AttributeDefinition> optionalAttributeDefinitions = new ArrayList<AttributeDefinition>();
	private String description;
	private String id;
	private String name;

	public ObjectClassDefinitionImpl(String id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public void addRequiredAttribute(AttributeDefinition attributeDefinition)
	{
    allAttributeDefinitions.add(attributeDefinition);
	  requiredAttributeDefinitions.add(attributeDefinition);
	}

  public void addOptionalAttribute(AttributeDefinition attributeDefinition)
  {
    allAttributeDefinitions.add(attributeDefinition);
    optionalAttributeDefinitions.add(attributeDefinition);
  }

  @Override
	public AttributeDefinition[] getAttributeDefinitions(int filter)
	{
    switch(filter)
    {
      case ObjectClassDefinition.REQUIRED:
        return requiredAttributeDefinitions.toArray(new AttributeDefinition[0]);
      case ObjectClassDefinition.OPTIONAL:
        return optionalAttributeDefinitions.toArray(new AttributeDefinition[0]);
      case ObjectClassDefinition.ALL:
      default:
        return allAttributeDefinitions.toArray(new AttributeDefinition[0]);
    }
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public InputStream getIcon(int arg0) throws IOException
	{
		return null;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
