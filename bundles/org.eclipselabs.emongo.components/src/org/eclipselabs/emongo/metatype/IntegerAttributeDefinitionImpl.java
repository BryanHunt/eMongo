/**
 * 
 */

package org.eclipselabs.emongo.metatype;

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
