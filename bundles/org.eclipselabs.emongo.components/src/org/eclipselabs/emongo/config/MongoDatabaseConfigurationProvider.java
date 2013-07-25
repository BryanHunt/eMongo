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

package org.eclipselabs.emongo.config;

import org.eclipselabs.emongo.MongoClientProvider;

/**
 * @author bhunt
 * 
 */
public interface MongoDatabaseConfigurationProvider
{
	String PROP_FACTORY_ID = "org.eclipselabs.emongo.databaseConfigurationProvider";
	String PROP_CLIENT_ID = MongoClientProvider.PROP_CLIENT_ID;
	String PROP_ALIAS = "alias";
	String PROP_DATABASE = "database";

	String getAlias();

	String getDatabaseName();

	String getClientId();
}
