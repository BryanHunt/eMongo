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

package org.eclipselabs.emongo.components;

import org.osgi.service.log.LogService;

/**
 * @author bhunt
 * 
 */
public abstract class AbstractComponent
{
	private volatile LogService logService;

	public void bindLogService(LogService logService)
	{
		this.logService = logService;
	}

	public void unbindLogService(LogService logService)
	{
		if (logService == this.logService)
			this.logService = null;
	}

	protected void handleIllegalConfiguration(String message)
	{
		LogService ls = logService;

		if (ls != null)
			ls.log(LogService.LOG_ERROR, message);

		throw new IllegalStateException(message);
	}

	protected void handleConfigurationException(String message, Exception e)
	{
		LogService ls = logService;

		if (ls != null)
			ls.log(LogService.LOG_ERROR, message, e);

		throw new IllegalStateException(e);
	}
}