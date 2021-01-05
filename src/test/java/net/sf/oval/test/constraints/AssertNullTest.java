/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertNullCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertNullTest extends AbstractContraintsTest {

   @Test
   public void testNotNull() {
      final AssertNullCheck check = new AssertNullCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
      assertThat(check.isSatisfied(null, true, null)).isFalse();
      assertThat(check.isSatisfied(null, 1, null)).isFalse();
      assertThat(check.isSatisfied(null, "", null)).isFalse();
      assertThat(check.isSatisfied(null, ' ', null)).isFalse();
      assertThat(check.isSatisfied(null, " ", null)).isFalse();
   }
}
