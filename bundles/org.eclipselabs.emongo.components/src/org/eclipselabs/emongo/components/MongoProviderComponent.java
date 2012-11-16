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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipselabs.emongo.MongoProvider;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

/**
 * @author bhunt
 * 
 */
public class MongoProviderComponent extends AbstractComponent implements MongoProvider
{
	private volatile String[] uris;
	private volatile Mongo mongo;

	@Override
	public Mongo getMongo()
	{
		return mongo;
	}

	@Override
	public String[] getURIs()
	{
		return uris;
	}

	public void activate(Map<String, Object> properties)
	{
		MongoOptions options = createMongoOptions(properties);

		// The uriProperty is a single string containing one or more server URIs.
		// When more than one URI is specified, it denotes a replica set and the
		// URIs must be separated by a comma (CSV).

		String uriProperty = (String) properties.get(PROP_URI);

		if (uriProperty == null || uriProperty.isEmpty())
			handleIllegalConfiguration("The MongoDB URI was not found in the configuration properties");

		// The regex \s matches whitepsace.  The extra \ is needed because of how it's treated in java strings.
		// The split is done on any number of whitespace chars followed by a comma followed by any number of
		// whitespace chars.  What is left is the URI(s).

		uris = uriProperty.split("\\s*,\\s*");
		String currentURI = null;

		try
		{
			if (uris.length == 1)
			{
				currentURI = uris[0].trim();

				// The URI will be of the form: mongodb://host[:port]/db
				// When the string is split on / the URI must have 4 parts

				if (!currentURI.startsWith("mongodb://") && currentURI.split("/").length != 4)
					handleIllegalConfiguration("The uri: '" + currentURI + "' does not have the form 'mongodb://host[:port]/db'");

				ServerAddress serverAddress = createServerAddress(currentURI);
				mongo = createMongo(options, serverAddress);
			}
			else
			{
				ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>(uris.length);

				for (String uri : uris)
				{
					currentURI = uri.trim();

					// The URI will be of the form: mongodb://host[:port]/db
					// When the string is split on / the URI must have 4 parts

					if (!currentURI.startsWith("mongodb://") && currentURI.split("/").length != 4)
						handleIllegalConfiguration("The uri: '" + currentURI + "' does not have the form 'mongodb://host[:port]/db'");

					serverAddresses.add(createServerAddress(currentURI));
				}

				mongo = createMongo(options, serverAddresses);
			}
		}
		catch (UnknownHostException e)
		{
			handleConfigurationException("The URI: '" + currentURI + "' has a bad hostname", e);
		}
		catch (URISyntaxException e)
		{
			handleConfigurationException("The URI: '" + currentURI + "' is not a proper URI", e);
		}
	}

	public void deactivate()
	{
		if (mongo != null)
			mongo.close();
	}

	protected Mongo createMongo(MongoOptions options, ArrayList<ServerAddress> serverAddresses)
	{
		return new Mongo(serverAddresses, options);
	}

	protected Mongo createMongo(MongoOptions options, ServerAddress serverAddress)
	{
		return new Mongo(serverAddress, options);
	}

	private MongoOptions createMongoOptions(Map<String, Object> properties)
	{
		MongoOptions options = new MongoOptions();

		String description = (String) properties.get(PROP_DESCRIPTION);

		if (description != null)
			options.description = description;

		Integer connectionsPerHost = (Integer) properties.get(PROP_CONNECTIONS_PER_HOST);

		if (connectionsPerHost != null)
			options.connectionsPerHost = connectionsPerHost;

		Integer threadsAllowedToBlockForConnectionMultiplier = (Integer) properties.get(PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER);

		if (threadsAllowedToBlockForConnectionMultiplier != null)
			options.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;

		Integer maxWaitTime = (Integer) properties.get(PROP_MAX_WAIT_TIME);

		if (maxWaitTime != null)
			options.maxWaitTime = maxWaitTime;

		Integer connectTimeout = (Integer) properties.get(PROP_CONNECT_TIMEOUT);

		if (connectTimeout != null)
			options.connectTimeout = connectTimeout;

		Integer socketTimeout = (Integer) properties.get(PROP_SOCKET_TIMEOUT);

		if (socketTimeout != null)
			options.socketTimeout = socketTimeout;

		Boolean socketKeepAlive = (Boolean) properties.get(PROP_SOCKET_KEEP_ALIVE);

		if (socketKeepAlive != null)
			options.socketKeepAlive = socketKeepAlive;

		Boolean autoConnectRetry = (Boolean) properties.get(PROP_AUTO_CONNECT_RETRY);

		if (autoConnectRetry != null)
			options.autoConnectRetry = autoConnectRetry;

		Long maxAutoConnectRetryTime = (Long) properties.get(PROP_MAX_AUTO_CONNECT_RETRY_TIME);

		if (maxAutoConnectRetryTime != null)
			options.maxAutoConnectRetryTime = maxAutoConnectRetryTime;

		Boolean safe = (Boolean) properties.get(PROP_SAFE);

		if (safe != null)
			options.safe = safe;

		Integer w = (Integer) properties.get(PROP_W);

		if (w != null)
			options.w = w;

		Integer wtimeout = (Integer) properties.get(PROP_WTIMEOUT);

		if (wtimeout != null)
			options.wtimeout = wtimeout;

		Boolean fsync = (Boolean) properties.get(PROP_FSYNC);

		if (fsync != null)
			options.fsync = fsync;

		Boolean j = (Boolean) properties.get(PROP_J);

		if (j != null)
			options.j = j;

		return options;
	}

	private ServerAddress createServerAddress(String uriProperty) throws URISyntaxException, UnknownHostException
	{
		URI uri = new URI(uriProperty);
		int port = uri.getPort();
		ServerAddress serverAddress = port == -1 ? new ServerAddress(uri.getHost()) : new ServerAddress(uri.getHost(), uri.getPort());
		return serverAddress;
	}
}
