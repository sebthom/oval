/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import net.sf.oval.constraint.RelativeDateRangeCheck;

/**
 * @author shank3
 */
public class RelativeDateRangeTest extends AbstractContraintsTest {

   @Test
   public void testDateRange() {
      final RelativeDateRangeCheck check = new RelativeDateRangeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      check.setMinus("P2D");
      assertThat(check.isSatisfied(null, "2000-03-03 09:09:08", null)).isFalse();
      assertThat(check.isSatisfied(null, new Date(), null)).isTrue();
      assertThat(check.isSatisfied(null, Calendar.getInstance(), null)).isTrue();
      assertThat(check.isSatisfied(null, LocalDateTime.now(), null)).isTrue();

      check.setPlus("P2D");
      assertThat(check.isSatisfied(null, new Date(), null)).isTrue();
      assertThat(check.isSatisfied(null, Calendar.getInstance(), null)).isTrue();
      assertThat(check.isSatisfied(null, LocalDateTime.now().plusDays(3), null)).isFalse();

      final Calendar cal = Calendar.getInstance();
      assertThat(check.isSatisfied(null, cal, null)).isTrue();
      assertThat(check.isSatisfied(null, cal.getTime(), null)).isTrue();
      String format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(cal.toInstant().atZone(ZoneId.systemDefault()));
      assertThat(check.isSatisfied(null, format, null)).isTrue();

      cal.add(Calendar.YEAR, -100);
      assertThat(check.isSatisfied(null, cal, null)).isFalse();
      assertThat(check.isSatisfied(null, cal.getTime(), null)).isFalse();
      format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(cal.toInstant().atZone(ZoneId.systemDefault()));
      assertThat(check.isSatisfied(null, format, null)).isFalse();

      cal.add(Calendar.YEAR, 200);
      assertThat(check.isSatisfied(null, cal, null)).isFalse();
      assertThat(check.isSatisfied(null, cal.getTime(), null)).isFalse();
      format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(cal.toInstant().atZone(ZoneId.systemDefault()));
      assertThat(check.isSatisfied(null, format, null)).isFalse();

      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
   }

   @Test
   public void testLiteralsWithMax() {
      final RelativeDateRangeCheck check = new RelativeDateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();
      check.setPlus("PT3S");
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() + 4000), null)).isFalse();
      assertThat(check.isSatisfied(null, new Date(System.currentTimeMillis() - 4000), null)).isTrue();

   }

   @Test
   public void testLiteralsWithMin() {
      final RelativeDateRangeCheck check = new RelativeDateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final Date now = new Date();
      check.setMinus("PT1S");
      assertThat(check.isSatisfied(null, now, null)).isTrue();
   }

   @Test
   public void testToleranceWithMax() {
      final RelativeDateRangeCheck check = new RelativeDateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final LocalDateTime target = LocalDateTime.now().plusMinutes(61);
      check.setPlus("PT1H");
      check.setTolerance(300);
      assertThat(check.isSatisfied(null, target, null)).isTrue();
   }

   @Test
   public void testToleranceWithMin() {
      final RelativeDateRangeCheck check = new RelativeDateRangeCheck();
      check.setFormat("yyyy-MM-dd HH:mm:ss");

      final LocalDateTime target = LocalDateTime.now().minusMinutes(61);
      check.setMinus("PT1H");
      check.setTolerance(300);
      assertThat(check.isSatisfied(null, target, null)).isTrue();
   }
}
