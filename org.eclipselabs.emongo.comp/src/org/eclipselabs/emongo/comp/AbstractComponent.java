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

package org.eclipselabs.emongo.comp;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.log.LogService;

/**
 * @author bhunt
 * 
 */
public abstract class AbstractComponent
{
	private AtomicReference<LogService> logServiceReference = new AtomicReference<LogService>();

	protected void bindLogService(LogService logService)
	{
		logServiceReference.set(logService);
	}

	protected void unbindLogService(LogService logService)
	{
		logServiceReference.compareAndSet(logService, null);
	}

	protected void handleIllegalConfiguration(String message)
	{
		if (message == null)
			return;

		log(LogService.LOG_ERROR, message);
		throw new IllegalStateException(message);
	}

	protected void handleConfigurationException(String message, Exception e)
	{
	  log(LogService.LOG_ERROR, message, e);
		throw new IllegalStateException(e);
	}
	
	protected void log(int level, String message)
	{
    LogService logService = logServiceReference.get();

    if (logService != null)
      logService.log(level, message);	  
	}
	
	protected void log(int level, String message, Exception e)
	{
    LogService logService = logServiceReference.get();

    if (logService != null)
      logService.log(level, message, e);
	  
	}
}