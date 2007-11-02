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
package net.sf.oval.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.logging.Logger;
import net.sf.oval.logging.LoggerFactory;
import net.sf.oval.logging.LoggerFactoryJDKImpl;

/**
 * @author Sebastian Thomschke
 */
public final class Log
{
	/* cannot use CollectionFactoryHolder.getFactory().createMap(32) here, since 
	 * the collection factory uses the Log itself which is not yet initialized
	 */
	private final static Map<String, Log> logRegistry = new HashMap<String, Log>(32);

	private static LoggerFactory loggerFactory = new LoggerFactoryJDKImpl();

	public static Log getLog(final Class clazz)
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");
		return getLog(clazz.getName());
	}

	public synchronized static Log getLog(final String name)
	{
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		Log log = logRegistry.get(name);
		if (log == null)
		{
			log = new Log(loggerFactory.createLogger(name));
		}
		return log;
	}

	/**
	 * @return the loggerFactory
	 */
	public static LoggerFactory getLoggerFactory()
	{
		synchronized (logRegistry)
		{
			return loggerFactory;
		}
	}

	/**
	 * @param loggerFactory the loggerFactory to set
	 */
	public static void setLoggerFactory(final LoggerFactory loggerFactory)
	{
		if (loggerFactory == null)
			throw new IllegalArgumentException("loggerFactory cannot be null");

		synchronized (logRegistry)
		{
			Log.loggerFactory = loggerFactory;
			for (final Entry<String, Log> entry : logRegistry.entrySet())
			{
				entry.getValue().setLogger(loggerFactory.createLogger(entry.getKey()));
			}
		}
	}

	private Logger logger;

	/**
	 * private constructor to avoid external instantiation
	 */
	private Log(final Logger logger)
	{
		setLogger(logger);
	}

	public void debug(final String msg)
	{
		logger.debug(msg);
	}

	public void debug(final String msgFormat, final Object arg1)
	{
		if (logger.isDebug())
			logger.debug(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()));
	}

	public void debug(final String msgFormat, final Object arg1, final Throwable t)
	{
		if (logger.isDebug())
			logger.debug(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()), t);
	}

	public void debug(final String msg, final Throwable t)
	{
		logger.debug(msg, t);
	}

	public void error(final String msg)
	{
		logger.error(msg);
	}

	public void error(final String msgFormat, final Object arg1)
	{
		if (logger.isError())
			logger.error(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()));
	}

	public void error(final String msgFormat, final Object arg1, final Throwable t)
	{
		if (logger.isError())
			logger.error(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()), t);
	}

	public void error(final String msg, final Throwable t)
	{
		logger.error(msg, t);
	}

	public void info(final String msg)
	{
		logger.info(msg);
	}

	public void info(final String msgFormat, final Object arg1)
	{
		if (logger.isInfo())
			logger.info(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()));
	}

	public void info(final String msgFormat, final Object arg1, final Throwable t)
	{
		if (logger.isInfo())
			logger.info(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()), t);
	}

	public void info(final String msg, final Throwable t)
	{
		logger.info(msg, t);
	}

	public boolean isDebug()
	{
		return logger.isDebug();
	}

	public boolean isError()
	{
		return logger.isError();
	}

	public boolean isInfo()
	{
		return logger.isInfo();
	}

	public boolean isTrace()
	{
		return logger.isTrace();
	}

	public boolean isWarn()
	{
		return logger.isWarn();
	}

	private void setLogger(final Logger logger)
	{
		if (logger == null) throw new IllegalArgumentException("logger cannot be null");

		this.logger = logger;
	}

	public void trace(final String msg)
	{
		logger.debug(msg);
	}

	public void trace(final String msgFormat, final Object arg1)
	{
		if (logger.isDebug())
			logger.trace(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()));
	}

	public void trace(final String msgFormat, final Object arg1, final Throwable t)
	{
		if (logger.isDebug())
			logger.trace(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()), t);
	}

	public void trace(final String msg, final Throwable t)
	{
		logger.trace(msg, t);
	}

	public void warn(final String msg)
	{
		logger.warn(msg);
	}

	public void warn(final String msgFormat, final Object arg1)
	{
		if (logger.isWarn())
			logger.warn(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()));
	}

	public void warn(final String msgFormat, final Object arg1, final Throwable t)
	{
		if (logger.isWarn())
			logger.warn(StringUtils.replaceAll(msgFormat, "{}", arg1 == null ? "null" : arg1
					.toString()), t);
	}

	public void warn(final String msg, final Throwable t)
	{
		logger.warn(msg, t);
	}

}
