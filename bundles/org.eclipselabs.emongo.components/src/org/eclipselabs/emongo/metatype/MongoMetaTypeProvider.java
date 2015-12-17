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

import org.eclipselabs.emongo.MongoProvider;
import org.eclipselabs.emongo.components.MongoProviderComponent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
@Component(service = MetaTypeProvider.class, property = {MetaTypeConfiguration.PROP_CLIENT_PID})
public class MongoMetaTypeProvider implements MetaTypeProvider
{
	@Override
	public String[] getLocales()
	{
		return null;
	}

	@Override
	public ObjectClassDefinition getObjectClassDefinition(String id, String locale)
	{
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl(MongoProvider.PROP_CLIENT_ID, "ID", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoProviderComponent.validateClientId(value);
			}
		};

		clientId.setDescription("The unique identifier for the client.");

		AttributeDefinitionImpl uri = new AttributeDefinitionImpl(MongoProvider.PROP_URI, "URI", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoProviderComponent.validateURI(value);
			}
		};

		uri.setDescription("The URI of the MongoDB server of the form 'mongodb://[username:password@]host[:port][/database]'.  Separate URIs with a comma (CSV) for a replica set.");

		// alwaysUseMBeans
		
    AttributeDefinitionImpl alwaysUseMBeans = new AttributeDefinitionImpl(MongoProvider.PROP_ALWAYS_USE_MBEANS, "Always use MBeans", AttributeDefinition.BOOLEAN);
    alwaysUseMBeans.setDefaultValue(new String[] {Boolean.FALSE.toString()});
    alwaysUseMBeans.setDescription("Sets whether JMX beans registered by the driver should always be MBeans, regardless of whether the VM is Java 6 or greater.");

		// connectionsPerHost
		
    AttributeDefinitionImpl connectionsPerHost = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_CONNECTIONS_PER_HOST, "Connections Per Host", 1);
		connectionsPerHost.setDefaultValue(new String[] { "100" });
		connectionsPerHost
				.setDescription("The maximum number of connections allowed per host for this Mongo instance. Those connections will be kept in a pool when idle. Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection. Default is 100.");

    // connectTimeout
    
    AttributeDefinitionImpl connectTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_CONNECT_TIMEOUT, "Connect Timeout", 0);
    connectTimeout.setDefaultValue(new String[] { "0" });
    connectTimeout
        .setDescription("The connection timeout in milliseconds. It is used solely when establishing a new connection Socket.connect(java.net.SocketAddress, int) Default is 0 and means no timeout.");

    // cursorFinalizerEnabled
    
    AttributeDefinitionImpl cursorFinalizerEnabled = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_CURSOR_FINALIZER_ENABLED, "Cursor Finalizer Enable", AttributeDefinition.BOOLEAN);
    cursorFinalizerEnabled.setDefaultValue(new String[] { Boolean.TRUE.toString() });
    cursorFinalizerEnabled.setDescription("Sets whether cursor finalizers are enabled.");

    // description
    
    AttributeDefinitionImpl description = new AttributeDefinitionImpl(MongoProvider.PROP_DESCRIPTION, "Description", AttributeDefinition.STRING);
    description.setDescription("The description for Mongo instances created with these options. This is used in various places like logging.");

    // threadsAllowedToBlockForConnectionMultiplier
		
		AttributeDefinitionImpl threadsAllowedToBlockForConnectionMultiplier = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER,
				"Blocked Threads Multiplier", 1);
		threadsAllowedToBlockForConnectionMultiplier.setDefaultValue(new String[] { "5" });
		threadsAllowedToBlockForConnectionMultiplier
				.setDescription("This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool. All further threads will get an exception right away. For example if connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50 threads can wait for a connection. Default is 5.");

		// maxWaitTime
		
		AttributeDefinitionImpl maxWaitTime = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MAX_WAIT_TIME, "Max Wait Time", 0);
		maxWaitTime.setDefaultValue(new String[] { "120000" });
		maxWaitTime.setDescription("The maximum wait time in ms that a thread may wait for a connection to become available. Default is 120,000.");

		// socketTimeout
		
		AttributeDefinitionImpl socketTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_SOCKET_TIMEOUT, "Socket Timeout", 0);
		socketTimeout.setDefaultValue(new String[] { "0" });
		socketTimeout.setDescription("The socket timeout in milliseconds It is used for I/O socket read and write operations Socket.setSoTimeout(int) Default is 0 and means no timeout.");

		// socketKeepAlive
		
		AttributeDefinitionImpl socketKeepAlive = new AttributeDefinitionImpl(MongoProvider.PROP_SOCKET_KEEP_ALIVE, "Socket Keep Alive", AttributeDefinition.BOOLEAN);
		socketKeepAlive.setDefaultValue(new String[] { "false" });
		socketKeepAlive.setDescription("This flag controls the socket keep alive feature that keeps a connection alive through firewalls Socket.setKeepAlive(boolean) Default is false.");

		// w
		
		AttributeDefinitionImpl w = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_W, "Write Concern - w", 0);
		w.setDefaultValue(new String[] { "0" });
		w.setDescription("The 'w' value of the global WriteConcern. Default is 0.");

		// wtimeout
		
		AttributeDefinitionImpl wtimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_WTIMEOUT, "Write Concern - wtimeout", 0);
		wtimeout.setDefaultValue(new String[] { "0" });
		wtimeout.setDescription("The 'wtimeout' value of the global WriteConcern. Default is 0.");

		// fsync
		
		AttributeDefinitionImpl fsync = new AttributeDefinitionImpl(MongoProvider.PROP_FSYNC, "Write Concern - fsync", AttributeDefinition.BOOLEAN);
		fsync.setDefaultValue(new String[] { "false" });
		fsync.setDescription("The 'fsync' value of the global WriteConcern. Default is false.");

		// j
		
		AttributeDefinitionImpl j = new AttributeDefinitionImpl(MongoProvider.PROP_J, "Write Concern - j", AttributeDefinition.BOOLEAN);
		j.setDefaultValue(new String[] { "false" });
		j.setDescription("The 'j' value of the global WriteConcern. Default is false.");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoProvider.PID, "MongoDB Client", "MongoDB Client Configuration");
		
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
