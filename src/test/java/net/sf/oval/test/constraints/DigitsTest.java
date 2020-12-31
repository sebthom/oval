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

import java.math.BigDecimal;

import org.junit.Test;

import net.sf.oval.constraint.DigitsCheck;

/**
 * @author Sebastian Thomschke
 */
public class DigitsTest extends AbstractContraintsTest {

   @Test
   public void testDigits() {
      final DigitsCheck check = new DigitsCheck();
      super.testCheck(check);
      check.setMaxFraction(2);
      check.setMaxInteger(2);

      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(null, 0, null)).isTrue();
      assertThat(check.isSatisfied(null, 0.0, null)).isTrue();
      assertThat(check.isSatisfied(null, 0.00, null)).isTrue();
      assertThat(check.isSatisfied(null, 0.000, null)).isTrue(); // returns true since it equals 0.0d
      assertThat(check.isSatisfied(null, 0.001, null)).isFalse();
      assertThat(check.isSatisfied(null, 000.0, null)).isTrue(); // returns true since it equals 0.0d
      assertThat(check.isSatisfied(null, 12, null)).isTrue();
      assertThat(check.isSatisfied(null, 123, null)).isFalse();
      assertThat(check.isSatisfied(null, 12.12, null)).isTrue();
      assertThat(check.isSatisfied(null, 12.123, null)).isFalse();
      assertThat(check.isSatisfied(null, 123.12, null)).isFalse();

      assertThat(check.isSatisfied(null, "0", null)).isTrue();
      assertThat(check.isSatisfied(null, "0.0", null)).isTrue();
      assertThat(check.isSatisfied(null, "0.00", null)).isTrue();
      assertThat(check.isSatisfied(null, "0.000", null)).isFalse(); // returns false since BigDecimal keeps the fraction
      assertThat(check.isSatisfied(null, "0.001", null)).isFalse();

      // TODO returns true since it is automatically converted into a BigDecimal("0.0") => should return false in case of pure strings?
      assertThat(check.isSatisfied(null, "000.0", null)).isTrue();

      assertThat(check.isSatisfied(null, "12", null)).isTrue();
      assertThat(check.isSatisfied(null, "123", null)).isFalse();
      assertThat(check.isSatisfied(null, "12.12", null)).isTrue();
      assertThat(check.isSatisfied(null, "12.123", null)).isFalse();
      assertThat(check.isSatisfied(null, "123.12", null)).isFalse();

      assertThat(check.isSatisfied(null, new BigDecimal("0"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("0.0"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("0.00"), null)).isTrue();

      // TODO returns false since BigDecimal keeps the fraction => should this return true for BigDecimals ?
      assertThat(check.isSatisfied(null, new BigDecimal("0.000"), null)).isFalse();

      assertThat(check.isSatisfied(null, new BigDecimal("0.001"), null)).isFalse();

      // returns true since it is automatically converted into a BigDecimal("0.0")
      assertThat(check.isSatisfied(null, new BigDecimal("000.0"), null)).isTrue();

      assertThat(check.isSatisfied(null, new BigDecimal("12"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("123"), null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal("12.12"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("12.123"), null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal("123.12"), null)).isFalse();

      check.setMaxInteger(13);
      check.setMaxFraction(13);
      assertThat(check.isSatisfied(null, 1_234_567_890_123L, null)).isTrue();
      assertThat(check.isSatisfied(null, 12_345_678_901_234L, null)).isFalse();
      assertThat(check.isSatisfied(null, "1234567890123", null)).isTrue();
      assertThat(check.isSatisfied(null, "12345678901234", null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal("1234567890123"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("12345678901234"), null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal("1234567890123.1234567890123"), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal("12345678901234.12345678901234"), null)).isFalse();
   }
}
