/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class PastCheck extends AbstractAnnotationCheck<Past> {

   private static final long serialVersionUID = 1L;

   private long tolerance = 0;

   @Override
   public void configure(final Past constraintAnnotation) {
      super.configure(constraintAnnotation);
      setTolerance(constraintAnnotation.tolerance());
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public long getTolerance() {
      return tolerance;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      final long now = System.currentTimeMillis() + tolerance;

      // check if the value is a Date
      if (valueToValidate instanceof Date)
         return ((Date) valueToValidate).getTime() < now;

      // check if the value is a Calendar
      if (valueToValidate instanceof Calendar)
         return ((Calendar) valueToValidate).getTimeInMillis() < now;

      // check if the value is a LocalDate
      if (valueToValidate instanceof LocalDate)
         return ((LocalDate) valueToValidate).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < now;

      // check if the value is a LocalTime
      if (valueToValidate instanceof LocalTime)
         return ((LocalTime) valueToValidate).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < now;

      // check if the value is a LocalDateTime
      if (valueToValidate instanceof LocalDateTime)
         return ((LocalDateTime) valueToValidate).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < now;

      // check if the value is an OffsetTime
      if (valueToValidate instanceof OffsetTime)
         return ((OffsetTime) valueToValidate).atDate(LocalDate.now()).toInstant().toEpochMilli() < now;

      // check if the value is an OffsetDateTime
      if (valueToValidate instanceof OffsetDateTime)
         return ((OffsetDateTime) valueToValidate).toInstant().toEpochMilli() < now;

      // check if the value is an ZonedDateTime
      if (valueToValidate instanceof ZonedDateTime)
         return ((ZonedDateTime) valueToValidate).toInstant().toEpochMilli() < now;

      // see if we can extract a date based on the object's String representation
      final String stringValue = valueToValidate.toString();
      try {
         return DateFormat.getDateTimeInstance().parse(stringValue).getTime() < now;
      } catch (final ParseException ex) {
         return false;
      }
   }

   public void setTolerance(final long tolerance) {
      this.tolerance = tolerance;
   }
}
