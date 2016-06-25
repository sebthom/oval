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
package net.sf.oval.constraint;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class FutureCheck extends AbstractAnnotationCheck<Future> {
    private static final long serialVersionUID = 1L;

    private long tolerance;

    @Override
    public void configure(final Future constraintAnnotation) {
        super.configure(constraintAnnotation);
        setTolerance(constraintAnnotation.tolerance());
    }

    @Override
    protected ConstraintTarget[] getAppliesToDefault() {
        return new ConstraintTarget[] { ConstraintTarget.VALUES };
    }

    public long getTolerance() {
        return tolerance;
    }

    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
        if (valueToValidate == null)
            return true;

        final long now = System.currentTimeMillis() - tolerance;

        // check if the value is a Date
        if (valueToValidate instanceof Date) // return ((Date) value).after(new Date());
            return ((Date) valueToValidate).getTime() > now;

        // check if the value is a Calendar
        if (valueToValidate instanceof Calendar)
            return ((Calendar) valueToValidate).getTime().getTime() > now;

        // see if we can extract a date based on the object's String representation
        final String stringValue = valueToValidate.toString();
        try {
            // return DateFormat.getDateTimeInstance().parse(stringValue).after(new Date());
            return DateFormat.getDateTimeInstance().parse(stringValue).getTime() > now;
        } catch (final ParseException ex) {
            return false;
        }
    }

    public void setTolerance(final long tolerance) {
        this.tolerance = tolerance;
    }
}
