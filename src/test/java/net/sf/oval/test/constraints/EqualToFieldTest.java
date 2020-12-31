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

import net.sf.oval.constraint.EqualToFieldCheck;

/**
 * @author Sebastian Thomschke
 */
public class EqualToFieldTest extends AbstractContraintsTest {
   public static class EnrichedEntity extends Entity {
      @SuppressWarnings("hiding")
      protected String password1;

      protected String password1Repeated;

      protected String password2Repeated;
   }

   public static class Entity {
      protected String password1 = "mug";
      protected String password2DifferentName;

      public String getPassword2() {
         return password2DifferentName;
      }

   }

   @Test
   public void testEqualToField() {
      final EqualToFieldCheck check = new EqualToFieldCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      final EnrichedEntity entity = new EnrichedEntity();
      entity.password1 = "secret";
      entity.password1Repeated = "zecret";

      check.setFieldName("password1");
      check.setUseGetter(false);

      assertThat(check.isSatisfied(entity, entity.password1Repeated, null)).isFalse();
      entity.password1Repeated = "secret";
      assertThat(check.isSatisfied(entity, entity.password1Repeated, null)).isTrue();

      entity.password2DifferentName = "secret";
      entity.password2Repeated = "zecret";

      check.setFieldName("password2");
      check.setUseGetter(true);

      assertThat(check.isSatisfied(entity, entity.password2Repeated, null)).isFalse();
      entity.password2Repeated = "secret";
      assertThat(check.isSatisfied(entity, entity.password2Repeated, null)).isTrue();

   }
}
