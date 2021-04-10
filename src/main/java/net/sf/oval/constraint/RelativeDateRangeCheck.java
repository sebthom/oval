/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author shank3
 */
public class RelativeDateRangeCheck extends AbstractAnnotationCheck<RelativeDateRange> {
   private static final Log LOG = Log.getLog(RelativeDateRangeCheck.class);

   private static final long serialVersionUID = 1L;

   private String format;
   private String plus;
   private String minus;

   private long tolerance = 0;

   @Override
   public void configure(final RelativeDateRange constraintAnnotation) {
      super.configure(constraintAnnotation);
      setPlus(constraintAnnotation.plus());
      setMinus(constraintAnnotation.minus());
      setFormat(constraintAnnotation.format());
      setTolerance(constraintAnnotation.tolerance());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(3);
      messageVariables.put("minus", minus.isEmpty() ? "-" : Duration.parse(minus).getSeconds() + "");
      messageVariables.put("plus", plus.isEmpty() ? "-" : Duration.parse(plus).getSeconds() + "");
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public String getFormat() {
      return format;
   }

   public String getMinus() {
      return minus;
   }

   public String getPlus() {
      return plus;
   }

   public long getTolerance() {
      return tolerance;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      if (StringUtils.isEmpty(plus) && StringUtils.isEmpty(minus)) {
         LOG.debug("No relative date range was configured.");
         return true;
      }

      final ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime target = null;

      // check if the value is a Date
      if (valueToValidate instanceof Date) {
         final Instant instant = ((Date) valueToValidate).toInstant();
         target = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
      } else if (valueToValidate instanceof Calendar) {
         final Instant instant = ((Calendar) valueToValidate).toInstant();
         target = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
      } else if (valueToValidate instanceof ZonedDateTime) {
         target = (ZonedDateTime) valueToValidate;
      } else if (valueToValidate instanceof LocalDateTime) {
         target = ZonedDateTime.of((LocalDateTime) valueToValidate, ZoneId.systemDefault());
      } else {
         // see if we can extract a date based on the object's String representation
         final String stringValue = valueToValidate.toString();
         try {
            if (format != null) {
               try {
                  final TemporalAccessor parse = DateTimeFormatter.ofPattern(format).parseBest(stringValue, ZonedDateTime::from, LocalDateTime::from);
                  if (parse instanceof ZonedDateTime) {
                     target = (ZonedDateTime) parse;
                  } else {
                     target = ZonedDateTime.of((LocalDateTime) parse, ZoneId.systemDefault());
                  }
               } catch (final DateTimeParseException ex) {
                  LOG.debug("valueToValidate not parsable with specified format {1}", format, ex);
               }
            }

            if (target == null) {
               target = ZonedDateTime.parse(stringValue);
            }
         } catch (final DateTimeParseException ex) {
            LOG.debug("valueToValidate is unparsable.", ex);
            return false;
         }
      }
      boolean inRange = true;
      if (!StringUtils.isEmpty(minus)) {
         final ZonedDateTime min = now.minus(Duration.parse(minus));
         inRange = min.isBefore(target) || Math.abs(min.toEpochSecond() - target.toEpochSecond()) <= tolerance;
      }
      if (inRange && !StringUtils.isEmpty(plus)) {
         final ZonedDateTime max = now.plus(Duration.parse(plus));
         inRange = max.isAfter(target) || Math.abs(max.toEpochSecond() - target.toEpochSecond()) <= tolerance;
      }
      return inRange;
   }

   public void setFormat(final String format) {
      this.format = format;
      requireMessageVariablesRecreation();
   }

   public void setMinus(final String minus) {
      this.minus = minus;
      requireMessageVariablesRecreation();
   }

   public void setPlus(final String plus) {
      this.plus = plus;
      requireMessageVariablesRecreation();
   }

   public void setTolerance(final long tolerance) {
      this.tolerance = tolerance;
   }
}
