/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertTrueCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertTrueTest extends AbstractContraintsTest {

   @Test
   public void testAssertTrue() {
      final AssertTrueCheck check = new AssertTrueCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(null, true, null)).isTrue();
      assertThat(check.isSatisfied(null, false, null)).isFalse();
      assertThat(check.isSatisfied(null, Boolean.TRUE, null)).isTrue();
      assertThat(check.isSatisfied(null, Boolean.FALSE, null)).isFalse();
      assertThat(check.isSatisfied(null, "true", null)).isTrue();
      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
   }
}
