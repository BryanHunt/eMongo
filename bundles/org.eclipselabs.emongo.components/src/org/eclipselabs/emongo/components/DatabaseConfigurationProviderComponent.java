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
public class DatabaseConfigurationProviderComponent extends AbstractComponent implements DatabaseConfigurationProvider
{
	private volatile String alias;
	private volatile String databaseName;
	private volatile String uri;
	private volatile String user;
	private volatile String password;

	public void configure(Map<String, Object> properties)
	{
		alias = (String) properties.get(DatabaseConfigurationProvider.PROP_ALIAS);
		uri = (String) properties.get(DatabaseConfigurationProvider.PROP_URI);
		user = (String) properties.get(DatabaseConfigurationProvider.PROP_USER);
		password = (String) properties.get(DatabaseConfigurationProvider.PROP_PASSWORD);

		if (alias == null || alias.isEmpty())
			handleIllegalConfiguration("The database alias was not found in the configuration properties");

		if (uri == null || uri.isEmpty())
			handleIllegalConfiguration("The MongoDB uri was not found in the configuration properties");

		// The URI will be of the form: mongodb://host[:port]/db
		// When the string is split on / the URI must have 4 parts

		String[] uriElements = uri.split("/");

		if (!uri.startsWith("mongodb://") || uriElements.length != 4)
			handleIllegalConfiguration("The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/db'");

		databaseName = uriElements[3];
	}

	@Override
	public String getAlias()
	{
		return alias;
	}

	@Override
	public String getDatabaseName()
	{
		return databaseName;
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
