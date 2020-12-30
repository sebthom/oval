/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import net.sf.oval.constraint.DateRangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class DateRangeTest extends AbstractContraintsTest {

   @Test
   public void testDateRange() {
      final DateRangeCheck check = new DateRangeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null, null)).isTrue();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      check.setMin("2000-03-03 09:09:09");
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:08", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:09", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:10", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2900-03-03 09:09:09", null, null)).isTrue();

      check.setMax("2080-08-08 10:10:10");
      assertThat(check.isSatisfied(null, "2080-08-08 10:10:09", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2080-08-08 10:10:10", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2080-08-08 10:10:11", null, null)).isFalse();

      final Calendar cal = Calendar.getInstance();
      assertThat(check.isSatisfied(null, cal, null, null)).isTrue();
      assertThat(check.isSatisfied(null, cal.getTime(), null, null)).isTrue();
      assertThat(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null)).isTrue();

      cal.add(Calendar.YEAR, -100);
      assertThat(check.isSatisfied(null, cal, null, null)).isFalse();
      assertThat(check.isSatisfied(null, cal.getTime(), null, null)).isFalse();
      assertThat(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null)).isFalse();

      cal.add(Calendar.YEAR, 200);
      assertThat(check.isSatisfied(null, cal, null, null)).isFalse();
      assertThat(check.isSatisfied(null, cal.getTime(), null, null)).isFalse();
      assertThat(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null)).isFalse();

      assertThat(check.isSatisfied(null, "bla", null, null)).isFalse();
   }

   @Test
   public void testLiteralsWithMax() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();
      check.setMax("now");
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() + 4000), null, null)).isFalse();
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() - 4000), null, null)).isTrue();

      check.setMax("today");
      assertThat(check.isSatisfied(null, now, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:10", null, null)).isTrue();

      check.setMax("tomorrow");
      assertThat(check.isSatisfied(null, now, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:10", null, null)).isTrue();

      check.setMax("yesterday");
      assertThat(check.isSatisfied(null, now, null, null)).isFalse();
   }

   @Test
   public void testLiteralsWithMin() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();

      check.setMin("now");
      assertThat(check.isSatisfied(null, now, null, null)).isTrue();

      check.setMin("today");
      assertThat(check.isSatisfied(null, now, null, null)).isTrue();

      check.setMin("tomorrow");
      assertThat(check.isSatisfied(null, now, null, null)).isFalse();

      check.setMin("yesterday");
      assertThat(check.isSatisfied(null, now, null, null)).isTrue();
   }

   @Test
   public void testToleranceWithMax() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setMax("now");
      check.setTolerance(4000);
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null)).isTrue();
      check.setTolerance(0);
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null)).isFalse();
   }

   @Test
   public void testToleranceWithMin() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setMin("now");
      check.setTolerance(4000);
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() - 2000), null, null)).isTrue();
      check.setTolerance(0);
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() - 2000), null, null)).isFalse();
   }
}
