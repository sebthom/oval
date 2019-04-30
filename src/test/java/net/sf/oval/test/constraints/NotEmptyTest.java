/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.NotEmptyCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEmptyTest extends AbstractContraintsTest {
   public void testNotEmpty() {
      final NotEmptyCheck check = new NotEmptyCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      assertTrue(check.isSatisfied(null, "bla", null, null));
      assertTrue(check.isSatisfied(null, true, null, null));
      assertTrue(check.isSatisfied(null, 1, null, null));
      assertFalse(check.isSatisfied(null, "", null, null));
      assertTrue(check.isSatisfied(null, ' ', null, null));
      assertTrue(check.isSatisfied(null, " ", null, null));
   }
}
