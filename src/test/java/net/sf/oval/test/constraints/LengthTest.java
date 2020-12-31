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

import net.sf.oval.constraint.LengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class LengthTest extends AbstractContraintsTest {

   @Test
   public void testLength() {
      final LengthCheck check = new LengthCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setMax(5);
      check.setMin(3);
      assertThat(check.getMax()).isEqualTo(5);
      assertThat(check.getMin()).isEqualTo(3);

      assertThat(check.isSatisfied(null, "1234", null)).isTrue();
      assertThat(check.isSatisfied(null, "12", null)).isFalse();
      assertThat(check.isSatisfied(null, "", null)).isFalse();
      assertThat(check.isSatisfied(null, "123456", null)).isFalse();
   }
}
