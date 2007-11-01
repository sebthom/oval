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

/**
 * @author Sebastian Thomschke
 */
public interface Logger
{
	void debug(String msg);

	void debug(String msgFormat, Object arg1);

	void debug(String msgFormat, Object arg1, Throwable t);

	void debug(String msg, Throwable t);

	void error(String msg);

	void error(String msgFormat, Object arg1);

	void error(String msgFormat, Object arg1, Throwable t);

	void error(String msg, Throwable t);

	void info(String msg);

	void info(String msgFormat, Object arg1);

	void info(String msgFormat, Object arg1, Throwable t);

	void info(String msg, Throwable t);

	boolean isDebug();

	boolean isError();

	boolean isInfo();

	boolean isTrace();

	boolean isWarn();

	void trace(String msg);

	void trace(String msgFormat, Object arg1);

	void trace(String msgFormat, Object arg1, Throwable t);

	void trace(String msg, Throwable t);

	void warn(String msg);

	void warn(String msgFormat, Object arg1);

	void warn(String msgFormat, Object arg1, Throwable t);

	void warn(String msg, Throwable t);
}
