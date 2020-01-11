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

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.AssertFieldConstraintsCheck;
import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ApplyFieldConstraintsToParametersTest extends TestCase {
   @Guarded
   protected static class Person {
      @NotNull(message = "NOT_NULL")
      private String firstName = "";

      @AssertTrue(message = "ASSERT_TRUE")
      private boolean isValid = true;

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

      public void setDummyFirstName(@SuppressWarnings("unused") @AssertFieldConstraints("firstName") final String dummyFirstName) {
         // doing interesting stuff here
      }

      public void setFirstName(@AssertFieldConstraints final String firstName) {
         this.firstName = firstName;
      }

      public void setLastName(@AssertFieldConstraints final String lastName) {
         this.lastName = lastName;
      }

      public void setValid(@AssertFieldConstraints final boolean isValid) {
         this.isValid = isValid;
      }

      public void setZipCode(@AssertFieldConstraints final String zipCode) {
         this.zipCode = zipCode;
      }

      public void setZipCode2(final String zipCode) {
         this.zipCode = zipCode;
      }
   }

   @Guarded
   protected static class PersonService {
      public Person[] findPersonsByZipCode(@AssertFieldConstraints(declaringClass = Person.class) @SuppressWarnings("unused") final String zipCode) {
         return null;
      }
   }

   public void testFieldConstraintsFromDifferentClass() {
      final PersonService ps = new PersonService();

      try {
         ps.findPersonsByZipCode(null);
         fail("NOT_NULL ConstraintsViolatedException expected");
      } catch (final ConstraintsViolatedException ex) {
         assertEquals(1, ex.getConstraintViolations().length);
         assertEquals("NOT_NULL", ex.getMessage());
         assertEquals(MethodParameterContext.class, ex.getConstraintViolations()[0].getContext().getClass());
         assertEquals(FieldContext.class, ex.getConstraintViolations()[0].getCheckDeclaringContext().getClass());
      }

      try {
         ps.findPersonsByZipCode("foobar");
         fail("REG_EX ConstraintsViolatedException expected");
      } catch (final ConstraintsViolatedException ex) {
         assertEquals("REG_EX", ex.getMessage());
      }
   }

   /**
    * by default constraints specified for a field are also used for validating
    * method parameters of the corresponding setter methods
    */
   public void testSetterValidation() throws Exception {
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
      assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
      assertTrue(va.getConstraintViolations().size() == 1);
      assertTrue(va.getConstraintViolations().get(0).getMessage().equals("LENGTH"));
      va.clear();

      // test @NotEmpty
      p.setZipCode("");
      assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
      assertTrue(va.getConstraintViolations().size() == 1);
      assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_EMPTY"));
      va.clear();

      // test @RegEx
      p.setZipCode("dffd34");
      assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
      assertTrue(va.getConstraintViolations().size() == 1);
      assertTrue(va.getConstraintViolations().get(0).getMessage().equals("REG_EX"));
      va.clear();

      // test @AssertTrue
      p.setValid(false);
      assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
      assertTrue(va.getConstraintViolations().size() == 1);
      assertTrue(va.getConstraintViolations().get(0).getMessage().equals("ASSERT_TRUE"));
      va.clear();

      // test @FieldConstraint("fieldname")
      p.setDummyFirstName(null);
      assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
      assertTrue(va.getConstraintViolations().size() == 1);
      assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
      va.clear();

      // test dynamic introduction of FieldConstraintsCheck
      {
         p.setZipCode2("dffd34");
         assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
      }
      {
         final Method setter = p.getClass().getMethod("setZipCode2", new Class<?>[] {String.class});
         final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
         guard.addChecks(setter, 0, check);
         p.setZipCode2("dffd34");
         assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
         assertTrue(va.getConstraintViolations().size() == 1);
         assertTrue(va.getConstraintViolations().get(0).getMessage().equals("REG_EX"));
         va.clear();
         guard.removeChecks(setter, 0, check);
      }
      {
         final Method setter = p.getClass().getMethod("setZipCode2", new Class<?>[] {String.class});
         final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
         check.setFieldName("firstName");
         guard.addChecks(setter, 0, check);
         p.setZipCode2("dffd34");
         assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
         p.setZipCode2(null);
         assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
         assertTrue(va.getConstraintViolations().size() == 1);
         assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
         va.clear();
         guard.removeChecks(setter, 0, check);
      }
      {
         p.setZipCode2("dffd34");
         assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
      }
   }
}
