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

import org.apache.log4j.Level;

import net.sf.oval.internal.util.Assert;

/**
 * Log4J Wrapper
 *
 * @author Sebastian Thomschke
 */
public class LoggerL4JImpl implements Logger {
    private static final String WRAPPER = "net.sf.oval.logging";

    private final org.apache.log4j.Logger log4jLogger;

    /**
     * @param name the name of the logger
     * @throws IllegalArgumentException if <code>name == null</code>
     */
    public LoggerL4JImpl(final String name) throws IllegalArgumentException {
        Assert.argumentNotNull("name", name);
        log4jLogger = org.apache.log4j.Logger.getLogger(name);
    }

    public void debug(final String msg) {
        log4jLogger.log(WRAPPER, Level.DEBUG, msg, null);
    }

    public void debug(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.DEBUG, msg, t);
    }

    public void error(final String msg) {
        log4jLogger.log(WRAPPER, Level.ERROR, msg, null);
    }

    public void error(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.ERROR, msg, t);
    }

    public void info(final String msg) {
        log4jLogger.log(WRAPPER, Level.INFO, msg, null);
    }

    public void info(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.INFO, msg, t);
    }

    public boolean isDebug() {
        return log4jLogger.isDebugEnabled();
    }

    public boolean isError() {
        return log4jLogger.isEnabledFor(Level.ERROR);
    }

    public boolean isInfo() {
        return log4jLogger.isInfoEnabled();
    }

    public boolean isTrace() {
        return log4jLogger.isTraceEnabled();
    }

    public boolean isWarn() {
        return log4jLogger.isEnabledFor(Level.WARN);
    }

    public void trace(final String msg) {
        log4jLogger.log(WRAPPER, Level.TRACE, msg, null);
    }

    public void trace(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.TRACE, msg, t);
    }

    public void warn(final String msg) {
        log4jLogger.log(WRAPPER, Level.WARN, msg, null);
    }

    public void warn(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.WARN, msg, t);
    }
}
