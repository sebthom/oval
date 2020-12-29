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

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ApplyFieldConstraintsToConstructorsTest {

   @Guarded(applyFieldConstraintsToConstructors = true, checkInvariants = false)
   private static class Person {
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

      protected Person(final boolean isValid, final String firstName, final String lastName, final String zipCode) {
         this.isValid = isValid;
         this.firstName = firstName;
         this.lastName = lastName;
         this.zipCode = zipCode;
      }

      protected Person(final String theFirstName, final String theLastName, final String theZipCode) {
         firstName = theFirstName;
         lastName = theLastName;
         zipCode = theZipCode;
      }
   }

   /**
    * by default constraints specified for a field are also used for validating
    * method parameters of the corresponding setter methods
    */
   @Test
   @SuppressWarnings("unused")
   public void testConstrucorParameterValidation() {

      new Person(true, "", "", "12345");

      new Person(null, null, null);

      try {
         new Person(false, null, null, null);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()).hasSize(4);
      }
   }
}
