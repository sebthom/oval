/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.MaxLengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxLengthTest extends AbstractContraintsTest {

   @Test
   public void testMaxLength() {
      final MaxLengthCheck check = new MaxLengthCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMax(5);
      assertThat(check.getMax()).isEqualTo(5);

      assertThat(check.isSatisfied(null, "1234", null)).isTrue();
      assertThat(check.isSatisfied(null, "12", null)).isTrue();
      assertThat(check.isSatisfied(null, "", null)).isTrue();
      assertThat(check.isSatisfied(null, "123456", null)).isFalse();
   }
}
