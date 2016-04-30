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
public class LongAttributeDefinitionImpl extends AttributeDefinitionImpl
{
	private long min;

	public LongAttributeDefinitionImpl(String id, String name, long min)
	{
		super(id, name, AttributeDefinition.LONG);
		this.min = min;
	}

	@Override
	public String validate(String value)
	{
		try
		{
			long v = Long.parseLong(value);

			if (v >= min)
				return "";
		}
		catch (NumberFormatException e)
		{}

		return "Value must be a number >= " + min;
	}
}
