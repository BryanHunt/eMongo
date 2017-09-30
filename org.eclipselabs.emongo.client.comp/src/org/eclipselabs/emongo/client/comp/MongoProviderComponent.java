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

package org.eclipselabs.emongo.client.comp;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipselabs.emongo.client.MongoProvider;
import org.eclipselabs.emongo.comp.AbstractComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

/**
 * @author bhunt
 * 
 */
@Component(service = MongoProvider.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class MongoProviderComponent extends AbstractComponent implements MongoProvider
{
  private volatile String databaseName;
  private volatile Collection<String> uris;
  private volatile MongoClient mongoClient;

  public String[] getURIs()
  {
    return uris.toArray(new String[0]);
  }
  
  @Override
  public MongoClient getMongoClient()
  {
    return mongoClient;
  }

  @Override
  public MongoDatabase getMongoDatabase()
  {
    return getMongoClient().getDatabase(databaseName);
  }

  @Activate
  public void activate(Map<String, Object> config)
  {
    databaseName = (String) config.getOrDefault(MongoProvider.PROP_DEATABASE_NAME, "");

    // The uriProperty is a single string containing one or more server URIs.
    // When more than one URI is specified, it denotes a replica set and the
    // URIs must be separated by a comma (CSV).

    uris = new ArrayList<String>();
    handleIllegalConfiguration(validateURI((String) config.get(MongoProvider.PROP_URI), uris));

    MongoClientOptions options = createMongoClientOptions(config);
    mongoClient = createMongoClient(uris, options);
  }

  @Deactivate
  public void deactivate()
  {
    if (mongoClient != null)
      mongoClient.close();
  }

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
  public void bindLogService(LogService logService)
  {
    super.bindLogService(logService);
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }

  protected MongoClient createMongoClient(Collection<String> uris, MongoClientOptions options)
  {
    String currentURI = null;

    try
    {
      List<ServerAddress> serverAddresses = new ArrayList<>(uris.size());
      List<MongoCredential> mongoCredentials = new ArrayList<>();
      
      for (String uri : uris)
      {
        currentURI = uri;
        serverAddresses.add(createServerAddress(currentURI));
        String[] segments = currentURI.split("/");
        String host = segments[2];
        int authIndex = host.indexOf('@');
        
        if (segments.length == 4)
          databaseName = segments[3];

        if(authIndex > 0)
        {
          String[] credentials = host.substring(0, authIndex).split(":");
          mongoCredentials.add(MongoCredential.createCredential(credentials[0], databaseName, credentials[1].toCharArray()));
        }
      }
      
      if(serverAddresses.size() == 1)
        return new MongoClient(serverAddresses.get(0), mongoCredentials, options);
      else
        return new MongoClient(serverAddresses, mongoCredentials, options);
        
    }
    catch (UnknownHostException e)
    {
      handleConfigurationException("The URI: '" + currentURI + "' has a bad hostname", e);
    }
    catch (URISyntaxException e)
    {
      handleConfigurationException("The URI: '" + currentURI + "' is not a proper URI", e);
    }
    
    return null;
  }

  private String validateURI(String value, Collection<String> uris)
  {
    if (value == null || value.isEmpty())
      return "The MongoDB URI was not found in the configuration properties";

    // The regex \s matches whitepsace. The extra \ is needed because of how
    // it's treated in java
    // strings. The split is done on any number of whitespace chars followed by
    // a comma followed by
    // any number of whitespace chars. What is left is the URI(s).

    for (String targetURI : value.split("\\s*,\\s*"))
    {
      String uri = targetURI.trim();
      String[] segments = uri.split("/");

      if (!uri.startsWith("mongodb://") || uri.endsWith("/") || segments.length < 3 || segments.length > 5)
        return "The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/[database]'";

      if (uris != null)
        uris.add(uri);
    }

    return null;
  }

  private MongoClientOptions createMongoClientOptions(Map<String, Object> config)
  {
    MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();

    optionsBuilder.alwaysUseMBeans((boolean) config.getOrDefault(MongoProvider.PROP_ALWAYS_USE_MBEANS, false));
    optionsBuilder.connectionsPerHost((int) config.getOrDefault(MongoProvider.PROP_CONNECTIONS_PER_HOST, 100));
    optionsBuilder.connectTimeout((int) config.getOrDefault(MongoProvider.PROP_CONNECT_TIMEOUT, 10000));
    optionsBuilder.cursorFinalizerEnabled((boolean) config.getOrDefault(MongoProvider.PROP_CURSOR_FINALIZER_ENABLED, true));
    optionsBuilder.description((String) config.getOrDefault(MongoProvider.PROP_DESCRIPTION, ""));
    optionsBuilder.heartbeatConnectTimeout((int) config.getOrDefault(MongoProvider.PROP_HEARTBEAT_CONNECT_TIMEOUT, 20000));
    optionsBuilder.heartbeatFrequency((int) config.getOrDefault(MongoProvider.PROP_HEARTBEAT_FREQUENCY, 10000));
    optionsBuilder.heartbeatSocketTimeout((int) config.getOrDefault(MongoProvider.PROP_HEARTBEAT_SOCKET_TIMEOUT, 20000));
    optionsBuilder.localThreshold((int) config.getOrDefault(MongoProvider.PROP_LOCAL_THRESHOLD, 15));
    optionsBuilder.maxConnectionIdleTime((int) config.getOrDefault(MongoProvider.PROP_MAX_CONNECTION_IDLE_TIME, 0));
    optionsBuilder.maxConnectionLifeTime((int) config.getOrDefault(MongoProvider.PROP_MAX_CONNECTION_LIFE_TIME, 0));
    optionsBuilder.maxWaitTime((int) config.getOrDefault(MongoProvider.PROP_MAX_WAIT_TIME, 120000));
    optionsBuilder.minConnectionsPerHost((int) config.getOrDefault(MongoProvider.PROP_MIN_CONNECTIONS_PER_HOST, 0));
    optionsBuilder.minHeartbeatFrequency((int) config.getOrDefault(MongoProvider.PROP_MIN_HEARTBEAT_FREQUENCY, 500));
    optionsBuilder.serverSelectionTimeout((int) config.getOrDefault(MongoProvider.PROP_SERVER_SELECTION_TIMEOUT, 30000));
    optionsBuilder.socketTimeout((int) config.getOrDefault(MongoProvider.PROP_SOCKET_TIMEOUT, 0));
    optionsBuilder.sslEnabled((boolean) config.getOrDefault(MongoProvider.PROP_SSL_ENABLED, false));
    optionsBuilder.sslInvalidHostNameAllowed((boolean) config.getOrDefault(MongoProvider.PROP_SSL_INVALID_HOST_NAME_ALLOWED, false));
    optionsBuilder.threadsAllowedToBlockForConnectionMultiplier((int) config.getOrDefault(MongoProvider.PROP_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER, 5));
    WriteConcern writeConcern = new WriteConcern((int) config.getOrDefault(MongoProvider.PROP_W, 1), (int) config.getOrDefault(MongoProvider.PROP_WTIMEOUT, 0));
    optionsBuilder.writeConcern(writeConcern);
    
    if (!((String) config.getOrDefault(MongoProvider.REQUIRED_REPLICA_SET_NAME, "")).isEmpty())
      optionsBuilder.requiredReplicaSetName((String) config.getOrDefault(MongoProvider.REQUIRED_REPLICA_SET_NAME, ""));

    List<Tag> tags = new ArrayList<>();

    if (config.get(MongoProvider.PROP_READ_PREFERENCE_TAGS) != null)
    {
      for (String tag : (String[]) config.get(MongoProvider.PROP_READ_PREFERENCE_TAGS))
      {
        String[] elements = tag.split("=");
        tags.add(new Tag(elements[0], elements[1]));
      }
    }

    TagSet tagSet = new TagSet(tags);

    switch ((int) config.getOrDefault(MongoProvider.PROP_READ_PREFERENCE_TYPE, 0))
    {
    case 1:
      optionsBuilder.readPreference(ReadPreference.nearest(tagSet));
      break;
    case 2:
      optionsBuilder.readPreference(ReadPreference.primary());
      break;
    case 3:
      optionsBuilder.readPreference(ReadPreference.primaryPreferred(tagSet));
      break;
    case 4:
      optionsBuilder.readPreference(ReadPreference.secondary(tagSet));
      break;
    case 5:
      optionsBuilder.readPreference(ReadPreference.secondaryPreferred(tagSet));
      break;
    }

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
