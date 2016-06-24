/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval.logging;

import net.sf.oval.internal.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Commons Logging Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerJCLImpl implements Logger
{
	private final Log jclLoggger;

	/**
	 * @param name the name of the logger
	 * @throws IllegalArgumentException if <code>name == null</code>
	 */
	public LoggerJCLImpl(final String name) throws IllegalArgumentException
	{
		Assert.argumentNotNull("name", name);

		jclLoggger = LogFactory.getLog(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg)
	{
		jclLoggger.debug(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg, final Throwable t)
	{
		jclLoggger.debug(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg)
	{
		jclLoggger.error(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg, final Throwable t)
	{
		jclLoggger.error(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg)
	{
		jclLoggger.info(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg, final Throwable t)
	{
		jclLoggger.info(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebug()
	{
		return jclLoggger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isError()
	{
		return jclLoggger.isErrorEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfo()
	{
		return jclLoggger.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTrace()
	{
		return jclLoggger.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarn()
	{
		return jclLoggger.isWarnEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg)
	{
		jclLoggger.trace(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg, final Throwable t)
	{
		jclLoggger.trace(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg)
	{
		jclLoggger.warn(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg, final Throwable t)
	{
		jclLoggger.warn(msg, t);
	}
}
