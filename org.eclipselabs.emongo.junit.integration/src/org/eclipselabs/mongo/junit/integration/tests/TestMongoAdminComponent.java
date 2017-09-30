package org.eclipselabs.mongo.junit.integration.tests;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.bson.Document;
import org.eclipselabs.emongo.admin.MongoAdmin;
import org.eclipselabs.emongo.junit.util.MongoDatabaseLocator;
import org.eclipselabs.eunit.junit.utils.ServiceConfigurator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TestMongoAdminComponent
{
  private static Dictionary<String, Object> adminConfig = new Hashtable<>();
  
  static
  {
    adminConfig.put("mongodbBinDir", "/Library/MongoDB/bin");
  }
  
  @Rule
  public MongoDatabaseLocator databaseLocator = new MongoDatabaseLocator("localhost", 27017, "junit", "junit");
  
  @Rule
  public ServiceConfigurator<MongoAdmin> mongoAdmin = new ServiceConfigurator<>(MongoAdmin.class, MongoAdmin.PID, adminConfig);
  
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private MongoAdmin adminService;

  @Before
  public void setUp()
  {
    adminService = mongoAdmin.getService();  
  }
  
  @Test
  public void testDumpAndRestoreDatabase() throws IOException, InterruptedException
  {
    MongoDatabase database = databaseLocator.getDatabase();
    MongoCollection<Document> collection = database.getCollection("test");
    collection.insertOne(new Document("junit", "junit"));
    assertTrue(adminService.dumpDatabase("localhost", database.getName(), tempFolder.getRoot()));
    database.drop();
    assertThat(collection.find(new Document("junit", "junit")).first(), is(nullValue()));
    assertTrue(adminService.restoreDatabase("localhost", database.getName(), tempFolder.getRoot()));
    assertThat(collection.find(new Document("junit", "junit")).first(), is(notNullValue()));
  }
}
