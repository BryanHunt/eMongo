/**
 * 
 */

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
