/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertFalseCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertFalseTest extends AbstractContraintsTest {

   @Test
   public void testAssertFalse() {
      final AssertFalseCheck check = new AssertFalseCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(null, false, null)).isTrue();
      assertThat(check.isSatisfied(null, true, null)).isFalse();
      assertThat(check.isSatisfied(null, Boolean.FALSE, null)).isTrue();
      assertThat(check.isSatisfied(null, Boolean.TRUE, null)).isFalse();
      assertThat(check.isSatisfied(null, "true", null)).isFalse();
      assertThat(check.isSatisfied(null, "bla", null)).isTrue();
   }
}
