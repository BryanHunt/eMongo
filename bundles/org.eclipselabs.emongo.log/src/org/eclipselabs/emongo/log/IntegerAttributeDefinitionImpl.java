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

import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author bhunt
 * 
 */
public class IntegerAttributeDefinitionImpl extends AttributeDefinitionImpl
{
	private int min;

	public IntegerAttributeDefinitionImpl(String id, String name, int min)
	{
		super(id, name, AttributeDefinition.INTEGER);
		this.min = min;
	}

	@Override
	public String validate(String value)
	{
		try
		{
			int v = Integer.parseInt(value);

			if (v >= min)
				return "";
		}
		catch (NumberFormatException e)
		{}

		return "Value must be a number >= " + min;
	}
}
