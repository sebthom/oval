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

import org.junit.Test;

import net.sf.oval.constraint.RangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class RangeTest extends AbstractContraintsTest {

   @Test
   public void testRange() {
      final RangeCheck check = new RangeCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMin(3);
      assertThat(check.getMin()).isEqualTo(3.0);

      assertThat(check.isSatisfied(null, "16", null)).isTrue();

      check.setMax(6);
      assertThat(check.getMax()).isEqualTo(6.0);

      assertThat(check.isSatisfied(null, "4", null)).isTrue();
      assertThat(check.isSatisfied(null, "16", null)).isFalse();
      assertThat(check.isSatisfied(null, "2", null)).isFalse();
   }
}
