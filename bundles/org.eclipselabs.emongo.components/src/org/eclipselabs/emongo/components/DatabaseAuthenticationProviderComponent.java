/*******************************************************************************
 * Copyright (c) 2012 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.components;

import java.util.Map;

/**
 * @author bhunt
 * 
 */
public class DatabaseAuthenticationProviderComponent extends AbstractComponent implements DatabaseAuthenticationProvider
{
	private volatile String uri;
	private volatile String user;
	private volatile String password;

	public DatabaseAuthenticationProviderComponent()
	{}

	public void configure(Map<String, Object> properties)
	{
		uri = (String) properties.get(DatabaseAuthenticationProvider.PROP_URI);
		user = (String) properties.get(DatabaseAuthenticationProvider.PROP_USER);
		password = (String) properties.get(DatabaseAuthenticationProvider.PROP_PASSWORD);
	
		if (uri == null || uri.isEmpty())
			handleIllegalConfiguration("The MongoDB uri was not found in the configuration properties");
	
		if (user == null || user.isEmpty())
			handleIllegalConfiguration("The MongoDB user was not found in the configuration properties");
	
		if (password == null || password.isEmpty())
			handleIllegalConfiguration("The MongoDB password was not found in the configuration properties");
	
		// The URI will be of the form: mongodb://host[:port]/db
		// When the string is split on / the URI must have 4 parts
	
		if (!uri.startsWith("mongodb://") || uri.split("/").length != 4)
			handleIllegalConfiguration("The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/db'");
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public String getUser()
	{
		return user;
	}

	@Override
	public String getPassword()
	{
		return password;
	}
}
