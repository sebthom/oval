/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.Test;

import net.sf.oval.constraint.NotMatchPatternCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotMatchPatternTest extends AbstractContraintsTest {

   @Test
   public void testMatchPattern() {
      final NotMatchPatternCheck check = new NotMatchPatternCheck();
      super.testCheck(check);

      check.setPattern("\\d+", 0);
      assertThat(check.isSatisfied(null, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "", null)).isTrue();
      assertThat(check.isSatisfied(null, "1234", null)).isFalse();
      assertThat(check.isSatisfied(null, "12.34", null)).isTrue();
      assertThat(check.isSatisfied(null, "12,34", null)).isTrue();
      assertThat(check.isSatisfied(null, "foo", null)).isTrue();

      check.setPatterns(Pattern.compile("[123]+", 0), Pattern.compile("[abc]+", 0));
      assertThat(check.isSatisfied(null, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "", null)).isTrue();
      assertThat(check.isSatisfied(null, "12", null)).isFalse();
      assertThat(check.isSatisfied(null, "abc", null)).isFalse();
      assertThat(check.isSatisfied(null, "45", null)).isTrue();
      assertThat(check.isSatisfied(null, "de", null)).isTrue();
   }
}
