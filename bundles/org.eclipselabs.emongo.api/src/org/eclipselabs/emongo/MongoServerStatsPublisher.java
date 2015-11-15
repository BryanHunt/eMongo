package org.eclipselabs.emongo;

import org.bson.Document;

public interface MongoServerStatsPublisher
{
  void publishStats(Document stats);
}
