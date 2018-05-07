/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfAnyCheck extends AbstractAnnotationCheck<InstanceOfAny> {
    private static final long serialVersionUID = 1L;

    private Class<?>[] types;

    @Override
    public void configure(final InstanceOfAny constraintAnnotation) {
        super.configure(constraintAnnotation);
        setTypes(constraintAnnotation.value());
    }

    @Override
    protected Map<String, String> createMessageVariables() {
        final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
        if (types.length == 1) {
            messageVariables.put("types", types[0].getName());
        } else {
            final String[] classNames = new String[types.length];
            for (int i = 0, l = classNames.length; i < l; i++) {
                classNames[i] = types[i].getName();
            }
            messageVariables.put("types", StringUtils.implode(classNames, ","));
        }
        return messageVariables;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    @Override
    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
        if (valueToValidate == null)
            return true;

        for (final Class<?> type : types)
            if (type.isInstance(valueToValidate))
                return true;
        return false;
    }

    public void setTypes(final Class<?>... types) {
        this.types = types;
        requireMessageVariablesRecreation();
    }
}
