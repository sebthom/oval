/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.sf.oval.constraint.MemberOfEnum;
import net.sf.oval.constraint.MemberOfEnumCheck;

/**
 * @author shank3
 */
public class MemberOfEnumTest extends AbstractContraintsTest {

   static class TestDTO {

      @MemberOfEnum(CountryEnum.class)
      private String country;

      TestDTO(final String country) {
         this.country = country;
      }
   }

   enum CountryEnum {
      China, USA, Japan;
   }

   @Test
   public void testMemberOf() {
      final MemberOfEnumCheck check = new MemberOfEnumCheck();
      super.testCheck(check);

      check.setConstraintEnum(CountryEnum.class);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(null, "China", null)).isTrue();
      assertThat(check.isSatisfied(null, "non-exists", null)).isFalse();

      assertThat(check.getMessageVariables()).containsKeys("members");
   }
}
