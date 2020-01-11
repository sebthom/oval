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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.oval.constraint.MaxSizeCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxSizeTest extends AbstractContraintsTest {
   public void testMaxSize() {
      final MaxSizeCheck check = new MaxSizeCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      check.setMax(2);
      assertEquals(2, check.getMax());

      assertTrue(check.isSatisfied(null, new Object[0], null, null));
      assertTrue(check.isSatisfied(null, new Object[1], null, null));
      assertTrue(check.isSatisfied(null, new Object[2], null, null));
      assertFalse(check.isSatisfied(null, new Object[3], null, null));

      final List<Object> list = new ArrayList<>();
      assertTrue(check.isSatisfied(null, list, null, null));
      list.add(1);
      assertTrue(check.isSatisfied(null, list, null, null));
      list.add(2);
      assertTrue(check.isSatisfied(null, list, null, null));
      list.add(3);
      assertFalse(check.isSatisfied(null, list, null, null));

      final Set<Object> set = new HashSet<>();
      assertTrue(check.isSatisfied(null, set, null, null));
      set.add(1);
      assertTrue(check.isSatisfied(null, set, null, null));
      set.add(2);
      assertTrue(check.isSatisfied(null, set, null, null));
      set.add(3);
      assertFalse(check.isSatisfied(null, set, null, null));

      final Map<Object, Object> map = new HashMap<>();
      assertTrue(check.isSatisfied(null, map, null, null));
      map.put(1, 1);
      assertTrue(check.isSatisfied(null, map, null, null));
      map.put(2, 2);
      assertTrue(check.isSatisfied(null, map, null, null));
      map.put(3, 3);
      assertFalse(check.isSatisfied(null, map, null, null));

      assertFalse(check.isSatisfied(null, "bla", null, null));
   }
}
