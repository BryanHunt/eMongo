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

package org.eclipselabs.emongo.metatype;

import org.eclipselabs.emongo.MongoClientProvider;
import org.eclipselabs.emongo.components.MongoClientProviderComponent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {"metatype.factory.pid=org.eclipselabs.emongo.clientProvider"})
public class MongoClientMetaTypeProvider implements MetaTypeProvider
{
	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
	{
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl(MongoClientProvider.PROP_CLIENT_ID, "ID", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoClientProviderComponent.validateClientId(value);
			}
		};

		clientId.setDescription("The unique identifier for the client.");

		AttributeDefinitionImpl uri = new AttributeDefinitionImpl(MongoClientProvider.PROP_URI, "URI", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoClientProviderComponent.validateURI(value);
			}
		};

		uri.setDescription("The URI of the MongoDB server of the form 'mongodb://host[:port]'.  Separate URIs with a comma (CSV) for a replica set.");

		AttributeDefinitionImpl description = new AttributeDefinitionImpl(MongoClientProvider.PROP_DESCRIPTION, "Description", AttributeDefinition.STRING);
		description.setDescription("The description for Mongo instances created with these options. This is used in various places like logging.");

		AttributeDefinitionImpl connectionsPerHost = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_CONNECTIONS_PER_HOST, "Connections Per Host", 1);
		connectionsPerHost.setDefaultValue(new String[] { "100" });
		connectionsPerHost
				.setDescription("The maximum number of connections allowed per host for this Mongo instance. Those connections will be kept in a pool when idle. Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection. Default is 100.");

		AttributeDefinitionImpl threadsAllowedToBlockForConnectionMultiplier = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER,
				"Blocked Threads Multiplier", 1);
		connectionsPerHost.setDefaultValue(new String[] { "5" });
		threadsAllowedToBlockForConnectionMultiplier
				.setDescription("This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool. All further threads will get an exception right away. For example if connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50 threads can wait for a connection. Default is 5.");

		AttributeDefinitionImpl maxWaitTime = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_MAX_WAIT_TIME, "Max Wait Time", 0);
		maxWaitTime.setDefaultValue(new String[] { "120000" });
		maxWaitTime.setDescription("The maximum wait time in ms that a thread may wait for a connection to become available. Default is 120,000.");

		AttributeDefinitionImpl connectTimeout = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_CONNECT_TIMEOUT, "Connect Timeout", 0);
		connectTimeout.setDefaultValue(new String[] { "0" });
		connectTimeout
				.setDescription("The connection timeout in milliseconds. It is used solely when establishing a new connection Socket.connect(java.net.SocketAddress, int) Default is 0 and means no timeout.");

		AttributeDefinitionImpl socketTimeout = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_SOCKET_TIMEOUT, "Socket Timeout", 0);
		socketTimeout.setDefaultValue(new String[] { "0" });
		socketTimeout.setDescription("The socket timeout in milliseconds It is used for I/O socket read and write operations Socket.setSoTimeout(int) Default is 0 and means no timeout.");

		AttributeDefinitionImpl socketKeepAlive = new AttributeDefinitionImpl(MongoClientProvider.PROP_SOCKET_KEEP_ALIVE, "Socket Keep Alive", AttributeDefinition.BOOLEAN);
		socketKeepAlive.setDefaultValue(new String[] { "false" });
		socketKeepAlive.setDescription("This flag controls the socket keep alive feature that keeps a connection alive through firewalls Socket.setKeepAlive(boolean) Default is false.");

		AttributeDefinitionImpl w = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_W, "Write Concern - w", 0);
		w.setDefaultValue(new String[] { "0" });
		w.setDescription("The 'w' value of the global WriteConcern. Default is 0.");

		AttributeDefinitionImpl wtimeout = new IntegerAttributeDefinitionImpl(MongoClientProvider.PROP_WTIMEOUT, "Write Concern - wtimeout", 0);
		wtimeout.setDefaultValue(new String[] { "0" });
		wtimeout.setDescription("The 'wtimeout' value of the global WriteConcern. Default is 0.");

		AttributeDefinitionImpl fsync = new AttributeDefinitionImpl(MongoClientProvider.PROP_FSYNC, "Write Concern - fsync", AttributeDefinition.BOOLEAN);
		fsync.setDefaultValue(new String[] { "false" });
		fsync.setDescription("The 'fsync' value of the global WriteConcern. Default is false.");

		AttributeDefinitionImpl j = new AttributeDefinitionImpl(MongoClientProvider.PROP_J, "Write Concern - j", AttributeDefinition.BOOLEAN);
		j.setDefaultValue(new String[] { "false" });
		j.setDescription("The 'j' value of the global WriteConcern. Default is false.");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(ConfigurationProperties.CLIENT_PID, "MongoDB Client", "MongoDB Client Configuration");
		
		ocd.addRequiredAttribute(clientId);
		ocd.addRequiredAttribute(uri);
		ocd.addOptionalAttribute(description);
		ocd.addOptionalAttribute(connectionsPerHost);
		ocd.addOptionalAttribute(threadsAllowedToBlockForConnectionMultiplier);
		ocd.addOptionalAttribute(maxWaitTime);
		ocd.addOptionalAttribute(connectTimeout);
		ocd.addOptionalAttribute(socketTimeout);
		ocd.addOptionalAttribute(socketKeepAlive);
		ocd.addOptionalAttribute(w);
		ocd.addOptionalAttribute(wtimeout);
		ocd.addOptionalAttribute(fsync);
		ocd.addOptionalAttribute(j);

		return ocd;
	}
}
