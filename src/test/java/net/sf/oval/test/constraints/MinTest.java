/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import net.sf.oval.constraint.MinCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinTest extends AbstractContraintsTest {

   @Test
   public void testMax() {
      final MinCheck check = new MinCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMin(40);
      assertThat(check.getMin()).isEqualTo(40.0);

      assertThat(check.isSatisfied(null, "40", null)).isTrue();
      assertThat(check.isSatisfied(null, 40, null)).isTrue();
      assertThat(check.isSatisfied(null, (byte) 40, null)).isTrue();
      assertThat(check.isSatisfied(null, (short) 40, null)).isTrue();
      assertThat(check.isSatisfied(null, (float) 40.0, null)).isTrue();
      assertThat(check.isSatisfied(null, 40.0, null)).isTrue();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(40), null)).isTrue();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(40.0), null)).isTrue();
      assertThat(check.isSatisfied(null, BigInteger.valueOf(40), null)).isTrue();

      assertThat(check.isSatisfied(null, "50", null)).isTrue();
      assertThat(check.isSatisfied(null, 50, null)).isTrue();
      assertThat(check.isSatisfied(null, (byte) 50, null)).isTrue();
      assertThat(check.isSatisfied(null, (short) 50, null)).isTrue();
      assertThat(check.isSatisfied(null, (float) 50.0, null)).isTrue();
      assertThat(check.isSatisfied(null, 50.0, null)).isTrue();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(50), null)).isTrue();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(50.0), null)).isTrue();
      assertThat(check.isSatisfied(null, BigInteger.valueOf(50), null)).isTrue();

      assertThat(check.isSatisfied(null, "20", null)).isFalse();
      assertThat(check.isSatisfied(null, 20, null)).isFalse();
      assertThat(check.isSatisfied(null, (byte) 20, null)).isFalse();
      assertThat(check.isSatisfied(null, (short) 20, null)).isFalse();
      assertThat(check.isSatisfied(null, (float) 20.0, null)).isFalse();
      assertThat(check.isSatisfied(null, 20.0, null)).isFalse();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(20), null)).isFalse();
      assertThat(check.isSatisfied(null, BigDecimal.valueOf(20.0), null)).isFalse();
      assertThat(check.isSatisfied(null, BigInteger.valueOf(20), null)).isFalse();

      assertThat(check.isSatisfied(null, "", null)).isFalse();
      assertThat(check.isSatisfied(null, "sdfQ", null)).isFalse();
   }
}
