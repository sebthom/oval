/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

/**
 * SLF4J Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerSLF4JImpl implements Logger
{
	private final org.slf4j.Logger slf4jLogger;

	/**
	 * @param name the name of the logger
	 * @throws IllegalArgumentException if <code>name == null</code>
	 */
	public LoggerSLF4JImpl(final String name) throws IllegalArgumentException
	{
		Assert.notNull("name", name);
		slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg)
	{
		slf4jLogger.debug(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg, final Throwable t)
	{
		slf4jLogger.debug(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg)
	{
		slf4jLogger.error(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg, final Throwable t)
	{
		slf4jLogger.error(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg)
	{
		slf4jLogger.info(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg, final Throwable t)
	{
		slf4jLogger.info(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebug()
	{
		return slf4jLogger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isError()
	{
		return slf4jLogger.isErrorEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfo()
	{
		return slf4jLogger.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTrace()
	{
		return slf4jLogger.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarn()
	{
		return slf4jLogger.isWarnEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg)
	{
		slf4jLogger.trace(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg, final Throwable t)
	{
		slf4jLogger.trace(msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg)
	{
		slf4jLogger.warn(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg, final Throwable t)
	{
		slf4jLogger.warn(msg, t);
	}
}
