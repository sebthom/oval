/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.oval.constraint.DateRangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class DateRangeTest extends AbstractContraintsTest {

   public void testDateRange() {
      final DateRangeCheck check = new DateRangeCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      check.setMin("2000-03-03 09:09:09");
      assertFalse(check.isSatisfied(null, "2000-03-03 09:09:08", null, null));
      assertTrue(check.isSatisfied(null, "2000-03-03 09:09:09", null, null));
      assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));
      assertTrue(check.isSatisfied(null, "2900-03-03 09:09:09", null, null));

      check.setMax("2080-08-08 10:10:10");
      assertTrue(check.isSatisfied(null, "2080-08-08 10:10:09", null, null));
      assertTrue(check.isSatisfied(null, "2080-08-08 10:10:10", null, null));
      assertFalse(check.isSatisfied(null, "2080-08-08 10:10:11", null, null));

      final Calendar cal = Calendar.getInstance();
      assertTrue(check.isSatisfied(null, cal, null, null));
      assertTrue(check.isSatisfied(null, cal.getTime(), null, null));
      assertTrue(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

      cal.add(Calendar.YEAR, -100);
      assertFalse(check.isSatisfied(null, cal, null, null));
      assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
      assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

      cal.add(Calendar.YEAR, 200);
      assertFalse(check.isSatisfied(null, cal, null, null));
      assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
      assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

      assertFalse(check.isSatisfied(null, "bla", null, null));
   }

   public void testLiteralsWithMax() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();
      check.setMax("now");
      assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() + 4000), null, null));
      assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() - 4000), null, null));

      check.setMax("today");
      assertTrue(check.isSatisfied(null, now, null, null));
      assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));

      check.setMax("tomorrow");
      assertTrue(check.isSatisfied(null, now, null, null));
      assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));

      check.setMax("yesterday");
      assertFalse(check.isSatisfied(null, now, null, null));
   }

   public void testLiteralsWithMin() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();
      check.setMin("now");
      assertTrue(check.isSatisfied(null, now, null, null));

      check.setMin("today");
      assertTrue(check.isSatisfied(null, now, null, null));

      check.setMin("tomorrow");
      assertFalse(check.isSatisfied(null, now, null, null));

      check.setMin("yesterday");
      assertTrue(check.isSatisfied(null, now, null, null));
   }

   public void testToleranceWithMax() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setMax("now");
      check.setTolerance(4000);
      assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
      check.setTolerance(0);
      assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
   }

   public void testToleranceWithMin() {
      final DateRangeCheck check = new DateRangeCheck();
      check.setMin("now");
      check.setTolerance(4000);
      assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() - 2000), null, null));
      check.setTolerance(0);
      assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() - 2000), null, null));
   }
}
