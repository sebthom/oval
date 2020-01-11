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

import net.sf.oval.constraint.NotEqualCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEqualTest extends AbstractContraintsTest {
   public void testNotEqual() {
      final NotEqualCheck check = new NotEqualCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      check.setTestString("TEST");
      check.setIgnoreCase(false);
      assertTrue(check.isSatisfied(null, 10, null, null));
      assertTrue(check.isSatisfied(null, "", null, null));
      assertTrue(check.isSatisfied(null, "test", null, null));
      assertFalse(check.isSatisfied(null, "TEST", null, null));

      check.setIgnoreCase(true);
      assertFalse(check.isSatisfied(null, "test", null, null));
      assertFalse(check.isSatisfied(null, "TEST", null, null));
   }
}
