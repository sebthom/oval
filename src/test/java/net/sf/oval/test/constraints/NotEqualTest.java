/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.NotEqualCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEqualTest extends AbstractContraintsTest {

   @Test
   public void testNotEqual() {
      final NotEqualCheck check = new NotEqualCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setTestString("TEST");
      check.setIgnoreCase(false);
      assertThat(check.isSatisfied(null, 10, null)).isTrue();
      assertThat(check.isSatisfied(null, "", null)).isTrue();
      assertThat(check.isSatisfied(null, "test", null)).isTrue();
      assertThat(check.isSatisfied(null, "TEST", null)).isFalse();

      check.setIgnoreCase(true);
      assertThat(check.isSatisfied(null, "test", null)).isFalse();
      assertThat(check.isSatisfied(null, "TEST", null)).isFalse();
   }
}
