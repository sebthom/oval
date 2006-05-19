/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.bundle;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator when loaded as eclipse plugin.
 * 
 * This class is currently not used.
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public class Activator implements BundleActivator
{
	private static final Logger LOG = Logger.getLogger(Activator.class.getName());

	public void start(final BundleContext context)
	{
		LOG.fine("Bundle net.sf.oval started");
	}

	public void stop(final BundleContext context)
	{
		LOG.fine("Bundle net.sf.oval stopped");
	}
}
