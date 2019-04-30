/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConditionalConstraintsTest extends TestCase {
   protected static class TestEntity {
      @NotNull(when = "groovy:_this.lastname!=null", message = "NOT_NULL")
      public String firstname;

      public String lastname;
   }

   public void testConstraintViolationOrder() {
      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      List<ConstraintViolation> violations = v.validate(e);
      assertEquals(0, violations.size());
      e.lastname = "foo";
      violations = v.validate(e);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());
   }
}
