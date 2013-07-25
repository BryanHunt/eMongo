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

package org.eclipselabs.emongo;

import com.mongodb.MongoClient;

/**
 * This OSGi service provides access to a configured MongoDB driver. The MongoDB driver
 * is initialized using the configured service properties.
 * 
 * @author bhunt
 * 
 */
public interface MongoClientProvider
{
	// --- MongoOptions properties ---------------------------------------------------------

	String PROP_DESCRIPTION = "description";
	String PROP_CONNECTIONS_PER_HOST = "connectionsPerHost";
	String PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER = "threadsAllowedToBlockForConnectionMultiplier";
	String PROP_MAX_WAIT_TIME = "maxWaitTime";
	String PROP_CONNECT_TIMEOUT = "connectTimeout";
	String PROP_SOCKET_TIMEOUT = "socketTimeout";
	String PROP_SOCKET_KEEP_ALIVE = "socketKeepAlive";
	String PROP_AUTO_CONNECT_RETRY = "autoConnectRetry";
	String PROP_MAX_AUTO_CONNECT_RETRY_TIME = "maxAutoConnectRetryTime";
	String PROP_CONTINUE_ON_INSERT_ERROR = "continueOnInsertError";
	String PROP_W = "w";
	String PROP_WTIMEOUT = "wtimeout";
	String PROP_FSYNC = "fsync";
	String PROP_J = "j";

	// --- Provider service specific properties ---------------------------------------------

	String PROP_FACTORY_ID = "org.eclipselabs.emongo.clientProvider";
	String PROP_URI = "uri";
	String PROP_CLIENT_ID = "client_id";

	// --------------------------------------------------------------------------------------

	/**
	 * 
	 * @return the MongoDB client driver configured by the service properties
	 */
	MongoClient getMongoClient();

	/**
	 * 
	 * @return list of URIs configured on the client. A single URI will be returned for a single
	 *         MongoDB. Multiple URIs will be returned for a replica set.
	 */
	String[] getURIs();

	String getClientId();
}
