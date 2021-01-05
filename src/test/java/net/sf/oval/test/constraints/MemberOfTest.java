/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.MemberOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class MemberOfTest extends AbstractContraintsTest {

   @Test
   public void testMemberOf() {
      final MemberOfCheck check = new MemberOfCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMembers("10", "false", "TRUE");
      check.setIgnoreCase(false);
      assertThat(check.isSatisfied(null, 10, null)).isTrue();
      assertThat(check.isSatisfied(null, "10", null)).isTrue();
      assertThat(check.isSatisfied(null, 10.0, null)).isFalse();
      assertThat(check.isSatisfied(null, "false", null)).isTrue();
      assertThat(check.isSatisfied(null, false, null)).isTrue();
      assertThat(check.isSatisfied(null, "TRUE", null)).isTrue();
      assertThat(check.isSatisfied(null, true, null)).isFalse();

      check.setIgnoreCase(true);
      assertThat(check.isSatisfied(null, "FALSE", null)).isTrue();
      assertThat(check.isSatisfied(null, false, null)).isTrue();
      assertThat(check.isSatisfied(null, "true", null)).isTrue();
      assertThat(check.isSatisfied(null, true, null)).isTrue();
   }
}
