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
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MinCheck extends AbstractAnnotationCheck<Min> {
    private static final long serialVersionUID = 1L;

    private boolean inclusive = true;
    private double min;

    @Override
    public void configure(final Min constraintAnnotation) {
        super.configure(constraintAnnotation);
        setMin(constraintAnnotation.value());
    }

    @Override
    protected Map<String, String> createMessageVariables() {
        final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
        messageVariables.put("min", Double.toString(min));
        return messageVariables;
    }

    @Override
    protected ConstraintTarget[] getAppliesToDefault() {
        return new ConstraintTarget[] { ConstraintTarget.VALUES };
    }

    public double getMin() {
        return min;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
        if (valueToValidate == null)
            return true;

        if (valueToValidate instanceof Number) {
            final double doubleValue = ((Number) valueToValidate).doubleValue();
            if (inclusive)
                return doubleValue >= min;
            return doubleValue > min;
        }

        final String stringValue = valueToValidate.toString();
        try {
            final double doubleValue = Double.parseDouble(stringValue);
            if (inclusive)
                return doubleValue >= min;
            return doubleValue > min;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public void setInclusive(final boolean inclusive) {
        this.inclusive = inclusive;
    }

    public void setMin(final double min) {
        this.min = min;
        requireMessageVariablesRecreation();
    }
}
