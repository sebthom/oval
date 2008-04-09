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

import org.apache.log4j.Level;

/**
 * Log4J Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerL4JImpl implements Logger
{
	private final static String WRAPPER = "net.sf.oval.logging";

	private final org.apache.log4j.Logger log;

	public LoggerL4JImpl(final String name)
	{
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		log = org.apache.log4j.Logger.getLogger(name);
	}

	public void debug(final String msg)
	{
		log.log(WRAPPER, Level.DEBUG, msg, null);
	}

	public void debug(final String msg, final Throwable t)
	{
		log.log(WRAPPER, Level.DEBUG, msg, t);
	}

	public void error(final String msg)
	{
		log.log(WRAPPER, Level.ERROR, msg, null);
	}

	public void error(final String msg, final Throwable t)
	{
		log.log(WRAPPER, Level.ERROR, msg, t);
	}

	public void info(final String msg)
	{
		log.log(WRAPPER, Level.INFO, msg, null);
	}

	public void info(final String msg, final Throwable t)
	{
		log.log(WRAPPER, Level.INFO, msg, t);
	}

	public boolean isDebug()
	{
		return log.isDebugEnabled();
	}

	public boolean isError()
	{
		return log.isEnabledFor(Level.ERROR);
	}

	public boolean isInfo()
	{
		return log.isInfoEnabled();
	}

	public boolean isTrace()
	{
		return log.isTraceEnabled();
	}

	public boolean isWarn()
	{
		return log.isEnabledFor(Level.WARN);
	}

	public void trace(final String msg)
	{
		log.log(WRAPPER, Level.TRACE, msg, null);
	}

	public void trace(final String msg, final Throwable t)
	{
		log.log(WRAPPER, Level.TRACE, msg, t);
	}

	public void warn(final String msg)
	{
		log.log(WRAPPER, Level.WARN, msg, null);
	}

	public void warn(final String msg, final Throwable t)
	{
		log.log(WRAPPER, Level.WARN, msg, t);
	}
}
