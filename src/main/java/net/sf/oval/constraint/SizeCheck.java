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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class SizeCheck extends AbstractAnnotationCheck<Size> {
    private static final long serialVersionUID = 1L;

    private int min;
    private int max;

    @Override
    public void configure(final Size constraintAnnotation) {
        super.configure(constraintAnnotation);
        setMax(constraintAnnotation.max());
        setMin(constraintAnnotation.min());
    }

    @Override
    protected Map<String, String> createMessageVariables() {
        final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
        messageVariables.put("max", Integer.toString(max));
        messageVariables.put("min", Integer.toString(min));
        return messageVariables;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    @Override
    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
        if (valueToValidate == null)
            return true;

        if (valueToValidate instanceof Collection<?>) {
            final int size = ((Collection<?>) valueToValidate).size();
            return size >= min && size <= max;
        }
        if (valueToValidate instanceof Map<?, ?>) {
            final int size = ((Map<?, ?>) valueToValidate).size();
            return size >= min && size <= max;
        }
        if (valueToValidate.getClass().isArray()) {
            final int size = Array.getLength(valueToValidate);
            return size >= min && size <= max;
        }
        final String str = valueToValidate.toString();
        final int size = str.length();
        return size >= min && size <= max;
    }

    public void setMax(final int max) {
        this.max = max;
        requireMessageVariablesRecreation();
    }

    public void setMin(final int min) {
        this.min = min;
        requireMessageVariablesRecreation();
    }
}
