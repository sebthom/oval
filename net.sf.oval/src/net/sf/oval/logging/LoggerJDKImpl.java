/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import net.sf.oval.internal.util.StringUtils;

/**
 * JDK Logging Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerJDKImpl implements Logger
{
	private final java.util.logging.Logger log;
	private final String name;

	public LoggerJDKImpl(final String name)
	{
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		this.name = name;
		log = java.util.logging.Logger.getLogger(name);
	}

	public void debug(final String msg)
	{
		log(Level.FINE, msg, null);
	}

	public void debug(final String msgFormat, final Object arg1)
	{
		log(Level.FINE, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), null);
	}

	public void debug(final String msgFormat, final Object arg1, final Throwable t)
	{
		log(Level.FINE, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), t);
	}

	public void debug(final String msg, final Throwable t)
	{
		log(Level.FINE, msg, t);
	}

	public void error(final String msg)
	{
		log(Level.SEVERE, msg, null);
	}

	public void error(final String msgFormat, final Object arg1)
	{
		log(Level.SEVERE, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), null);
	}

	public void error(final String msgFormat, final Object arg1, final Throwable t)
	{
		log(Level.SEVERE, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), t);
	}

	public void error(final String msg, final Throwable t)
	{
		log(Level.SEVERE, msg, t);
	}

	public void info(final String msg)
	{
		log(Level.INFO, msg, null);
	}

	public void info(final String msgFormat, final Object arg1)
	{
		log(Level.INFO, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), null);
	}

	public void info(final String msgFormat, final Object arg1, final Throwable t)
	{
		log(Level.INFO, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), t);
	}

	public void info(final String msg, final Throwable t)
	{
		log(Level.INFO, msg, t);
	}

	public boolean isDebug()
	{
		return log.isLoggable(Level.FINE);
	}

	public boolean isError()
	{
		return log.isLoggable(Level.SEVERE);
	}

	public boolean isInfo()
	{
		return log.isLoggable(Level.INFO);
	}

	public boolean isTrace()
	{
		return log.isLoggable(Level.FINEST);
	}

	public boolean isWarn()
	{
		return log.isLoggable(Level.WARNING);
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
		final int offset = 3;
		final StackTraceElement[] steArray = new Throwable().getStackTrace();
		record.setSourceClassName(steArray[offset].getClassName());
		record.setSourceMethodName(steArray[offset].getMethodName());

		log.log(record);
	}

	public void trace(final String msg)
	{
		log(Level.FINEST, msg, null);
	}

	public void trace(final String msgFormat, final Object arg1)
	{
		log(Level.FINEST, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), null);
	}

	public void trace(final String msgFormat, final Object arg1, final Throwable t)
	{
		log(Level.FINEST, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), t);
	}

	public void trace(final String msg, final Throwable t)
	{
		log(Level.FINEST, msg, t);
	}

	public void warn(final String msg)
	{
		log(Level.WARNING, msg, null);
	}

	public void warn(final String msgFormat, final Object arg1)
	{
		log(Level.WARNING, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), null);
	}

	public void warn(final String msgFormat, final Object arg1, final Throwable t)
	{
		log(Level.WARNING, StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
				.toString()), t);
	}

	public void warn(final String msg, final Throwable t)
	{
		log(Level.WARNING, msg, t);
	}
}
