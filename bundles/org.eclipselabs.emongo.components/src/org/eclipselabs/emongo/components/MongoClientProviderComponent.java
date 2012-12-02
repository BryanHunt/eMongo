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

import org.eclipselabs.emongo.MongoClientProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * @author bhunt
 * 
 */
public class MongoClientProviderComponent extends AbstractComponent implements MongoClientProvider
{
	private volatile String[] uris;
	private volatile MongoClient mongoClient;

	@Override
	public MongoClient getMongoClient()
	{
		return mongoClient;
	}

	@Override
	public String[] getURIs()
	{
		return uris;
	}

	public void activate(Map<String, Object> properties)
	{
		MongoClientOptions options = createMongoClientOptions(properties);

		// The uriProperty is a single string containing one or more server URIs.
		// When more than one URI is specified, it denotes a replica set and the
		// URIs must be separated by a comma (CSV).

		String uriProperty = (String) properties.get(PROP_URI);

		if (uriProperty == null || uriProperty.isEmpty())
			handleIllegalConfiguration("The MongoDB URI was not found in the configuration properties");

		// The regex \s matches whitepsace. The extra \ is needed because of how it's treated in java
		// strings. The split is done on any number of whitespace chars followed by a comma followed by
		// any number of whitespace chars. What is left is the URI(s).

		uris = uriProperty.split("\\s*,\\s*");
		String currentURI = null;

		try
		{
			if (uris.length == 1)
			{
				currentURI = uris[0].trim();
				checkURI(currentURI);
				ServerAddress serverAddress = createServerAddress(currentURI);
				mongoClient = createMongoClient(options, serverAddress);
			}
			else
			{
				ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>(uris.length);

				for (String uri : uris)
				{
					currentURI = uri.trim();
					checkURI(currentURI);
					serverAddresses.add(createServerAddress(currentURI));
				}

				mongoClient = createMongoClient(options, serverAddresses);
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
		if (mongoClient != null)
			mongoClient.close();
	}

	protected MongoClient createMongoClient(MongoClientOptions options, ArrayList<ServerAddress> serverAddresses)
	{
		return new MongoClient(serverAddresses, options);
	}

	protected MongoClient createMongoClient(MongoClientOptions options, ServerAddress serverAddress)
	{
		return new MongoClient(serverAddress, options);
	}

	private void checkURI(String currentURI)
	{
		// The URI will be of the form: mongodb://host[:port]
		// When the string is split on / the URI must have 3 parts

		if (!currentURI.startsWith("mongodb://") || currentURI.endsWith("/") || currentURI.split("/").length != 3)
			handleIllegalConfiguration("The uri: '" + currentURI + "' does not have the form 'mongodb://host[:port]'");
	}

	private MongoClientOptions createMongoClientOptions(Map<String, Object> properties)
	{
		MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();

		String description = (String) properties.get(PROP_DESCRIPTION);

		if (description != null)
			optionsBuilder.description(description);

		Integer connectionsPerHost = (Integer) properties.get(PROP_CONNECTIONS_PER_HOST);

		if (connectionsPerHost != null)
			optionsBuilder.connectionsPerHost(connectionsPerHost);

		Integer threadsAllowedToBlockForConnectionMultiplier = (Integer) properties.get(PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER);

		if (threadsAllowedToBlockForConnectionMultiplier != null)
			optionsBuilder.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);

		Integer maxWaitTime = (Integer) properties.get(PROP_MAX_WAIT_TIME);

		if (maxWaitTime != null)
			optionsBuilder.maxWaitTime(maxWaitTime);

		Integer connectTimeout = (Integer) properties.get(PROP_CONNECT_TIMEOUT);

		if (connectTimeout != null)
			optionsBuilder.connectTimeout(connectTimeout);

		Integer socketTimeout = (Integer) properties.get(PROP_SOCKET_TIMEOUT);

		if (socketTimeout != null)
			optionsBuilder.socketTimeout(socketTimeout);

		Boolean socketKeepAlive = (Boolean) properties.get(PROP_SOCKET_KEEP_ALIVE);

		if (socketKeepAlive != null)
			optionsBuilder.socketKeepAlive(socketKeepAlive);

		Boolean autoConnectRetry = (Boolean) properties.get(PROP_AUTO_CONNECT_RETRY);

		if (autoConnectRetry != null)
			optionsBuilder.autoConnectRetry(autoConnectRetry);

		Long maxAutoConnectRetryTime = (Long) properties.get(PROP_MAX_AUTO_CONNECT_RETRY_TIME);

		if (maxAutoConnectRetryTime != null)
			optionsBuilder.maxAutoConnectRetryTime(maxAutoConnectRetryTime);

		Boolean continueOnInsertError = (Boolean) properties.get(PROP_CONTINUE_ON_INSERT_ERROR);

		if (continueOnInsertError == null)
			continueOnInsertError = Boolean.FALSE;

		Integer w = (Integer) properties.get(PROP_W);

		if (w == null)
			w = Integer.valueOf(1);

		Integer wtimeout = (Integer) properties.get(PROP_WTIMEOUT);

		if (wtimeout == null)
			wtimeout = Integer.valueOf(0);

		Boolean fsync = (Boolean) properties.get(PROP_FSYNC);

		if (fsync == null)
			fsync = Boolean.FALSE;

		Boolean j = (Boolean) properties.get(PROP_J);

		if (j == null)
			j = Boolean.FALSE;

		WriteConcern writeConcern = new WriteConcern(w, wtimeout, fsync, j, continueOnInsertError);
		optionsBuilder.writeConcern(writeConcern);

		return optionsBuilder.build();
	}

	private ServerAddress createServerAddress(String uriProperty) throws URISyntaxException, UnknownHostException
	{
		URI uri = new URI(uriProperty);
		int port = uri.getPort();
		ServerAddress serverAddress = port == -1 ? new ServerAddress(uri.getHost()) : new ServerAddress(uri.getHost(), uri.getPort());
		return serverAddress;
	}
}
