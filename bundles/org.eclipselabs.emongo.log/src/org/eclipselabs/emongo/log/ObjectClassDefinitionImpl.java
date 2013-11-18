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

package org.eclipselabs.emongo.log;

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
	private Collection<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
	private String description;
	private String id;
	private String name;

	public ObjectClassDefinitionImpl(String id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public void addAttribute(AttributeDefinition attributeDefinition)
	{
		attributeDefinitions.add(attributeDefinition);
	}

	@Override
	public AttributeDefinition[] getAttributeDefinitions(int arg0)
	{
		return attributeDefinitions.toArray(new AttributeDefinition[0]);
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
