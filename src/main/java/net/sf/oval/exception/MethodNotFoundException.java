/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class MethodNotFoundException extends ReflectionException {
    private static final long serialVersionUID = 1L;

    public MethodNotFoundException(final String message) {
        super(message);
    }

    public MethodNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MethodNotFoundException(final Throwable cause) {
        super(cause);
    }
}