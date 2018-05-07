/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.context;

import java.lang.reflect.Method;

import net.sf.oval.internal.util.SerializableMethod;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MethodEntryContext extends OValContext {
    private static final long serialVersionUID = 1L;

    private final SerializableMethod method;

    public MethodEntryContext(final Method method) {
        this.method = SerializableMethod.getInstance(method);
    }

    public Method getMethod() {
        return method.getMethod();
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + "." + method.getName() + "(" + StringUtils.implode(method.getParameterTypes(), ",") + ")";
    }
}
