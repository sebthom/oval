/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.sf.oval.constraint.SizeCheck;

/**
 * @author Sebastian Thomschke
 */
public class SizeTest extends AbstractContraintsTest {

   @Test
   public void testSize() {
      final SizeCheck check = new SizeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMin(2);
      check.setMax(3);
      assertThat(check.getMin()).isEqualTo(2);
      assertThat(check.getMax()).isEqualTo(3);

      assertThat(check.isSatisfied(null, new Object[0], null)).isFalse();
      assertThat(check.isSatisfied(null, new Object[1], null)).isFalse();
      assertThat(check.isSatisfied(null, new Object[2], null)).isTrue();
      assertThat(check.isSatisfied(null, new Object[3], null)).isTrue();
      assertThat(check.isSatisfied(null, new Object[4], null)).isFalse();

      final List<Object> list = new ArrayList<>();
      assertThat(check.isSatisfied(null, list, null)).isFalse();
      list.add(1);
      assertThat(check.isSatisfied(null, list, null)).isFalse();
      list.add(2);
      assertThat(check.isSatisfied(null, list, null)).isTrue();
      list.add(3);
      assertThat(check.isSatisfied(null, list, null)).isTrue();
      list.add(4);
      assertThat(check.isSatisfied(null, list, null)).isFalse();

      final Set<Object> set = new HashSet<>();
      assertThat(check.isSatisfied(null, set, null)).isFalse();
      set.add(1);
      assertThat(check.isSatisfied(null, set, null)).isFalse();
      set.add(2);
      assertThat(check.isSatisfied(null, set, null)).isTrue();
      set.add(3);
      assertThat(check.isSatisfied(null, set, null)).isTrue();
      set.add(4);
      assertThat(check.isSatisfied(null, set, null)).isFalse();

      final Map<Object, Object> map = new HashMap<>();
      assertThat(check.isSatisfied(null, map, null)).isFalse();
      map.put(1, 1);
      assertThat(check.isSatisfied(null, map, null)).isFalse();
      map.put(2, 2);
      assertThat(check.isSatisfied(null, map, null)).isTrue();
      map.put(3, 3);
      assertThat(check.isSatisfied(null, map, null)).isTrue();
      map.put(4, 4);
      assertThat(check.isSatisfied(null, map, null)).isFalse();

      assertThat(check.isSatisfied(null, "bla", null)).isTrue();
      assertThat(check.isSatisfied(null, "blabla", null)).isFalse();
   }
}
