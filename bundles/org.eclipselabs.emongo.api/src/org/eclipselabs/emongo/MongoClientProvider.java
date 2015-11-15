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

	/**
	 * The service property key for: The description for Mongo instances created with these options.
	 * This is used in various places like logging.
	 */
	String PROP_DESCRIPTION = "description";

	/**
	 * The service property key for: The maximum number of connections allowed per host for this Mongo
	 * instance. Those connections will be kept in a pool when idle. Once the pool is exhausted, any
	 * operation requiring a connection will block waiting for an available connection. Default is
	 * 100.
	 */
	String PROP_CONNECTIONS_PER_HOST = "connectionsPerHost";

	/**
	 * The service property key for: This multiplier, multiplied with the connectionsPerHost setting,
	 * gives the maximum number of threads that may be waiting for a connection to become available
	 * from the pool. All further threads will get an exception right away. For example if
	 * connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50
	 * threads can wait for a connection. Default is 5.
	 */
	String PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER = "threadsAllowedToBlockForConnectionMultiplier";

	/**
	 * The service property key for: The maximum wait time in ms that a thread may wait for a
	 * connection to become available. Default is 120,000.
	 */
	String PROP_MAX_WAIT_TIME = "maxWaitTime";

	/**
	 * The service property key for: The connection timeout in milliseconds. It is used solely when
	 * establishing a new connection Socket.connect(java.net.SocketAddress, int) Default is 0 and
	 * means no timeout.
	 */
	String PROP_CONNECT_TIMEOUT = "connectTimeout";

	/**
	 * The service property key for: The socket timeout in milliseconds It is used for I/O socket read
	 * and write operations Socket.setSoTimeout(int) Default is 0 and means no timeout.
	 */
	String PROP_SOCKET_TIMEOUT = "socketTimeout";

	/**
	 * The service property key for: This flag controls the socket keep alive feature that keeps a
	 * connection alive through firewalls Socket.setKeepAlive(boolean) Default is false.
	 */
	String PROP_SOCKET_KEEP_ALIVE = "socketKeepAlive";

	/**
	 * The service property key for: If true, the driver will keep trying to connect to the same
	 * server in case that the socket cannot be established. There is maximum amount of time to keep
	 * retrying, which is 15s by default. This can be useful to avoid some exceptions being thrown
	 * when a server is down temporarily by blocking the operations. It also can be useful to smooth
	 * the transition to a new master (so that a new master is elected within the retry time). Note
	 * that when using this flag: - for a replica set, the driver will trying to connect to the old
	 * master for that time, instead of failing over to the new one right away - this does not prevent
	 * exception from being thrown in read/write operations on the socket, which must be handled by
	 * application Even if this flag is false, the driver already has mechanisms to automatically
	 * recreate broken connections and retry the read operations. Default is false.
	 */
	String PROP_AUTO_CONNECT_RETRY = "autoConnectRetry";

	/**
	 * The service property key for: The maximum amount of time in MS to spend retrying to open
	 * connection to the same server. Default is 0, which means to use the default 15s if
	 * autoConnectRetry is on.
	 */
	String PROP_MAX_AUTO_CONNECT_RETRY_TIME = "maxAutoConnectRetryTime";

	/**
	 * The service property key for: If batch inserts should continue after the first error. Default
	 * is false.
	 */
	String PROP_CONTINUE_ON_INSERT_ERROR = "continueOnInsertError";

	/**
	 * The service property key for: The 'w' value of the global WriteConcern. Default is 0.
	 */
	String PROP_W = "w";

	/**
	 * The service property key for: The 'wtimeout' value of the global WriteConcern. Default is 0.
	 */
	String PROP_WTIMEOUT = "wtimeout";

	/**
	 * The service property key for: The 'fsync' value of the global WriteConcern. Default is false.
	 */
	String PROP_FSYNC = "fsync";

	/**
	 * The service property key for: The 'j' value of the global WriteConcern. Default is false.
	 */
	String PROP_J = "j";

	// --- Provider service specific properties ---------------------------------------------

	/**
	 * The service property key for the client URI. The value may be a single URI for one MongoDB
	 * server, or a CSV of URIs for a MongoDB replica set. The value must have the form:
	 * mongodb://host[:port] [,mongodb://host[:port]]  You may optionally specify a user,
	 * password, and database in which case, the URI would have the form:
	 * user:password@mongodb://host[:port]/database [,user:password@mongodb://host[:port]/database]
	 */
	String PROP_URI = "uri";

	/**
	 * The service property key for the client id. The value must be unique to an OSGi application
	 * instance.
	 */
	String PROP_CLIENT_ID = "clientId";

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

	/**
	 * 
	 * @return the unique client id configured by the service properties
	 */
	String getClientId();
}
