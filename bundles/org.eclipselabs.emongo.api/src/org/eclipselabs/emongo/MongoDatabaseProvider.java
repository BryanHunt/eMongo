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

package org.eclipselabs.emongo;

import com.mongodb.client.MongoDatabase;

/**
 * A MongoDatabaseProvider allows clients to obtain a reference to a MongoDB
 * database without having to understand the configuration of the database.
 * Instances of MongoDatabaseProvider will be registered as an OSGi service.
 * This service allows the database connection to be configured independent of
 * the client code. It is expected that the client code will be aware of the
 * alias to distinguish between multiple databases.
 * 
 * @author bhunt
 * 
 */
public interface MongoDatabaseProvider
{
	/**
	 * The service property key for the MongoDB client reference filter.
	 */
	String PROP_CLIENT_FILTER = "MongoClientProvider.target";

	/**
	 * The service property key for configuring the database alias. The alias value
	 * may be used by client code to distinguish between multiple databases.
	 */
	String PROP_ALIAS = "alias";

	/**
	 * The service property key for the MongoDB database name when configuring the database provider.
	 */
	String PROP_DATABASE = "databaseName";

	/**
	 * 
	 * @return the database URI in the form: mongodb://host[:port]/database
	 */
	String getURI();

	/**
	 * 
	 * @return the MongoDB database
	 */
	MongoDatabase getDatabase();
}
