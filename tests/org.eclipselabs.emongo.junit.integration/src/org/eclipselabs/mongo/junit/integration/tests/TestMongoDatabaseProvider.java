/**
 * 
 */

package org.eclipselabs.mongo.junit.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.eclipselabs.eunit.junit.utils.ServiceLocator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public class TestMongoDatabaseProvider
{
	@Rule
	public ServiceLocator<MongoDatabaseProvider> mongoDatabaseProviderLocator = new ServiceLocator<MongoDatabaseProvider>(MongoDatabaseProvider.class);

	private MongoDatabaseProvider mongoDatabaseProvider;

	@Before
	public void setUp()
	{
		mongoDatabaseProvider = mongoDatabaseProviderLocator.getService();
	}

	@Test
	public void testGetAlias()
	{
		assertThat(mongoDatabaseProvider.getAlias(), is("junit"));
	}

	@Test
	public void testGetDB()
	{
		DB db = mongoDatabaseProvider.getDB();
		assertThat(db, is(notNullValue()));
		assertThat(db.getName(), is("junit"));
		assertThat(db.getMongo().getAddress().getHost(), is("localhost"));
	}
}
