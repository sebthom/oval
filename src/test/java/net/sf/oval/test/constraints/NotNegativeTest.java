/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.Test;

import net.sf.oval.constraint.NotNegativeCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotNegativeTest extends AbstractContraintsTest {

   @Test
   public void testNotNegative() {
      final NotNegativeCheck check = new NotNegativeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(null, 0, null)).isTrue();
      assertThat(check.isSatisfied(null, 0.0, null)).isTrue();
      assertThat(check.isSatisfied(null, 1, null)).isTrue();
      assertThat(check.isSatisfied(null, 1.0, null)).isTrue();
      assertThat(check.isSatisfied(null, "0", null)).isTrue();
      assertThat(check.isSatisfied(null, "0.0", null)).isTrue();
      assertThat(check.isSatisfied(null, "1", null)).isTrue();
      assertThat(check.isSatisfied(null, "1.0", null)).isTrue();
      assertThat(check.isSatisfied(null, "-1", null)).isFalse();
      assertThat(check.isSatisfied(null, "-1.0", null)).isFalse();
      assertThat(check.isSatisfied(null, false, null)).isFalse();
      assertThat(check.isSatisfied(null, true, null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal(-1), null)).isFalse();
      assertThat(check.isSatisfied(null, new BigDecimal(0), null)).isTrue();
      assertThat(check.isSatisfied(null, new BigDecimal(1), null)).isTrue();
   }
}
