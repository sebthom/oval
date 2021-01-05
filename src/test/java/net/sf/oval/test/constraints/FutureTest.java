/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.text.DateFormat;
import java.util.Calendar;

import org.junit.Test;

import net.sf.oval.constraint.FutureCheck;

/**
 * @author Sebastian Thomschke
 */
public class FutureTest extends AbstractContraintsTest {

   @Test
   public void testFuture() {
      final FutureCheck isInFuture = new FutureCheck();
      super.testCheck(isInFuture);

      assertThat(isInFuture.isSatisfied(null, null, null)).isTrue();

      final Calendar cal = Calendar.getInstance();
      cal.roll(Calendar.YEAR, 1);
      assertThat(isInFuture.isSatisfied(null, cal, null)).isTrue();
      assertThat(isInFuture.isSatisfied(null, cal.getTime(), null)).isTrue();
      assertThat(isInFuture.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null)).isTrue();

      cal.roll(Calendar.YEAR, -2);
      assertThat(isInFuture.isSatisfied(null, cal, null)).isFalse();
      assertThat(isInFuture.isSatisfied(null, cal.getTime(), null)).isFalse();
      assertThat(isInFuture.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null)).isFalse();

      assertThat(isInFuture.isSatisfied(null, "bla", null)).isFalse();
   }

   @Test
   public void testTolerance() {
      final FutureCheck isInFuture = new FutureCheck();

      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MILLISECOND, -2000); // roll() does not work with milliseconds

      isInFuture.setTolerance(0);
      assertThat(isInFuture.isSatisfied(null, cal, null)).isFalse();

      isInFuture.setTolerance(1000);
      assertThat(isInFuture.isSatisfied(null, cal, null)).isFalse();

      isInFuture.setTolerance(5000);
      assertThat(isInFuture.isSatisfied(null, cal, null)).isTrue();
   }
}
