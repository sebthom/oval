/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConditionalConstraintsTest {

   protected static class TestEntity {
      @NotNull(when = "groovy:_this.lastname!=null", message = "NOT_NULL")
      public String firstname;

      public String lastname;
   }

   @Test
   public void testConstraintViolationOrder() {
      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      List<ConstraintViolation> violations = v.validate(e);
      assertThat(violations).isEmpty();
      e.lastname = "foo";
      violations = v.validate(e);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");
   }
}
