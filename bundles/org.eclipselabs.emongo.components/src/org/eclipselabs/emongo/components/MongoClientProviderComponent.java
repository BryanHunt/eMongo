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
import java.util.Collection;
import java.util.List;

import org.eclipselabs.emongo.MongoClientProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;


/**
 * @author bhunt
 * 
 */
@Component(service = MongoClientProvider.class, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = {"org.eclipselabs.emongo.clientProvider"})
public class MongoClientProviderComponent extends AbstractComponent implements MongoClientProvider
{
  public @interface ClientConfig 
  {
    String clientId();
    String uri();
    String[] credentials();
    boolean alwaysUseMBeans() default false;
    int connectionsPerHost() default 100;
    int connectTimeout() default 10000;
    boolean cursorFinalizerEnabled() default true;
    String description() default "";
    int heartbeatConnectTimeout() default 20000;
    int heartbeatFrequency() default 10000;
    int heartbeatSocketTimeout() default 20000;
    int localThreshold() default 15;
    int maxConnectionIdleTime() default 0;
    int maxConnectionLifeTime() default 0;
    int maxWaitTime() default 120000;
    int minConnectionsPerHost() default 0;
    int minHeartbeatFrequency() default 500;
//    readPreference(ReadPreference readPreference)
    String requiredReplicaSetName() default "";
    int serverSelectionTimeout() default 30000;
    boolean socketKeepAlive() default false;
    int socketTimeout() default 0;
    boolean sslEnabled() default false;
    boolean sslInvalidHostNameAllowed() default false;
    int threadsAllowedToBlockForConnectionMultiplier() default 5;
    int writeConcernW() default 1;
    int writeConcernWtimeout() default 0;
    boolean writeConcernFsync() default false;
    boolean writeConcernJ() default false;
  }

  private volatile String clientId;
	private volatile Collection<String> uris;
	private volatile MongoClient mongoClient;

	public static String validateClientId(String value)
	{
		if (value == null || value.isEmpty())
			return "The MongoDB client id was not found in the configuration properties";

		return null;
	}

	public static String validateURI(String value)
	{
		return validateURI(value, null);
	}

	@Override
	public String getClientId()
	{
		return clientId;
	}

	@Override
	public MongoClient getMongoClient()
	{
		return mongoClient;
	}

	@Override
	public String[] getURIs()
	{
		return uris.toArray(new String[0]);
	}

	@Activate
	public void activate(ClientConfig config)
	{
		handleIllegalConfiguration(validateClientId(config.clientId()));

		// The uriProperty is a single string containing one or more server URIs.
		// When more than one URI is specified, it denotes a replica set and the
		// URIs must be separated by a comma (CSV).

		uris = new ArrayList<String>();
		handleIllegalConfiguration(validateURI(config.uri(), uris));

		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		
		if(config.credentials() != null)
		{
		  for(String entry : config.credentials())
		  {
		    String credential[] = entry.split(":");
		    handleIllegalConfiguration(validateCredentials(credential));
		    credentials.add(MongoCredential.createCredential(credential[1], credential[0], credential[2].toCharArray()));
		  }
		}
		
		MongoClientOptions options = createMongoClientOptions(config);
		String currentURI = null;

		try
		{
			if (uris.size() == 1)
			{
				currentURI = uris.iterator().next();
				ServerAddress serverAddress = createServerAddress(currentURI);
				mongoClient = createMongoClient(serverAddress, credentials, options);
			}
			else
			{
				ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>(uris.size());

				for (String uri : uris)
				{
					currentURI = uri;
					serverAddresses.add(createServerAddress(currentURI));
				}

				mongoClient = createMongoClient(serverAddresses, credentials, options);
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

	@Deactivate
	public void deactivate()
	{
		if (mongoClient != null)
			mongoClient.close();
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
  public void bindLogService(LogService logService)
  {
    super.bindLogService(logService);;
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }

	protected MongoClient createMongoClient(ArrayList<ServerAddress> serverAddresses, List<MongoCredential> credentials, MongoClientOptions options)
	{
		return new MongoClient(serverAddresses, credentials, options);
	}

	protected MongoClient createMongoClient(ServerAddress serverAddress, List<MongoCredential> credentials, MongoClientOptions options)
	{
		return new MongoClient(serverAddress, credentials, options);
	}

	private static String validateURI(String value, Collection<String> uris)
	{
		if (value == null || value.isEmpty())
			return "The MongoDB URI was not found in the configuration properties";

		// The regex \s matches whitepsace. The extra \ is needed because of how it's treated in java
		// strings. The split is done on any number of whitespace chars followed by a comma followed by
		// any number of whitespace chars. What is left is the URI(s).

		for (String targetURI : value.split("\\s*,\\s*"))
		{
			String uri = targetURI.trim();
			String[] segments = uri.split("/");
			
			if (!uri.startsWith("mongodb://") || uri.endsWith("/") || segments.length < 3 || segments.length > 4)
				return "The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/[database]'";

			if (uris != null)
				uris.add(uri);
		}

		return null;
	}

	private static String validateCredentials(String[] credentialData)
	{
	  if(credentialData.length != 3)
	    return "A credential must be in the format 'db:userId:password'";
	  
	  return null;
	}
	
	private MongoClientOptions createMongoClientOptions(ClientConfig config)
	{
		MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();

		optionsBuilder.alwaysUseMBeans(config.alwaysUseMBeans());
		optionsBuilder.connectionsPerHost(config.connectionsPerHost());
		optionsBuilder.connectTimeout(config.connectTimeout());
		optionsBuilder.cursorFinalizerEnabled(config.cursorFinalizerEnabled());
		optionsBuilder.connectionsPerHost(config.connectionsPerHost());
		optionsBuilder.description(config.description());
		optionsBuilder.heartbeatConnectTimeout(config.heartbeatConnectTimeout());
		optionsBuilder.heartbeatFrequency(config.heartbeatFrequency());
		optionsBuilder.heartbeatSocketTimeout(config.heartbeatSocketTimeout());
		optionsBuilder.localThreshold(config.localThreshold());
		optionsBuilder.maxConnectionIdleTime(config.maxConnectionIdleTime());
		optionsBuilder.maxConnectionLifeTime(config.maxConnectionLifeTime());
		optionsBuilder.maxWaitTime(config.maxWaitTime());
		optionsBuilder.minConnectionsPerHost(config.minConnectionsPerHost());
		optionsBuilder.minHeartbeatFrequency(config.minHeartbeatFrequency());
		
		if(!config.requiredReplicaSetName().isEmpty())
		  optionsBuilder.requiredReplicaSetName(config.requiredReplicaSetName());
		
		optionsBuilder.serverSelectionTimeout(config.serverSelectionTimeout());
		optionsBuilder.socketKeepAlive(config.socketKeepAlive());
		optionsBuilder.socketTimeout(config.socketTimeout());
		optionsBuilder.sslEnabled(config.sslEnabled());
		optionsBuilder.sslInvalidHostNameAllowed(config.sslInvalidHostNameAllowed());
		optionsBuilder.threadsAllowedToBlockForConnectionMultiplier(config.threadsAllowedToBlockForConnectionMultiplier());
		WriteConcern writeConcern = new WriteConcern(config.writeConcernW(), config.writeConcernWtimeout(), config.writeConcernFsync(), config.writeConcernJ());
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
