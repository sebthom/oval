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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Commons Logging Wrapper
 * @author Sebastian Thomschke
 */
public class LoggerJCLImpl implements Logger
{
	private final Log log;

	/**
	 * @param name the name of the logger
	 * @throws IllegalArgumentException if <code>name == null</code>
	 */
	public LoggerJCLImpl(final String name) throws IllegalArgumentException
	{
		Assert.notNull("name", name);

		log = LogFactory.getLog(name);
	}

	public void debug(final String msg)
	{
		log.debug(msg);
	}

	public void debug(final String msg, final Throwable t)
	{
		log.debug(msg, t);
	}

	public void error(final String msg)
	{
		log.error(msg);
	}

	public void error(final String msg, final Throwable t)
	{
		log.error(msg, t);
	}

	public void info(final String msg)
	{
		log.info(msg);
	}

	public void info(final String msg, final Throwable t)
	{
		log.info(msg, t);
	}

	public boolean isDebug()
	{
		return log.isDebugEnabled();
	}

	public boolean isError()
	{
		return log.isErrorEnabled();
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
		return log.isWarnEnabled();
	}

	public void trace(final String msg)
	{
		log.trace(msg);
	}

	public void trace(final String msg, final Throwable t)
	{
		log.trace(msg, t);
	}

	public void warn(final String msg)
	{
		log.warn(msg);
	}

	public void warn(final String msg, final Throwable t)
	{
		log.warn(msg, t);
	}
}
