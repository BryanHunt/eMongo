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

	DB getDB();
}
