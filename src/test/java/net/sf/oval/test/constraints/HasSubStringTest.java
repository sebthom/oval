/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.HasSubstringCheck;

/**
 * @author Sebastian Thomschke
 */
public class HasSubStringTest extends AbstractContraintsTest {

   @Test
   public void testHasSubString() {
      final HasSubstringCheck check = new HasSubstringCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setSubstring("TeSt");
      assertThat(check.getSubstring()).isEqualTo("TeSt");

      check.setIgnoreCase(false);
      assertThat(check.isIgnoreCase()).isFalse();

      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
      assertThat(check.isSatisfied(null, "test", null)).isFalse();
      assertThat(check.isSatisfied(null, "TeSt", null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTeStaaaa", null)).isTrue();
      assertThat(check.isSatisfied(null, "TeStaaaa", null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTeSt", null)).isTrue();

      check.setIgnoreCase(true);
      assertThat(check.isIgnoreCase()).isTrue();

      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
      assertThat(check.isSatisfied(null, "test", null)).isTrue();
      assertThat(check.isSatisfied(null, "TEst", null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTesTaaaa", null)).isTrue();
      assertThat(check.isSatisfied(null, "TEstaaaa", null)).isTrue();
      assertThat(check.isSatisfied(null, "aaatESt", null)).isTrue();
   }
}
