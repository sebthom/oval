/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.MinLengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinLengthTest extends AbstractContraintsTest {

   @Test
   public void testMinLength() {
      final MinLengthCheck check = new MinLengthCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMin(3);
      assertThat(check.getMin()).isEqualTo(3);

      assertThat(check.isSatisfied(null, "1234", null)).isTrue();
      assertThat(check.isSatisfied(null, "12", null)).isFalse();
      assertThat(check.isSatisfied(null, "", null)).isFalse();
   }
}
