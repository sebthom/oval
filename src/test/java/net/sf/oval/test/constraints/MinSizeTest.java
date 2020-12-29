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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.sf.oval.constraint.MinSizeCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinSizeTest extends AbstractContraintsTest {

   @Test
   public void testMinSize() {
      final MinSizeCheck check = new MinSizeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null, null)).isTrue();

      check.setMin(2);
      assertThat(check.getMin()).isEqualTo(2);

      assertThat(check.isSatisfied(null, new Object[0], null, null)).isFalse();
      assertThat(check.isSatisfied(null, new Object[1], null, null)).isFalse();
      assertThat(check.isSatisfied(null, new Object[2], null, null)).isTrue();
      assertThat(check.isSatisfied(null, new Object[3], null, null)).isTrue();

      final List<Object> list = new ArrayList<>();
      assertThat(check.isSatisfied(null, list, null, null)).isFalse();
      list.add(1);
      assertThat(check.isSatisfied(null, list, null, null)).isFalse();
      list.add(2);
      assertThat(check.isSatisfied(null, list, null, null)).isTrue();
      list.add(3);
      assertThat(check.isSatisfied(null, list, null, null)).isTrue();

      final Set<Object> set = new HashSet<>();
      assertThat(check.isSatisfied(null, set, null, null)).isFalse();
      set.add(1);
      assertThat(check.isSatisfied(null, set, null, null)).isFalse();
      set.add(2);
      assertThat(check.isSatisfied(null, set, null, null)).isTrue();
      set.add(3);
      assertThat(check.isSatisfied(null, set, null, null)).isTrue();

      final Map<Object, Object> map = new HashMap<>();
      assertThat(check.isSatisfied(null, map, null, null)).isFalse();
      map.put(1, 1);
      assertThat(check.isSatisfied(null, map, null, null)).isFalse();
      map.put(2, 2);
      assertThat(check.isSatisfied(null, map, null, null)).isTrue();
      map.put(3, 3);
      assertThat(check.isSatisfied(null, map, null, null)).isTrue();

      assertThat(check.isSatisfied(null, "bla", null, null)).isFalse();
   }
}
