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

import org.eclipselabs.emongo.MongoProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
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
@Component(service = MongoProvider.class, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = { MongoProvider.PID })
public class MongoProviderComponent extends AbstractComponent implements MongoProvider
{
  public @interface ClientConfig
  {
    String clientId();

    String uri();

    String databaseName() default "";
    
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

    int readPreferenceType() default 0;

    String[] readPreferenceTags() default {};

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
  private volatile String databaseName;
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
  public MongoDatabase getMongoDatabase()
  {
    return getMongoClient().getDatabase(databaseName);
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

    databaseName = config.databaseName();
    
    // The uriProperty is a single string containing one or more server URIs.
    // When more than one URI is specified, it denotes a replica set and the
    // URIs must be separated by a comma (CSV).

    uris = new ArrayList<String>();
    handleIllegalConfiguration(validateURI(config.uri(), uris));

    MongoClientOptions options = createMongoClientOptions(config);
    String currentURI = null;

    try
    {
      if (uris.size() == 1)
      {
        currentURI = uris.iterator().next();
        ServerAddress serverAddress = createServerAddress(currentURI);
        mongoClient = createMongoClient(serverAddress, options);

        String[] segments = currentURI.split("/");

        if (segments.length == 4)
          databaseName = segments[3];
      }
      else
      {
        ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>(uris.size());

        for (String uri : uris)
        {
          currentURI = uri;
          serverAddresses.add(createServerAddress(currentURI));
          String[] segments = currentURI.split("/");

          if (segments.length == 4)
            databaseName = segments[3];

        }

        mongoClient = createMongoClient(serverAddresses, options);
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
    super.bindLogService(logService);
    ;
  }

  public void unbindLogService(LogService logService)
  {
    super.unbindLogService(logService);
  }

  protected MongoClient createMongoClient(ArrayList<ServerAddress> serverAddresses, MongoClientOptions options)
  {
    return new MongoClient(serverAddresses, options);
  }

  protected MongoClient createMongoClient(ServerAddress serverAddress, MongoClientOptions options)
  {
    return new MongoClient(serverAddress, options);
  }

  private static String validateURI(String value, Collection<String> uris)
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

      if (!uri.startsWith("mongodb://") || uri.endsWith("/") || segments.length < 4 || segments.length > 5)
        return "The uri: '" + uri + "' does not have the form 'mongodb://host[:port]/[database]'";

      if (uris != null)
        uris.add(uri);
    }

    return null;
  }

  private MongoClientOptions createMongoClientOptions(ClientConfig config)
  {
    MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();

    optionsBuilder.alwaysUseMBeans(config.alwaysUseMBeans());
    optionsBuilder.connectionsPerHost(config.connectionsPerHost());
    optionsBuilder.connectTimeout(config.connectTimeout());
    optionsBuilder.cursorFinalizerEnabled(config.cursorFinalizerEnabled());
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
    optionsBuilder.serverSelectionTimeout(config.serverSelectionTimeout());
    optionsBuilder.socketKeepAlive(config.socketKeepAlive());
    optionsBuilder.socketTimeout(config.socketTimeout());
    optionsBuilder.sslEnabled(config.sslEnabled());
    optionsBuilder.sslInvalidHostNameAllowed(config.sslInvalidHostNameAllowed());
    optionsBuilder.threadsAllowedToBlockForConnectionMultiplier(config.threadsAllowedToBlockForConnectionMultiplier());
    WriteConcern writeConcern = new WriteConcern(config.writeConcernW(), config.writeConcernWtimeout(), config.writeConcernFsync(), config.writeConcernJ());
    optionsBuilder.writeConcern(writeConcern);

    if (!config.requiredReplicaSetName().isEmpty())
      optionsBuilder.requiredReplicaSetName(config.requiredReplicaSetName());

    List<Tag> tags = new ArrayList<>();

    if (config.readPreferenceTags() != null)
    {
      for (String tag : config.readPreferenceTags())
      {
        String[] elements = tag.split("=");
        tags.add(new Tag(elements[0], elements[1]));
      }
    }

    TagSet tagSet = new TagSet(tags);

    switch (config.readPreferenceType())
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
