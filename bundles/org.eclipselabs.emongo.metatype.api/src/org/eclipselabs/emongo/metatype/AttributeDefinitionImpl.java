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

import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author bhunt
 * 
 */
public class AttributeDefinitionImpl implements AttributeDefinition
{
	private int cardinality;
	private String[] defaultValue;
	private String description;
	private String id;
	private String name;
	private String[] optionLabels;
	private String[] optionValues;
	private int type;

	public AttributeDefinitionImpl(String id, String name, int type)
	{
		this.id = id;
		this.name = name;
		this.type = type;
	}

	@Override
	public int getCardinality()
	{
		return cardinality;
	}

	@Override
	public String[] getDefaultValue()
	{
		return defaultValue;
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
	public String getName()
	{
		return name;
	}

	@Override
	public String[] getOptionLabels()
	{
		return optionLabels;
	}

	@Override
	public String[] getOptionValues()
	{
		return optionValues;
	}

	@Override
	public int getType()
	{
		return type;
	}

	public void setCardinality(int cardinality)
	{
		this.cardinality = cardinality;
	}

	public void setDefaultValue(String[] defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setOptionLabels(String[] optionLabels)
	{
		this.optionLabels = optionLabels;
	}

	public void setOptionValues(String[] optionValues)
	{
		this.optionValues = optionValues;
	}

	@Override
	public String validate(String arg0)
	{
		return null;
	}
}
