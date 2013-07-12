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

package org.eclipselabs.emongo.configurator;

/**
 * @author bhunt
 * 
 */
public class ConfigurationException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ConfigurationException(String message)
	{
		super(message);
	}

	public ConfigurationException(Throwable cause)
	{
		super(cause);
	}

	public ConfigurationException(String message, Throwable arg1)
	{
		super(message, arg1);
	}
}
