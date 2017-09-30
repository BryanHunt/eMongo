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

import org.bson.Document;
import org.eclipselabs.emongo.client.MongoProvider;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import com.mongodb.client.MongoCollection;

/**
 * @author bhunt
 * 
 */
@Component(configurationPid = {"org.eclipselabs.emongo.log"})
public class MongoLogListener implements LogListener
{
  public @interface LogConfig
  {
    String collection();
    int maxLevel() default LogService.LOG_ERROR;
  }
  
	private volatile LogReaderService logReaderService;
	private volatile MongoProvider mongoProvider;
	private volatile MongoCollection<Document> logCollection;
	private volatile Integer maxLevel;

	@Activate
	public void activate(LogConfig config)
	{
		String collection = config.collection();
		maxLevel = config.maxLevel();

		if (collection == null || collection.isEmpty())
			throw new IllegalStateException("The collection property cannot be empty");

		logCollection = mongoProvider.getMongoDatabase().getCollection(collection);
		logReaderService.addLogListener(this);
	}

	@Deactivate
	public void deactivate()
	{
		logReaderService.removeLogListener(this);
	}

	@Override
	public void logged(LogEntry entry)
	{
		if (entry.getLevel() > maxLevel)
			return;

		Document logData = new Document("level", entry.getLevel()).append("bsn", entry.getBundle().getSymbolicName()).append("time", new Date(entry.getTime())).append("message", entry.getMessage());

		@SuppressWarnings("rawtypes")
		ServiceReference serviceReference = entry.getServiceReference();

		if (serviceReference != null)
			logData.put("pid", serviceReference.getProperty(Constants.SERVICE_PID));

		Throwable exception = entry.getException();

		if (exception != null)
		{

			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			exception.printStackTrace(printWriter);

			Document exceptionData = new Document("message", exception.getMessage()).append("stack", stringWriter.toString());

			logData.put("exception", exceptionData);
		}

		logCollection.insertOne(logData);
	}

	@Reference(unbind = "-")
	public void bindLogReaderService(LogReaderService logReaderService)
	{
		this.logReaderService = logReaderService;
	}

  @Reference(unbind = "-")
	public void bindMongoClientProvider(MongoProvider mongoProvider)
	{
		this.mongoProvider = mongoProvider;
	}
}
