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
    ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl(MongoProvider.PID, "MongoDB Client", "MongoDB Client Configuration");
    
    // clinetId
    
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl(MongoProvider.PROP_CLIENT_ID, "ID", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoProviderComponent.validateClientId(value);
			}
		};

		clientId.setDescription("The unique identifier for the client.");
    ocd.addRequiredAttribute(clientId);

    // uri
    
		AttributeDefinitionImpl uri = new AttributeDefinitionImpl(MongoProvider.PROP_URI, "URI", AttributeDefinition.STRING)
		{
			@Override
			public String validate(String value)
			{
				return MongoProviderComponent.validateURI(value);
			}
		};

		uri.setDescription("The URI of the MongoDB server of the form 'mongodb://[username:password@]host[:port][/database]'.  Separate URIs with a comma (CSV) for a replica set.");
    ocd.addRequiredAttribute(uri);

		// alwaysUseMBeans
		
    AttributeDefinitionImpl alwaysUseMBeans = new AttributeDefinitionImpl(MongoProvider.PROP_ALWAYS_USE_MBEANS, "Always use MBeans", AttributeDefinition.BOOLEAN);
    alwaysUseMBeans.setDefaultValue(new String[] {Boolean.FALSE.toString()});
    alwaysUseMBeans.setDescription("Sets whether JMX beans registered by the driver should always be MBeans, regardless of whether the VM is Java 6 or greater.");
    ocd.addOptionalAttribute(alwaysUseMBeans);

		// connectionsPerHost
		
    AttributeDefinitionImpl connectionsPerHost = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_CONNECTIONS_PER_HOST, "Connections Per Host", 1);
		connectionsPerHost.setDefaultValue(new String[] { "100" });
		connectionsPerHost
				.setDescription("The maximum number of connections allowed per host for this Mongo instance. Those connections will be kept in a pool when idle. Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection. Default is 100.");
    ocd.addOptionalAttribute(connectionsPerHost);

    // connectTimeout
    
    AttributeDefinitionImpl connectTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_CONNECT_TIMEOUT, "Connect Timeout", 0);
    connectTimeout.setDefaultValue(new String[] { "0" });
    connectTimeout
        .setDescription("The connection timeout in milliseconds. It is used solely when establishing a new connection Socket.connect(java.net.SocketAddress, int) Default is 0 and means no timeout.");
    ocd.addOptionalAttribute(connectTimeout);

    // cursorFinalizerEnabled
    
    AttributeDefinitionImpl cursorFinalizerEnabled = new AttributeDefinitionImpl(MongoProvider.PROP_CURSOR_FINALIZER_ENABLED, "Cursor Finalizer Enable", AttributeDefinition.BOOLEAN);
    cursorFinalizerEnabled.setDefaultValue(new String[] { Boolean.TRUE.toString() });
    cursorFinalizerEnabled.setDescription("Sets whether cursor finalizers are enabled.");
    ocd.addOptionalAttribute(cursorFinalizerEnabled);

    // description
    
    AttributeDefinitionImpl description = new AttributeDefinitionImpl(MongoProvider.PROP_DESCRIPTION, "Description", AttributeDefinition.STRING);
    description.setDescription("The description for Mongo instances created with these options. This is used in various places like logging.");
    ocd.addOptionalAttribute(description);

    // heartbeatConnectTimeout

    AttributeDefinitionImpl heartbeatConnectTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_HEARTBEAT_CONNECT_TIMEOUT, "Heartbeat Connect Timeout", 0);
    heartbeatConnectTimeout.setDefaultValue(new String[] { "20000" });
    heartbeatConnectTimeout.setDescription("The connect timeout for connections used for the cluster heartbeat. Default is 20,000.");
    ocd.addOptionalAttribute(heartbeatConnectTimeout);
    
    // heartbeatFrequency
    
    AttributeDefinitionImpl heartbeatFrequency = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_HEARTBEAT_FREQUENCY, "Heartbeat Frequency", 0);
    heartbeatFrequency.setDefaultValue(new String[] { "10000" });
    heartbeatFrequency.setDescription("The heartbeat frequency. Default is 10,000.");
    ocd.addOptionalAttribute(heartbeatFrequency);

    // heartbeatSocketTimeout
    
    AttributeDefinitionImpl heartbeatSocketTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_HEARTBEAT_SOCKET_TIMEOUT, "Heartbeat Socket Timeout", 0);
    heartbeatSocketTimeout.setDefaultValue(new String[] { "20000" });
    heartbeatSocketTimeout.setDescription("The heartbeat socket timeout. Default is 20,000.");
    ocd.addOptionalAttribute(heartbeatSocketTimeout);

    // localThreshold
    
    AttributeDefinitionImpl localThreshold = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_LOCAL_THRESHOLD, "Local Threshold", 0);
    localThreshold.setDefaultValue(new String[] { "15" });
    localThreshold.setDescription("When choosing among multiple MongoDB servers to send a request, the MongoClient will only send that request to a server whose ping time is less than or equal to the server with the fastest ping time plus the local threshold. Default is 15.");
    ocd.addOptionalAttribute(localThreshold);

    // maxConnectionIdleTime
    
    AttributeDefinitionImpl maxConnectionIdleTime = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MAX_CONNECTION_IDLE_TIME, "Max Connection Idle Time", 0);
    maxConnectionIdleTime.setDefaultValue(new String[] { "0" });
    maxConnectionIdleTime.setDescription("The maximum idle time of a pooled connection. A zero value indicates no limit to the idle time. A pooled connection that has exceeded its idle time will be closed and replaced when necessary by a new connection. Default is 0.");
    ocd.addOptionalAttribute(maxConnectionIdleTime);

    // maxConnectionLifeTime

    AttributeDefinitionImpl maxConnectionLifeTime = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MAX_CONNECTION_IDLE_TIME, "Max Connection Life Time", 0);
    maxConnectionLifeTime.setDefaultValue(new String[] { "0" });
    maxConnectionLifeTime.setDescription("The maximum life time of a pooled connection. A zero value indicates no limit to the life time. A pooled connection that has exceeded its life time will be closed and replaced when necessary by a new connection. Default is 0.");
    ocd.addOptionalAttribute(maxConnectionLifeTime);

		// maxWaitTime
		
		AttributeDefinitionImpl maxWaitTime = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MAX_WAIT_TIME, "Max Wait Time", 0);
		maxWaitTime.setDefaultValue(new String[] { "120000" });
		maxWaitTime.setDescription("The maximum wait time in ms that a thread may wait for a connection to become available. Default is 120,000.");
    ocd.addOptionalAttribute(maxWaitTime);

    // minConnectionsPerHost

    AttributeDefinitionImpl minConnectionsPerHost = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MIN_CONNECTIONS_PER_HOST, "Min Connections Per Host", 0);
    minConnectionsPerHost.setDefaultValue(new String[] { "0" });
    minConnectionsPerHost.setDescription("The minimum number of connections per host for this MongoClient instance. Those connections will be kept in a pool when idle, and the pool will ensure over time that it contains at least this minimum number. Default is 0.");
    ocd.addOptionalAttribute(minConnectionsPerHost);

    // minHeartbeatFrequency

    AttributeDefinitionImpl minHeartbeatFrequency = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_MIN_HEARTBEAT_FREQUENCY, "Min Heartbeat Frequency", 0);
    minHeartbeatFrequency.setDefaultValue(new String[] { "500" });
    minHeartbeatFrequency.setDescription("The minimum heartbeat frequency. In the event that the driver has to frequently re-check a server's availability, it will wait at least this long since the previous check to avoid wasted effort. Default is 500.");
    ocd.addOptionalAttribute(minHeartbeatFrequency);

    // readPreferenceType

    AttributeDefinitionImpl readPreferenceType = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_READ_PREFERENCE_TYPE, "Read Preference Type", 0);
    readPreferenceType.setDefaultValue(new String[] { "1" });
    readPreferenceType.setOptionLabels(new String[] {"Nearest", "Primary", "Primary Preferred", "Secondary", "Secondary Preferred"});
    readPreferenceType.setOptionValues(new String[] {"1", "2", "3", "4", "5"});
    readPreferenceType.setDescription("The read preference type.");
    ocd.addOptionalAttribute(readPreferenceType);

    // readPreferenceTags

    AttributeDefinitionImpl readPreferenceTags = new AttributeDefinitionImpl(MongoProvider.PROP_READ_PREFERENCE_TAGS, "Read Preference Tags", AttributeDefinition.STRING);
    readPreferenceTags.setDescription("Read preference tags in the form key=value");
    ocd.addOptionalAttribute(readPreferenceTags);

    // requiredReplicaSetName

    AttributeDefinitionImpl requiredReplicaSetName = new AttributeDefinitionImpl(MongoProvider.REQUIRED_REPLICA_SET_NAME, "Required Replica Set Name", AttributeDefinition.STRING);
    requiredReplicaSetName.setDescription("The required replica set name");
    ocd.addOptionalAttribute(requiredReplicaSetName);

    // serverSelectionTimeout

    AttributeDefinitionImpl serverSelectionTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_SERVER_SELECTION_TIMEOUT, "Server Selection Timeout", 0);
    serverSelectionTimeout.setDefaultValue(new String[] { "30000" });
    serverSelectionTimeout.setDescription("The server selection timeout in milliseconds, which defines how long the driver will wait for server selection to succeed before throwing an exception.  Default is 30,000.");
    ocd.addOptionalAttribute(serverSelectionTimeout);

    // socketKeepAlive
    
    AttributeDefinitionImpl socketKeepAlive = new AttributeDefinitionImpl(MongoProvider.PROP_SOCKET_KEEP_ALIVE, "Socket Keep Alive", AttributeDefinition.BOOLEAN);
    socketKeepAlive.setDefaultValue(new String[] { "false" });
    socketKeepAlive.setDescription("This flag controls the socket keep alive feature that keeps a connection alive through firewalls Socket.setKeepAlive(boolean) Default is false.");
    ocd.addOptionalAttribute(socketKeepAlive);

		// socketTimeout
		
		AttributeDefinitionImpl socketTimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_SOCKET_TIMEOUT, "Socket Timeout", 0);
		socketTimeout.setDefaultValue(new String[] { "0" });
		socketTimeout.setDescription("The socket timeout in milliseconds It is used for I/O socket read and write operations Socket.setSoTimeout(int) Default is 0 and means no timeout.");
    ocd.addOptionalAttribute(socketTimeout);

    // sslEnabled() default false;

    AttributeDefinitionImpl sslEnabled = new AttributeDefinitionImpl(MongoProvider.PROP_SSL_ENABLED, "SSL Enabled", AttributeDefinition.BOOLEAN);
    sslEnabled.setDefaultValue(new String[] { "false" });
    sslEnabled.setDescription("Whether to use SSL. Default is false.");
    ocd.addOptionalAttribute(sslEnabled);

    // sslInvalidHostNameAllowed() default false;

    AttributeDefinitionImpl sslInvalidHostNameAllowed = new AttributeDefinitionImpl(MongoProvider.PROP_SSL_INVALID_HOST_NAME_ALLOWED, "SSL Invalid Hostname Allowed", AttributeDefinition.BOOLEAN);
    sslInvalidHostNameAllowed.setDefaultValue(new String[] { "false" });
    sslInvalidHostNameAllowed.setDescription("Whether invalid host names should be allowed if SSL is enabled. Defaults to false. Take care before setting this to true, as it makes the application susceptible to man-in-the-middle attacks. Note that host name verification currently requires Java 7, so if your application is using SSL and must run on Java 6, this property must be set to true. Default is false.");
    ocd.addOptionalAttribute(sslInvalidHostNameAllowed);

    // threadsAllowedToBlockForConnectionMultiplier
    
    AttributeDefinitionImpl threadsAllowedToBlockForConnectionMultiplier = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER,
        "Blocked Threads Multiplier", 1);
    threadsAllowedToBlockForConnectionMultiplier.setDefaultValue(new String[] { "5" });
    threadsAllowedToBlockForConnectionMultiplier
        .setDescription("This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool. All further threads will get an exception right away. For example if connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50 threads can wait for a connection. Default is 5.");
    ocd.addOptionalAttribute(threadsAllowedToBlockForConnectionMultiplier);

		// w
		
		AttributeDefinitionImpl w = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_W, "Write Concern - w", 0);
		w.setDefaultValue(new String[] { "0" });
		w.setDescription("The 'w' value of the global WriteConcern. Default is 0.");
		ocd.addOptionalAttribute(w);

		// wtimeout
		
		AttributeDefinitionImpl wtimeout = new IntegerAttributeDefinitionImpl(MongoProvider.PROP_WTIMEOUT, "Write Concern - wtimeout", 0);
		wtimeout.setDefaultValue(new String[] { "0" });
		wtimeout.setDescription("The 'wtimeout' value of the global WriteConcern. Default is 0.");
		ocd.addOptionalAttribute(wtimeout);

		// fsync
		
		AttributeDefinitionImpl fsync = new AttributeDefinitionImpl(MongoProvider.PROP_FSYNC, "Write Concern - fsync", AttributeDefinition.BOOLEAN);
		fsync.setDefaultValue(new String[] { "false" });
		fsync.setDescription("The 'fsync' value of the global WriteConcern. Default is false.");
		ocd.addOptionalAttribute(fsync);

		// j
		
		AttributeDefinitionImpl j = new AttributeDefinitionImpl(MongoProvider.PROP_J, "Write Concern - j", AttributeDefinition.BOOLEAN);
		j.setDefaultValue(new String[] { "false" });
		j.setDescription("The 'j' value of the global WriteConcern. Default is false.");
		ocd.addOptionalAttribute(j);

		return ocd;
	}
}
