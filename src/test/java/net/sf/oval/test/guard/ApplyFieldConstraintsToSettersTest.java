/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ApplyFieldConstraintsToSettersTest extends TestCase {
   @Guarded(applyFieldConstraintsToSetters = true)
   protected static class Person {
      @AssertTrue(message = "ASSERT_TRUE")
      private boolean isValid = true;

      @NotNull(message = "NOT_NULL")
      private String firstName = "";

      @NotNull(message = "NOT_NULL")
      private String lastName = "";

      @NotNull(message = "NOT_NULL")
      @Length(max = 6, message = "LENGTH")
      @NotEmpty(message = "NOT_EMPTY")
      @MatchPattern(pattern = "^[0-9]*$", message = "REG_EX")
      private String zipCode = "1";

      public String getFirstName() {
         return firstName;
      }

      public String getLastName() {
         return lastName;
      }

      public String getZipCode() {
         return zipCode;
      }

      public boolean isValid() {
         return isValid;
      }

      public void setFirstName(final String firstName) {
         this.firstName = firstName;
      }

      public void setLastName(final String lastName) {
         this.lastName = lastName;
      }

      public void setValid(final boolean isValid) {
         this.isValid = isValid;
      }

      public void setZipCode(final String zipCode) {
         this.zipCode = zipCode;
      }
   }

   /**
    * by default constraints specified for a field are also used for validating
    * method parameters of the corresponding setter methods
    */
   public void testSetterValidation() {
      final Person p = new Person();

      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      guard.enableProbeMode(p);

      final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
      guard.addListener(va, p);

      // test @Length(max=)
      p.setFirstName("Mike");
      p.setLastName("Mahoney");
      p.setZipCode("1234567");
      assertEquals(1, va.getConstraintsViolatedExceptions().size());
      assertEquals(1, va.getConstraintViolations().size());
      assertEquals("LENGTH", va.getConstraintViolations().get(0).getMessage());
      va.clear();

      // test @NotEmpty
      p.setZipCode("");
      assertEquals(1, va.getConstraintsViolatedExceptions().size());
      assertEquals(1, va.getConstraintViolations().size());
      assertEquals("NOT_EMPTY", va.getConstraintViolations().get(0).getMessage());
      va.clear();

      // test @RegEx
      p.setZipCode("dffd34");
      assertEquals(1, va.getConstraintsViolatedExceptions().size());
      assertEquals(1, va.getConstraintViolations().size());
      assertEquals("REG_EX", va.getConstraintViolations().get(0).getMessage());
      va.clear();

      // test @AssertTrue
      p.setValid(false);
      assertEquals(1, va.getConstraintsViolatedExceptions().size());
      assertEquals(1, va.getConstraintViolations().size());
      assertEquals("ASSERT_TRUE", va.getConstraintViolations().get(0).getMessage());
      va.clear();
   }
}
