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

import net.sf.oval.constraint.MaxLengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxLengthTest extends AbstractContraintsTest {
   public void testMaxLength() {
      final MaxLengthCheck check = new MaxLengthCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      check.setMax(5);
      assertEquals(5, check.getMax());

      assertTrue(check.isSatisfied(null, "1234", null, null));
      assertTrue(check.isSatisfied(null, "12", null, null));
      assertTrue(check.isSatisfied(null, "", null, null));
      assertFalse(check.isSatisfied(null, "123456", null, null));
   }
}
