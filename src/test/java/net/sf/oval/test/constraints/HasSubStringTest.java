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
      assertThat(check.isSatisfied(null, null, null, null)).isTrue();

      check.setSubstring("TeSt");
      assertThat(check.getSubstring()).isEqualTo("TeSt");

      check.setIgnoreCase(false);
      assertThat(check.isIgnoreCase()).isFalse();

      assertThat(check.isSatisfied(null, "bla", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "test", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "TeSt", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTeStaaaa", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "TeStaaaa", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTeSt", null, null)).isTrue();

      check.setIgnoreCase(true);
      assertThat(check.isIgnoreCase()).isTrue();

      assertThat(check.isSatisfied(null, "bla", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "test", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "TEst", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "aaaTesTaaaa", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "TEstaaaa", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "aaatESt", null, null)).isTrue();
   }
}
