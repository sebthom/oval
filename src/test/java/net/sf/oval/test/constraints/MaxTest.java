/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.oval.constraint.MaxCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxTest extends AbstractContraintsTest {
   public void testMax() {
      final MaxCheck check = new MaxCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      check.setMax(100);
      assertEquals(100.0, check.getMax());

      assertTrue(check.isSatisfied(null, "50", null, null));
      assertTrue(check.isSatisfied(null, 50, null, null));
      assertTrue(check.isSatisfied(null, (byte) 50, null, null));
      assertTrue(check.isSatisfied(null, (short) 50, null, null));
      assertTrue(check.isSatisfied(null, (float) 50.0, null, null));
      assertTrue(check.isSatisfied(null, 50.0, null, null));
      assertTrue(check.isSatisfied(null, BigDecimal.valueOf(50), null, null));
      assertTrue(check.isSatisfied(null, BigDecimal.valueOf(50.0), null, null));
      assertTrue(check.isSatisfied(null, BigInteger.valueOf(50), null, null));

      assertTrue(check.isSatisfied(null, "100", null, null));
      assertTrue(check.isSatisfied(null, 100, null, null));
      assertTrue(check.isSatisfied(null, (byte) 100, null, null));
      assertTrue(check.isSatisfied(null, (short) 100, null, null));
      assertTrue(check.isSatisfied(null, (float) 100.0, null, null));
      assertTrue(check.isSatisfied(null, 100.0, null, null));
      assertTrue(check.isSatisfied(null, BigDecimal.valueOf(100), null, null));
      assertTrue(check.isSatisfied(null, BigDecimal.valueOf(100.0), null, null));
      assertTrue(check.isSatisfied(null, BigInteger.valueOf(100), null, null));

      assertFalse(check.isSatisfied(null, "110", null, null));
      assertFalse(check.isSatisfied(null, 110, null, null));
      assertFalse(check.isSatisfied(null, (byte) 110, null, null));
      assertFalse(check.isSatisfied(null, (short) 110, null, null));
      assertFalse(check.isSatisfied(null, (float) 110.0, null, null));
      assertFalse(check.isSatisfied(null, 110.0, null, null));
      assertFalse(check.isSatisfied(null, BigDecimal.valueOf(110), null, null));
      assertFalse(check.isSatisfied(null, BigDecimal.valueOf(110.0), null, null));
      assertFalse(check.isSatisfied(null, BigInteger.valueOf(110), null, null));

      assertFalse(check.isSatisfied(null, "", null, null));
      assertFalse(check.isSatisfied(null, "sdfQ", null, null));
   }
}
