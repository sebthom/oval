/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class DateRangeCheck extends AbstractAnnotationCheck<DateRange> {
   private static final Log LOG = Log.getLog(DateRangeCheck.class);

   private static final long serialVersionUID = 1L;

   private String format;
   private String max;
   private String min;
   private String plusNow;
   private String minusNow;

   private transient Long maxMillis;
   private transient Long minMillis;
   private long tolerance = 0;

   @Override
   public void configure(final DateRange constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMin(constraintAnnotation.min());
      setMax(constraintAnnotation.max());
      setFormat(constraintAnnotation.format());
      setTolerance(constraintAnnotation.tolerance());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(3);
      messageVariables.put("min", min == null ? ".." : min);
      messageVariables.put("max", max == null ? ".." : max);
      messageVariables.put("format", format);
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public String getFormat() {
      return format;
   }

   public String getMax() {
      return max;
   }

   private long getMaxMillis() throws InvalidConfigurationException {
      if (maxMillis == null) {
         if (max != null && max.length() > 0) {
            switch (max) {
               case "now":
                  return System.currentTimeMillis() + tolerance;
               case "today": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  cal.add(Calendar.MILLISECOND, -1);
                  return cal.getTimeInMillis() + tolerance;
               }
               case "tomorrow": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  cal.add(Calendar.DAY_OF_YEAR, 2);
                  cal.add(Calendar.MILLISECOND, -1);
                  return cal.getTimeInMillis() + tolerance;
               }
               case "yesterday": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  cal.add(Calendar.MILLISECOND, -1);
                  return cal.getTimeInMillis() + tolerance;
               }
               default:
                  if (format != null && format.length() > 0) {
                     final SimpleDateFormat sdf = new SimpleDateFormat(format);
                     try {
                        maxMillis = sdf.parse(max).getTime() + tolerance;
                     } catch (final ParseException e) {
                        throw new InvalidConfigurationException("Unable to parse the max Date String", e);
                     }
                  } else {
                     try {
                        maxMillis = DateFormat.getDateTimeInstance().parse(max).getTime() + tolerance;
                     } catch (final ParseException e) {
                        throw new InvalidConfigurationException("Unable to parse the max Date String", e);
                     }
                  }
            }
         }
         if (Objects.isNull(maxMillis) && Objects.nonNull(plusNow) && plusNow.length() > 0) {
            final Duration duration;
            try {
               duration = Duration.parse(plusNow);
            } catch (final DateTimeParseException e) {
               throw new InvalidConfigurationException("Unable to parse the plusNow String", e);
            }
            return Instant.now().plus(duration).toEpochMilli() + tolerance;
         }
      }
      return Objects.nonNull(maxMillis) ? maxMillis : Long.MAX_VALUE;
   }

   public String getMin() {
      return min;
   }

   private long getMinMillis() throws InvalidConfigurationException {
      if (minMillis == null) {
         if (min != null && min.length() > 0) {
            switch (min) {
               case "now":
                  return System.currentTimeMillis() - tolerance;
               case "today": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  return cal.getTimeInMillis() - tolerance;
               }
               case "tomorrow": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  return cal.getTimeInMillis() - tolerance;
               }
               case "yesterday": {
                  final Calendar cal = Calendar.getInstance();
                  cal.set(Calendar.HOUR_OF_DAY, 0);
                  cal.set(Calendar.MINUTE, 0);
                  cal.set(Calendar.SECOND, 0);
                  cal.set(Calendar.MILLISECOND, 0);
                  cal.add(Calendar.DAY_OF_YEAR, -1);
                  return cal.getTimeInMillis() - tolerance;
               }
               default:
                  if (format != null && format.length() > 0) {
                     final SimpleDateFormat sdf = new SimpleDateFormat(format);
                     try {
                        minMillis = sdf.parse(min).getTime() - tolerance;
                     } catch (final ParseException e) {
                        throw new InvalidConfigurationException("Unable to parse the min Date String", e);
                     }
                  } else {
                     try {
                        minMillis = DateFormat.getDateTimeInstance().parse(min).getTime() - tolerance;
                     } catch (final ParseException e) {
                        throw new InvalidConfigurationException("Unable to parse the min Date String", e);
                     }
                  }
            }
         }
         if (Objects.isNull(minMillis) && Objects.nonNull(minusNow) && minusNow.length() > 0) {
            final Duration duration;
            try {
               duration = Duration.parse(minusNow);
            } catch (final DateTimeParseException e) {
               throw new InvalidConfigurationException("Unable to parse the minusNow String", e);
            }
            return Instant.now().minus(duration).toEpochMilli() - tolerance;
         }
      }
      return Objects.nonNull(minMillis) ? minMillis : 0L;
   }

   public long getTolerance() {
      return tolerance;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      long valueInMillis = -1;

      // check if the value is a Date
      if (valueToValidate instanceof Date) {
         valueInMillis = ((Date) valueToValidate).getTime();
      } else if (valueToValidate instanceof Calendar) {
         valueInMillis = ((Calendar) valueToValidate).getTimeInMillis();
      } else {
         // see if we can extract a date based on the object's String representation
         final String stringValue = valueToValidate.toString();
         try {
            if (format != null) {
               try {
                  valueInMillis = new SimpleDateFormat(format).parse(stringValue).getTime();
               } catch (final ParseException ex) {
                  LOG.debug("valueToValidate not parsable with specified format {1}", format, ex);
               }
            }

            if (valueInMillis == -1) {
               valueInMillis = DateFormat.getDateTimeInstance().parse(stringValue).getTime();
            }
         } catch (final ParseException ex) {
            LOG.debug("valueToValidate is unparsable.", ex);
            return false;
         }
      }

      return valueInMillis >= getMinMillis() && valueInMillis <= getMaxMillis();
   }

   public void setFormat(final String format) {
      this.format = format;
      requireMessageVariablesRecreation();
   }

   public void setMax(final String max) {
      this.max = max;
      maxMillis = null;
      requireMessageVariablesRecreation();
   }

   public void setMin(final String min) {
      this.min = min;
      minMillis = null;
      requireMessageVariablesRecreation();
   }

   public void setTolerance(final long tolerance) {
      this.tolerance = tolerance;
      minMillis = null;
      maxMillis = null;
   }

   public String getPlusNow() {
      return plusNow;
   }

   public void setPlusNow(final String plusNow) {
      this.plusNow = plusNow;
      maxMillis = null;
      requireMessageVariablesRecreation();
   }

   public String getMinusNow() {
      return minusNow;
   }

   public void setMinusNow(final String minusNow) {
      this.minusNow = minusNow;
      minMillis = null;
      requireMessageVariablesRecreation();
   }
}
