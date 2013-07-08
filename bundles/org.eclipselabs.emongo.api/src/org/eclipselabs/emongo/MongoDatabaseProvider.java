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
	String getAlias();

	DB getDB();
}
