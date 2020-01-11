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

import net.sf.oval.constraint.MemberOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class MemberOfTest extends AbstractContraintsTest {
   public void testMemberOf() {
      final MemberOfCheck check = new MemberOfCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      check.setMembers("10", "false", "TRUE");
      check.setIgnoreCase(false);
      assertTrue(check.isSatisfied(null, 10, null, null));
      assertTrue(check.isSatisfied(null, "10", null, null));
      assertFalse(check.isSatisfied(null, 10.0, null, null));
      assertTrue(check.isSatisfied(null, "false", null, null));
      assertTrue(check.isSatisfied(null, false, null, null));
      assertTrue(check.isSatisfied(null, "TRUE", null, null));
      assertFalse(check.isSatisfied(null, true, null, null));

      check.setIgnoreCase(true);
      assertTrue(check.isSatisfied(null, "FALSE", null, null));
      assertTrue(check.isSatisfied(null, false, null, null));
      assertTrue(check.isSatisfied(null, "true", null, null));
      assertTrue(check.isSatisfied(null, true, null, null));
   }
}
