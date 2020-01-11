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

import java.util.regex.Pattern;

import net.sf.oval.constraint.MatchPatternCheck;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternTest extends AbstractContraintsTest {
   public void testMatchPattern() {
      final MatchPatternCheck check = new MatchPatternCheck();
      super.testCheck(check);

      check.setMatchAll(true);
      check.setPattern("\\d*", 0);
      assertTrue(check.isSatisfied(null, null, null, null));
      assertTrue(check.isSatisfied(null, "", null, null));
      assertTrue(check.isSatisfied(null, "1234", null, null));
      assertFalse(check.isSatisfied(null, "12.34", null, null));
      assertFalse(check.isSatisfied(null, "12,34", null, null));
      assertFalse(check.isSatisfied(null, "foo", null, null));

      check.setPatterns(Pattern.compile("[1234]*", 0), Pattern.compile("[1256]*", 0));
      assertTrue(check.isSatisfied(null, null, null, null));
      assertTrue(check.isSatisfied(null, "", null, null));
      assertTrue(check.isSatisfied(null, "1212", null, null));
      assertFalse(check.isSatisfied(null, "1234", null, null));
      assertFalse(check.isSatisfied(null, "1256", null, null));
      assertFalse(check.isSatisfied(null, "34", null, null));
      assertFalse(check.isSatisfied(null, "56", null, null));

      check.setMatchAll(false);
      assertTrue(check.isSatisfied(null, "1212", null, null));
      assertTrue(check.isSatisfied(null, "1234", null, null));
      assertTrue(check.isSatisfied(null, "1256", null, null));
      assertTrue(check.isSatisfied(null, "34", null, null));
      assertTrue(check.isSatisfied(null, "56", null, null));
   }
}
