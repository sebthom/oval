/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
 *
 * @author Sebastian Thomschke
 */
public class LoggerSLF4JImpl implements Logger {
    private final org.slf4j.Logger slf4jLogger;

    /**
     * @param name the name of the logger
     * @throws IllegalArgumentException if <code>name == null</code>
     */
    public LoggerSLF4JImpl(final String name) throws IllegalArgumentException {
        Assert.argumentNotNull("name", name);
        slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
    }

    public void debug(final String msg) {
        slf4jLogger.debug(msg);
    }

    public void debug(final String msg, final Throwable t) {
        slf4jLogger.debug(msg, t);
    }

    public void error(final String msg) {
        slf4jLogger.error(msg);
    }

    public void error(final String msg, final Throwable t) {
        slf4jLogger.error(msg, t);
    }

    public void info(final String msg) {
        slf4jLogger.info(msg);
    }

    public void info(final String msg, final Throwable t) {
        slf4jLogger.info(msg, t);
    }

    public boolean isDebug() {
        return slf4jLogger.isDebugEnabled();
    }

    public boolean isError() {
        return slf4jLogger.isErrorEnabled();
    }

    public boolean isInfo() {
        return slf4jLogger.isInfoEnabled();
    }

    public boolean isTrace() {
        return slf4jLogger.isTraceEnabled();
    }

    public boolean isWarn() {
        return slf4jLogger.isWarnEnabled();
    }

    public void trace(final String msg) {
        slf4jLogger.trace(msg);
    }

    public void trace(final String msg, final Throwable t) {
        slf4jLogger.trace(msg, t);
    }

    public void warn(final String msg) {
        slf4jLogger.warn(msg);
    }

    public void warn(final String msg, final Throwable t) {
        slf4jLogger.warn(msg, t);
    }
}
