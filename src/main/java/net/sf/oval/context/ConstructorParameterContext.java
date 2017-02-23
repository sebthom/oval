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
package net.sf.oval.context;

import java.lang.reflect.Constructor;

import net.sf.oval.Validator;
import net.sf.oval.internal.util.SerializableConstructor;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstructorParameterContext extends OValContext {
    private static final long serialVersionUID = 1L;

    private final SerializableConstructor constructor;
    private final int parameterIndex;
    private final String parameterName;

    public ConstructorParameterContext(final Constructor<?> constructor, final int parameterIndex, final String parameterName) {
        this.constructor = SerializableConstructor.getInstance(constructor);
        this.parameterIndex = parameterIndex;
        this.parameterName = parameterName;
        compileTimeType = constructor.getParameterTypes()[parameterIndex];
    }

    public Constructor<?> getConstructor() {
        return constructor.getConstructor();
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public String getParameterName() {
        return parameterName;
    }

    @Override
    public String toString() {
        return constructor.getDeclaringClass().getName() + "(" + StringUtils.implode(constructor.getParameterTypes(), ",") + ") " + Validator
            .getMessageResolver().getMessage("net.sf.oval.context.ConstructorParameterContext.parameter") + " " + parameterIndex + (parameterName == null
                    || parameterName.length() == 0 ? "" : " (" + parameterName + ")");
    }
}
