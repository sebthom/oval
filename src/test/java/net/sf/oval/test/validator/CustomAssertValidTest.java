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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.AnnotationCheck;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public class CustomAssertValidTest extends TestCase {
   protected static class Address {
      @NotNull
      public String street;

      @NotNull
      public String city;

      @NotNull
      @Length(max = 6)
      @NotEmpty
      @MatchPattern(pattern = "^[0-9]*$")
      public String zipCode;

      @CustomAssertValid(message = "ASSERT_VALID")
      public Person contact;
   }

   protected static class CustomAnnotationConfigurer extends AnnotationsConfigurer {
      @SuppressWarnings("unchecked")
      @Override
      protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(final ConstraintAnnotation constraintAnnotation)
         throws ReflectionException {
         if (constraintAnnotation instanceof CustomAssertValid) {
            final CustomAssertValid customAssertValid = (CustomAssertValid) constraintAnnotation;

            // instantiate a AssertValidCheck based on the custom constraint annotation
            final AssertValidCheck assertValidCheck = new AssertValidCheck();
            assertValidCheck.setErrorCode(customAssertValid.errorCode());
            assertValidCheck.setMessage(customAssertValid.message());
            assertValidCheck.setProfiles(customAssertValid.profiles());
            assertValidCheck.setAppliesTo(customAssertValid.appliesTo());
            assertValidCheck.setSeverity(customAssertValid.severity());
            return (AnnotationCheck<ConstraintAnnotation>) assertValidCheck;
         }

         return super.initializeCheck(constraintAnnotation);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
   @Constraint(checkWith = CustomAssertValidCheck.class)
   public static @interface CustomAssertValid {
      ConstraintTarget[] appliesTo() default {ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE};

      String errorCode() default "CustomAssertValid";

      String message() default "CustomAssertValid.violated";

      String[] profiles() default {};

      int severity() default 0;
   }

   public static class CustomAssertValidCheck extends AbstractAnnotationCheck<CustomAssertValid> {
      private static final long serialVersionUID = 1L;

      private final AssertValidCheck assertValidCheck = new AssertValidCheck();

      @Override
      public void configure(final CustomAssertValid constraintAnnotation) {
         assertValidCheck.setErrorCode(constraintAnnotation.errorCode());
         assertValidCheck.setMessage(constraintAnnotation.message());
         assertValidCheck.setProfiles(constraintAnnotation.profiles());
         assertValidCheck.setAppliesTo(constraintAnnotation.appliesTo());
         assertValidCheck.setSeverity(constraintAnnotation.severity());
      }

      @Override
      public boolean isSatisfied(final Object validatedObject, final Object value, final OValContext context, final Validator validator) {
         return true;
      }
   }

   protected static class Person {
      @NotNull
      public String firstName;

      @NotNull
      public String lastName;

      @CustomAssertValid(message = "ASSERT_VALID")
      public Address homeAddress;

      @CustomAssertValid(message = "ASSERT_VALID")
      public List<Address> otherAddresses1;

      @CustomAssertValid(message = "ASSERT_VALID", appliesTo = ConstraintTarget.CONTAINER)
      public Set<Address> otherAddresses2;

      @CustomAssertValid(message = "ASSERT_VALID", appliesTo = {ConstraintTarget.VALUES, ConstraintTarget.CONTAINER})
      public Set<Address> otherAddresses3;

   }

   protected static class Registry {
      @CustomAssertValid
      public List<Address[]> addressClusters;

      @CustomAssertValid
      public Map<String, List<Person>> personsByCity;

      @CustomAssertValid
      public Map<String, Map<String, Address[]>> addressesByCityAndStreet;
   }

   public void testCollectionValues() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Person p = new Person();
      p.firstName = "John";
      p.lastName = "Doe";
      p.otherAddresses1 = new ArrayList<Address>();
      p.otherAddresses2 = new HashSet<Address>();
      p.otherAddresses3 = new HashSet<Address>();

      final Address a = new Address();
      a.street = "The Street";
      a.city = "The City";
      a.zipCode = null;
      assertEquals(1, validator.validate(a).size());

      p.otherAddresses1.add(a);
      assertEquals(1, validator.validate(p).size());

      p.otherAddresses1.remove(a);
      p.otherAddresses2.add(a);
      assertEquals(0, validator.validate(p).size());

      p.otherAddresses3.add(a);
      assertEquals(1, validator.validate(p).size());
   }

   public void testRecursion() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Registry registry = new Registry();

      // nulled collections and maps are valid
      assertTrue(validator.validate(registry).size() == 0);

      registry.addressesByCityAndStreet = new HashMap<String, Map<String, Address[]>>();
      registry.addressClusters = new ArrayList<Address[]>();
      registry.personsByCity = new HashMap<String, List<Person>>();

      // empty collections and maps are valid
      assertEquals(0, validator.validate(registry).size());

      final Person invalidPerson1 = new Person();
      final Person invalidPerson2 = new Person();

      // map with an empty list is valid
      registry.personsByCity.put("city1", new ArrayList<Person>());
      assertEquals(0, validator.validate(registry).size());

      registry.personsByCity.put("city1", Arrays.asList(new Person[] {invalidPerson1}));
      assertEquals(1, validator.validate(registry).size());

      registry.personsByCity.put("city2", Arrays.asList(new Person[] {invalidPerson2}));
      assertEquals(2, validator.validate(registry).size());

      registry.personsByCity.clear();
      registry.personsByCity.put("city1", Arrays.asList(new Person[] {invalidPerson1, invalidPerson1, invalidPerson2, invalidPerson2}));
      // still only two since invalidAddress1 and invalidAddress2 have already been validated
      assertEquals(2, validator.validate(registry).size());

      registry.personsByCity.clear();

      // list with an array with empty elements is valid
      registry.addressClusters.add(new Address[10]);
      assertEquals(0, validator.validate(registry).size());

      final Address invalidAddress1 = new Address();
      final Address invalidAddress2 = new Address();

      registry.addressClusters.add(new Address[10]);
      assertEquals(0, validator.validate(registry).size());

      registry.addressClusters.add(new Address[] {invalidAddress1, invalidAddress2, invalidAddress1, invalidAddress2});
      assertEquals(2, validator.validate(registry).size());

      registry.addressClusters.clear();

      // map with an entry with an empty map is valid
      registry.addressesByCityAndStreet.put("city1", new HashMap<String, Address[]>());
      assertEquals(0, validator.validate(registry).size());

      // map with an entry with an map with an element with an empty array is valid
      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[0]);
      assertEquals(0, validator.validate(registry).size());

      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[] {invalidAddress1, invalidAddress1, invalidAddress2, invalidAddress2});
      // still only two since invalidAddress1 and invalidAddress2 have already been validated
      assertEquals(2, validator.validate(registry).size());
   }

   public void testScalarValues() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Person p = new Person();
      p.firstName = "John";
      p.lastName = "Doe";
      assertEquals(0, validator.validate(p).size());

      final Address a = new Address();
      a.street = "The Street";
      a.city = "The City";
      a.zipCode = "12345";
      assertEquals(0, validator.validate(a).size());

      // make the address invalid
      a.zipCode = null;
      assertEquals(1, validator.validate(a).size());

      // associate the invalid address with the person check the person for validity
      p.homeAddress = a;
      List<ConstraintViolation> violations = validator.validate(p);
      assertEquals(1, violations.size());
      assertEquals("ASSERT_VALID", violations.get(0).getMessage());

      // test circular dependencies
      a.contact = p;
      violations = validator.validate(p);
      assertEquals(1, violations.size());
      assertEquals("ASSERT_VALID", violations.get(0).getMessage());
   }
}
