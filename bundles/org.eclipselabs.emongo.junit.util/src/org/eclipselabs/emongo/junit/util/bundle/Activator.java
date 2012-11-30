
package org.eclipselabs.emongo.junit.util.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	private static volatile BundleContext bundleContext;

	public static BundleContext getBundleContext()
	{
		return bundleContext;
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		bundleContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		bundleContext = null;
	}
}
