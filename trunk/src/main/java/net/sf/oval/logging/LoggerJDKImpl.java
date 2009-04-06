/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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

import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.oval.internal.util.Assert;

/**
 * JDK Logging Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerJDKImpl implements Logger
{
	private final java.util.logging.Logger jdkLogger;
	private final String name;

	/**
	 * @param name the name of the logger
	 * @throws IllegalArgumentException if <code>name == null</code>
	 */
	public LoggerJDKImpl(final String name) throws IllegalArgumentException
	{
		Assert.notNull("name", name);

		this.name = name;
		jdkLogger = java.util.logging.Logger.getLogger(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg)
	{
		log(Level.FINE, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(final String msg, final Throwable t)
	{
		log(Level.FINE, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg)
	{
		log(Level.SEVERE, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(final String msg, final Throwable t)
	{
		log(Level.SEVERE, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg)
	{
		log(Level.INFO, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(final String msg, final Throwable t)
	{
		log(Level.INFO, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebug()
	{
		return jdkLogger.isLoggable(Level.FINE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isError()
	{
		return jdkLogger.isLoggable(Level.SEVERE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfo()
	{
		return jdkLogger.isLoggable(Level.INFO);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTrace()
	{
		return jdkLogger.isLoggable(Level.FINEST);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarn()
	{
		return jdkLogger.isLoggable(Level.WARNING);
	}

	private void log(final Level level, final String msg, final Throwable t)
	{
		final LogRecord record = new LogRecord(level, msg);
		record.setLoggerName(name);
		record.setThrown(t);

		/* java.lang.Throwable
		 *	at net.sf.oval.logging.LoggerJDKImpl.log(LoggerJDKImpl.java:123)
		 *	at net.sf.oval.logging.LoggerJDKImpl.warn(LoggerJDKImpl.java:136)
		 *	at net.sf.oval.internal.Log.warn(Log.java:180)
		 */
		final int offset = 2;
		final StackTraceElement[] steArray = new Throwable().getStackTrace();
		record.setSourceClassName(steArray[offset].getClassName());
		record.setSourceMethodName(steArray[offset].getMethodName());

		jdkLogger.log(record);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg)
	{
		log(Level.FINEST, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(final String msg, final Throwable t)
	{
		log(Level.FINEST, msg, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg)
	{
		log(Level.WARNING, msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(final String msg, final Throwable t)
	{
		log(Level.WARNING, msg, t);
	}
}
