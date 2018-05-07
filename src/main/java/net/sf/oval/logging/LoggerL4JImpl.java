/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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

    @Override
    public void debug(final String msg) {
        log4jLogger.log(WRAPPER, Level.DEBUG, msg, null);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.DEBUG, msg, t);
    }

    @Override
    public void error(final String msg) {
        log4jLogger.log(WRAPPER, Level.ERROR, msg, null);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.ERROR, msg, t);
    }

    @Override
    public void info(final String msg) {
        log4jLogger.log(WRAPPER, Level.INFO, msg, null);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.INFO, msg, t);
    }

    @Override
    public boolean isDebug() {
        return log4jLogger.isDebugEnabled();
    }

    @Override
    public boolean isError() {
        return log4jLogger.isEnabledFor(Level.ERROR);
    }

    @Override
    public boolean isInfo() {
        return log4jLogger.isInfoEnabled();
    }

    @Override
    public boolean isTrace() {
        return log4jLogger.isTraceEnabled();
    }

    @Override
    public boolean isWarn() {
        return log4jLogger.isEnabledFor(Level.WARN);
    }

    @Override
    public void trace(final String msg) {
        log4jLogger.log(WRAPPER, Level.TRACE, msg, null);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.TRACE, msg, t);
    }

    @Override
    public void warn(final String msg) {
        log4jLogger.log(WRAPPER, Level.WARN, msg, null);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        log4jLogger.log(WRAPPER, Level.WARN, msg, t);
    }
}
