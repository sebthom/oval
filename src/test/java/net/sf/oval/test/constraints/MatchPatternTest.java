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

import java.util.regex.Pattern;

import org.junit.Test;

import net.sf.oval.constraint.MatchPatternCheck;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternTest extends AbstractContraintsTest {

   @Test
   public void testMatchPattern() {
      final MatchPatternCheck check = new MatchPatternCheck();
      super.testCheck(check);

      check.setMatchAll(true);
      check.setPattern("\\d*", 0);
      assertThat(check.isSatisfied(null, null, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "1234", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "12.34", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "12,34", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "foo", null, null)).isFalse();

      check.setPatterns(Pattern.compile("[1234]*", 0), Pattern.compile("[1256]*", 0));
      assertThat(check.isSatisfied(null, null, null, null)).isTrue();
      assertThat(check.isSatisfied(null, "", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "1212", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "1234", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "1256", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "34", null, null)).isFalse();
      assertThat(check.isSatisfied(null, "56", null, null)).isFalse();

      check.setMatchAll(false);
      assertThat(check.isSatisfied(null, "1212", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "1234", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "1256", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "34", null, null)).isTrue();
      assertThat(check.isSatisfied(null, "56", null, null)).isTrue();
   }
}
