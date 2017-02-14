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

        // check value against java.time.* API
        try {
            Class.forName("java.time.ZoneId");
            Class.forName("java.time.LocalDate");
            Class.forName("java.time.LocalTime");
            Class.forName("java.time.LocalDateTime");
            Class.forName("java.time.OffsetTime");
            Class.forName("java.time.OffsetDateTime");
            Class.forName("java.time.ZonedDateTime");

            // check if the value is a LocalDate
            if (valueToValidate instanceof java.time.LocalDate)
                return ((java.time.LocalDate) valueToValidate).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() > now;

            // check if the value is a LocalTime
            if (valueToValidate instanceof java.time.LocalTime)
                return ((java.time.LocalTime) valueToValidate).atDate(java.time.LocalDate.now()).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() > now;

            // check if the value is a LocalDateTime
            if (valueToValidate instanceof java.time.LocalDateTime)
                return ((java.time.LocalDateTime) valueToValidate).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() > now;

            // check if the value is an OffsetTime
            if (valueToValidate instanceof java.time.OffsetTime)
                return ((java.time.OffsetTime) valueToValidate).atDate(java.time.LocalDate.now()).toInstant().toEpochMilli() > now;

            // check if the value is an OffsetDateTime
            if (valueToValidate instanceof java.time.OffsetDateTime)
                return ((java.time.OffsetDateTime) valueToValidate).toInstant().toEpochMilli() > now;

            // check if the value is an ZonedDateTime
            if (valueToValidate instanceof java.time.ZonedDateTime)
                return ((java.time.ZonedDateTime) valueToValidate).toInstant().toEpochMilli() > now;

        } catch (ClassNotFoundException e) {
            // continue checking
        }

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
