/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.text.DateFormat;
import java.util.Calendar;

import org.junit.Test;

import net.sf.oval.constraint.PastCheck;

/**
 * @author Sebastian Thomschke
 */
public class PastTest extends AbstractContraintsTest {

   @Test
   public void testPast() {
      final PastCheck isInPast = new PastCheck();
      super.testCheck(isInPast);
      assertThat(isInPast.isSatisfied(null, null, null)).isTrue();

      final Calendar cal = Calendar.getInstance();
      cal.roll(Calendar.YEAR, -1);
      assertThat(isInPast.isSatisfied(null, cal, null)).isTrue();
      assertThat(isInPast.isSatisfied(null, cal.getTime(), null)).isTrue();
      assertThat(isInPast.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null)).isTrue();

      cal.roll(Calendar.YEAR, 2);
      assertThat(isInPast.isSatisfied(null, cal, null)).isFalse();
      assertThat(isInPast.isSatisfied(null, cal.getTime(), null)).isFalse();
      assertThat(isInPast.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null)).isFalse();

      assertThat(isInPast.isSatisfied(null, "bla", null)).isFalse();
   }

   @Test
   public void testTolerance() {
      final PastCheck isInPast = new PastCheck();

      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MILLISECOND, 2000); // roll() does not work with milliseconds

      isInPast.setTolerance(0);
      assertThat(isInPast.isSatisfied(null, cal, null)).isFalse();

      isInPast.setTolerance(1000);
      assertThat(isInPast.isSatisfied(null, cal, null)).isFalse();

      isInPast.setTolerance(5000);
      assertThat(isInPast.isSatisfied(null, cal, null)).isTrue();
   }
}
