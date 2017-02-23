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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.oval.internal.util.Assert;

/**
 * Commons Logging Wrapper
 *
 * @author Sebastian Thomschke
 */
public class LoggerJCLImpl implements Logger {
    private final Log jclLoggger;

    /**
     * @param name the name of the logger
     * @throws IllegalArgumentException if <code>name == null</code>
     */
    public LoggerJCLImpl(final String name) throws IllegalArgumentException {
        Assert.argumentNotNull("name", name);

        jclLoggger = LogFactory.getLog(name);
    }

    public void debug(final String msg) {
        jclLoggger.debug(msg);
    }

    public void debug(final String msg, final Throwable t) {
        jclLoggger.debug(msg, t);
    }

    public void error(final String msg) {
        jclLoggger.error(msg);
    }

    public void error(final String msg, final Throwable t) {
        jclLoggger.error(msg, t);
    }

    public void info(final String msg) {
        jclLoggger.info(msg);
    }

    public void info(final String msg, final Throwable t) {
        jclLoggger.info(msg, t);
    }

    public boolean isDebug() {
        return jclLoggger.isDebugEnabled();
    }

    public boolean isError() {
        return jclLoggger.isErrorEnabled();
    }

    public boolean isInfo() {
        return jclLoggger.isInfoEnabled();
    }

    public boolean isTrace() {
        return jclLoggger.isTraceEnabled();
    }

    public boolean isWarn() {
        return jclLoggger.isWarnEnabled();
    }

    public void trace(final String msg) {
        jclLoggger.trace(msg);
    }

    public void trace(final String msg, final Throwable t) {
        jclLoggger.trace(msg, t);
    }

    public void warn(final String msg) {
        jclLoggger.warn(msg);
    }

    public void warn(final String msg, final Throwable t) {
        jclLoggger.warn(msg, t);
    }
}
