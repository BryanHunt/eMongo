package org.eclipselabs.emongo.stats;

import org.bson.Document;

/**
 * This OSGi service API for providing a MongoDB stats publisher.  The database
 * monitor component uses the whiteboard pattern and calls all instances of
 * MongoServerStatsPublisher with the latest snapshot of the MongoDB database
 * stats collected from the serverStatus command.  It is up to the implementation 
 * of this service API to interpret the database stats.
 * 
 * @author bhunt
 * 
 */
public interface MongoServerStatsPublisher
{
  /**
   * Called by the database monitor component at a configured interval
   * 
   * @param stats the MongoDB database stats from serverStatus 
   */
  void publishStats(Document stats);
}
