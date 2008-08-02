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

import org.apache.log4j.Level;

/**
 * Log4J Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerL4JImpl implements Logger
{
	private final static String WRAPPER = "net.sf.oval.logging";

	private final org.apache.log4j.Logger log4jLogger;

	/**
	 * @param name the name of the logger
	 * @throws IllegalArgumentException if <code>name == null</code>
	 */
	public LoggerL4JImpl(final String name) throws IllegalArgumentException
	{
		Assert.notNull("name", name);
		log4jLogger = org.apache.log4j.Logger.getLogger(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg)
	{
		log4jLogger.log(WRAPPER, Level.DEBUG, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg, final Throwable t)
	{
		log4jLogger.log(WRAPPER, Level.DEBUG, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg)
	{
		log4jLogger.log(WRAPPER, Level.ERROR, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg, final Throwable t)
	{
		log4jLogger.log(WRAPPER, Level.ERROR, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg)
	{
		log4jLogger.log(WRAPPER, Level.INFO, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg, final Throwable t)
	{
		log4jLogger.log(WRAPPER, Level.INFO, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebug()
	{
		return log4jLogger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isError()
	{
		return log4jLogger.isEnabledFor(Level.ERROR);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfo()
	{
		return log4jLogger.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTrace()
	{
		return log4jLogger.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarn()
	{
		return log4jLogger.isEnabledFor(Level.WARN);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg)
	{
		log4jLogger.log(WRAPPER, Level.TRACE, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg, final Throwable t)
	{
		log4jLogger.log(WRAPPER, Level.TRACE, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg)
	{
		log4jLogger.log(WRAPPER, Level.WARN, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg, final Throwable t)
	{
		log4jLogger.log(WRAPPER, Level.WARN, msg, t);
	}
}
