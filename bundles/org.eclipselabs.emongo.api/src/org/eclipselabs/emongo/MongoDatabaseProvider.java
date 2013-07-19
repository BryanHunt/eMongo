/**
 * 
 */

package org.eclipselabs.emongo;

import com.mongodb.DB;

/**
 * @author bhunt
 * 
 */
public interface MongoDatabaseProvider
{
	String PROP_ALIAS = "alias";

	String getAlias();

	String getURI();

	DB getDB();
}
