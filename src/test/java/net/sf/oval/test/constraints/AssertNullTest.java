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

import net.sf.oval.constraint.AssertNullCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertNullTest extends AbstractContraintsTest {
   public void testNotNull() {
      final AssertNullCheck check = new AssertNullCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));
      assertFalse(check.isSatisfied(null, "bla", null, null));
      assertFalse(check.isSatisfied(null, true, null, null));
      assertFalse(check.isSatisfied(null, 1, null, null));
      assertFalse(check.isSatisfied(null, "", null, null));
      assertFalse(check.isSatisfied(null, ' ', null, null));
      assertFalse(check.isSatisfied(null, " ", null, null));
   }
}
