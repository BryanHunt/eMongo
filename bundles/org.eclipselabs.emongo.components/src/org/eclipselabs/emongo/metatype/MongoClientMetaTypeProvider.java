/**
 * 
 */

package org.eclipselabs.emongo.metatype;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author bhunt
 * 
 */
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
		AttributeDefinitionImpl clientId = new AttributeDefinitionImpl("client_id", "ID", AttributeDefinition.STRING);
		clientId.setDescription("The unique identifier for the client.");

		AttributeDefinitionImpl uri = new AttributeDefinitionImpl("uri", "URI", AttributeDefinition.STRING);
		uri.setDescription("The URI of the MongoDB server of the form 'mongodb://host:[port]'.  Separate URIs with a comma (CSV) for a replica set.");

		AttributeDefinitionImpl description = new AttributeDefinitionImpl("description", "Description", AttributeDefinition.STRING);
		description.setDescription("The description for Mongo instances created with these options. This is used in various places like logging.");

		AttributeDefinitionImpl connectionsPerHost = new IntegerAttributeDefinitionImpl("connectionsPerHost", "Connections Per Host", 1);
		connectionsPerHost.setDefaultValue(new String[] { "100" });
		connectionsPerHost
				.setDescription("The maximum number of connections allowed per host for this Mongo instance. Those connections will be kept in a pool when idle. Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection. Default is 100.");

		AttributeDefinitionImpl threadsAllowedToBlockForConnectionMultiplier = new IntegerAttributeDefinitionImpl("threadsAllowedToBlockForConnectionMultiplier", "Blocked Threads Multiplier", 1);
		connectionsPerHost.setDefaultValue(new String[] { "5" });
		threadsAllowedToBlockForConnectionMultiplier
				.setDescription("This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool. All further threads will get an exception right away. For example if connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50 threads can wait for a connection. Default is 5.");

		AttributeDefinitionImpl maxWaitTime = new IntegerAttributeDefinitionImpl("maxWaitTime", "Max Wait Time", 0);
		maxWaitTime.setDefaultValue(new String[] { "120000" });
		maxWaitTime.setDescription("The maximum wait time in ms that a thread may wait for a connection to become available. Default is 120,000.");

		AttributeDefinitionImpl connectTimeout = new IntegerAttributeDefinitionImpl("connectTimeout", "Connect Timeout", 0);
		connectTimeout.setDefaultValue(new String[] { "0" });
		connectTimeout
				.setDescription("The connection timeout in milliseconds. It is used solely when establishing a new connection Socket.connect(java.net.SocketAddress, int) Default is 0 and means no timeout.");

		AttributeDefinitionImpl socketTimeout = new IntegerAttributeDefinitionImpl("socketTimeout", "Connect Timeout", 0);
		socketTimeout.setDefaultValue(new String[] { "0" });
		socketTimeout.setDescription("The socket timeout in milliseconds It is used for I/O socket read and write operations Socket.setSoTimeout(int) Default is 0 and means no timeout.");

		AttributeDefinitionImpl socketKeepAlive = new AttributeDefinitionImpl("socketKeepAlive", "Socket Keep Alive", AttributeDefinition.BOOLEAN);
		socketKeepAlive.setDefaultValue(new String[] { "false" });
		socketKeepAlive.setDescription("This flag controls the socket keep alive feature that keeps a connection alive through firewalls Socket.setKeepAlive(boolean) Default is false.");

		AttributeDefinitionImpl autoConnectRetry = new AttributeDefinitionImpl("autoConnectRetry", "Auto Connect Retry", AttributeDefinition.BOOLEAN);
		autoConnectRetry.setDefaultValue(new String[] { "false" });
		autoConnectRetry
				.setDescription("If true, the driver will keep trying to connect to the same server in case that the socket cannot be established. There is maximum amount of time to keep retrying, which is 15s by default. This can be useful to avoid some exceptions being thrown when a server is down temporarily by blocking the operations. It also can be useful to smooth the transition to a new master (so that a new master is elected within the retry time). Note that when using this flag: - for a replica set, the driver will trying to connect to the old master for that time, instead of failing over to the new one right away - this does not prevent exception from being thrown in read/write operations on the socket, which must be handled by application Even if this flag is false, the driver already has mechanisms to automatically recreate broken connections and retry the read operations. Default is false.");

		AttributeDefinitionImpl maxAutoConnectRetryTime = new LongAttributeDefinitionImpl("maxAutoConnectRetryTime", "Max Auto Connect Retry Time", 0);
		maxAutoConnectRetryTime.setDefaultValue(new String[] { "0" });
		maxAutoConnectRetryTime
				.setDescription("The maximum amount of time in MS to spend retrying to open connection to the same server. Default is 0, which means to use the default 15s if autoConnectRetry is on.");

		AttributeDefinitionImpl continueOnInsertError = new AttributeDefinitionImpl("continueOnInsertError", "Write Concern - continueOnInsertError", AttributeDefinition.BOOLEAN);
		continueOnInsertError.setDefaultValue(new String[] { "false" });
		continueOnInsertError.setDescription("If batch inserts should continue after the first error. Default is false.");

		AttributeDefinitionImpl w = new IntegerAttributeDefinitionImpl("w", "Write Concern - w", 0);
		w.setDefaultValue(new String[] { "0" });
		w.setDescription("The 'w' value of the global WriteConcern. Default is 0.");

		AttributeDefinitionImpl wtimeout = new IntegerAttributeDefinitionImpl("wtimeout", "Write Concern - wtimeout", 0);
		wtimeout.setDefaultValue(new String[] { "0" });
		wtimeout.setDescription("The 'wtimeout' value of the global WriteConcern. Default is 0.");

		AttributeDefinitionImpl fsync = new AttributeDefinitionImpl("fsync", "Write Concern - fsync", AttributeDefinition.BOOLEAN);
		fsync.setDefaultValue(new String[] { "false" });
		fsync.setDescription("The 'fsync' value of the global WriteConcern. Default is false.");

		AttributeDefinitionImpl j = new AttributeDefinitionImpl("j", "Write Concern - j", AttributeDefinition.BOOLEAN);
		j.setDefaultValue(new String[] { "false" });
		j.setDescription("The 'j' value of the global WriteConcern. Default is false.");

		ObjectClassDefinitionImpl ocd = new ObjectClassDefinitionImpl("org.eclipselabs.emongo.clientProvider", "MongoDB Client", "MongoDB Client Configuration");
		ocd.addAttribute(clientId);
		ocd.addAttribute(uri);
		ocd.addAttribute(description);
		ocd.addAttribute(connectionsPerHost);
		ocd.addAttribute(threadsAllowedToBlockForConnectionMultiplier);
		ocd.addAttribute(maxWaitTime);
		ocd.addAttribute(connectTimeout);
		ocd.addAttribute(socketTimeout);
		ocd.addAttribute(socketKeepAlive);
		ocd.addAttribute(autoConnectRetry);
		ocd.addAttribute(maxAutoConnectRetryTime);
		ocd.addAttribute(continueOnInsertError);
		ocd.addAttribute(w);
		ocd.addAttribute(wtimeout);
		ocd.addAttribute(fsync);
		ocd.addAttribute(j);

		return ocd;
	}
}
