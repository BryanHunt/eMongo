/*******************************************************************************
 * Copyright (c) 2013 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import org.eclipselabs.emongo.MongoDatabaseProvider;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author bhunt
 * 
 */
public class MongoLogListener implements LogListener
{
	public static final String PROP_DATABASE_FILTER = "MongoDatabaseProvider.target";
	public static final String PROP_COLLECTION = "collection";
	public static final String PROP_MAX_LEVEL = "maxLevel";

	private volatile LogReaderService logReaderService;
	private volatile MongoDatabaseProvider mongoDatabaseProvider;
	private volatile DBCollection logCollection;
	private volatile Integer maxLevel;

	public void activate(Map<String, Object> properties)
	{
		String collection = (String) properties.get(PROP_COLLECTION);
		maxLevel = (Integer) properties.get(PROP_MAX_LEVEL);

		if (collection == null || collection.isEmpty())
			throw new IllegalStateException("The collection property cannot be empty");

		if (maxLevel == null)
			maxLevel = LogService.LOG_ERROR;

		logCollection = mongoDatabaseProvider.getDB().getCollection(collection);
		logReaderService.addLogListener(this);
	}

	public void deactivate()
	{
		logReaderService.removeLogListener(this);
	}

	@Override
	public void logged(LogEntry entry)
	{
		if (entry.getLevel() > maxLevel)
			return;

		DBObject logData = new BasicDBObject();
		logData.put("level", entry.getLevel());
		logData.put("bsn", entry.getBundle().getSymbolicName());
		logData.put("time", new Date(entry.getTime()));
		logData.put("message", entry.getMessage());

		@SuppressWarnings("rawtypes")
		ServiceReference serviceReference = entry.getServiceReference();

		if (serviceReference != null)
			logData.put("pid", serviceReference.getProperty(Constants.SERVICE_PID));

		Throwable exception = entry.getException();

		if (exception != null)
		{
			DBObject exceptionData = new BasicDBObject();
			exceptionData.put("message", exception.getMessage());
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			exception.printStackTrace(printWriter);
			exceptionData.put("stack", stringWriter.toString());

			logData.put("exception", exceptionData);
		}

		logCollection.insert(logData);
	}

	public void bindLogReaderService(LogReaderService logReaderService)
	{
		this.logReaderService = logReaderService;
	}

	public void bindMongoDatabaseProvider(MongoDatabaseProvider mongoDatabaseProvider)
	{
		this.mongoDatabaseProvider = mongoDatabaseProvider;
	}
}
