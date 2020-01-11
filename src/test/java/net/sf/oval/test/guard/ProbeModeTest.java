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
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.ProbeModeListener;

/**
 * @author Sebastian Thomschke
 */
public class ProbeModeTest extends TestCase {

   @Guarded
   protected static class Person {
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

      public void setFirstName(@AssertFieldConstraints final String firstName) {
         this.firstName = firstName;
      }

      public void setLastName(@AssertFieldConstraints final String lastName) {
         this.lastName = lastName;
      }

      public void setZipCode(@AssertFieldConstraints final String zipCode) {
         this.zipCode = zipCode;
      }
   }

   public void testProbeModeWithIllegalValues() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Person p = new Person();
      guard.enableProbeMode(p);

      p.setFirstName(null);
      p.setLastName(null);
      p.setZipCode("abcde");
      final ProbeModeListener result = guard.disableProbeMode(p);

      assertEquals("", p.getFirstName());
      assertEquals("", p.getLastName());
      assertEquals("1", p.getZipCode());
      assertEquals(3, result.getConstraintsViolatedExceptions().size());
      assertEquals(3, result.getConstraintViolations().size());
      try {
         result.commit();
         fail();
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   public void testProbeModeWithValidValues() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Person p = new Person();
      guard.enableProbeMode(p);

      p.setFirstName("John");
      p.setLastName("Doe");
      p.setZipCode("12345");

      final ProbeModeListener result = guard.disableProbeMode(p);

      assertEquals("", p.getFirstName());
      assertEquals("", p.getLastName());
      assertEquals("1", p.getZipCode());
      assertEquals(0, result.getConstraintsViolatedExceptions().size());
      assertEquals(0, result.getConstraintViolations().size());

      result.commit();

      assertEquals("John", p.getFirstName());
      assertEquals("Doe", p.getLastName());
      assertEquals("12345", p.getZipCode());
   }
}
