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

import org.junit.Test;

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
public class CustomAssertValidTest {

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
      protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(final ConstraintAnnotation constraintAnnotation,
         final ConstraintTarget... targetOverrides) throws ReflectionException {
         if (constraintAnnotation instanceof CustomAssertValid) {
            final CustomAssertValid customAssertValid = (CustomAssertValid) constraintAnnotation;

            // instantiate a AssertValidCheck based on the custom constraint annotation
            final AssertValidCheck assertValidCheck = new AssertValidCheck();
            assertValidCheck.setErrorCode(customAssertValid.errorCode());
            assertValidCheck.setMessage(customAssertValid.message());
            assertValidCheck.setProfiles(customAssertValid.profiles());
            if (targetOverrides.length > 0) {
               assertValidCheck.setAppliesTo(targetOverrides);
            } else {
               assertValidCheck.setAppliesTo(customAssertValid.appliesTo());
            }
            assertValidCheck.setSeverity(customAssertValid.severity());
            return (AnnotationCheck<ConstraintAnnotation>) assertValidCheck;
         }

         return super.initializeCheck(constraintAnnotation, targetOverrides);
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

   @Test
   public void testCollectionValues() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Person p = new Person();
      p.firstName = "John";
      p.lastName = "Doe";
      p.otherAddresses1 = new ArrayList<>();
      p.otherAddresses2 = new HashSet<>();
      p.otherAddresses3 = new HashSet<>();

      final Address a = new Address();
      a.street = "The Street";
      a.city = "The City";
      a.zipCode = null;
      assertThat(validator.validate(a)).hasSize(1);

      p.otherAddresses1.add(a);
      assertThat(validator.validate(p)).hasSize(1);

      p.otherAddresses1.remove(a);
      p.otherAddresses2.add(a);
      assertThat(validator.validate(p)).isEmpty();

      p.otherAddresses3.add(a);
      assertThat(validator.validate(p)).hasSize(1);
   }

   @Test
   public void testRecursion() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Registry registry = new Registry();

      // nulled collections and maps are valid
      assertThat(validator.validate(registry)).isEmpty();

      registry.addressesByCityAndStreet = new HashMap<>();
      registry.addressClusters = new ArrayList<>();
      registry.personsByCity = new HashMap<>();

      // empty collections and maps are valid
      assertThat(validator.validate(registry)).isEmpty();

      final Person invalidPerson1 = new Person();
      final Person invalidPerson2 = new Person();

      // map with an empty list is valid
      registry.personsByCity.put("city1", new ArrayList<Person>());
      assertThat(validator.validate(registry)).isEmpty();

      registry.personsByCity.put("city1", Arrays.asList(invalidPerson1));
      assertThat(validator.validate(registry).stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].firstName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].lastName cannot be null" //
      );

      registry.personsByCity.put("city2", Arrays.asList(invalidPerson2));
      assertThat(validator.validate(registry).stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].firstName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].lastName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city2\"][0].firstName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city2\"][0].lastName cannot be null" //
      );

      registry.personsByCity.clear();
      registry.personsByCity.put("city1", Arrays.asList(invalidPerson1, invalidPerson1, invalidPerson2, invalidPerson2));
      // still only two since invalidAddress1 and invalidAddress2 have already been validated
      assertThat(validator.validate(registry).stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].firstName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city1\"][0].lastName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city1\"][2].firstName cannot be null", //
         Registry.class.getName() + ".personsByCity[\"city1\"][2].lastName cannot be null" //
      );
      registry.personsByCity.clear();

      // list with an array with empty elements is valid
      registry.addressClusters.add(new Address[10]);
      assertThat(validator.validate(registry)).isEmpty();

      final Address invalidAddress1 = new Address();
      final Address invalidAddress2 = new Address();

      registry.addressClusters.add(new Address[10]);
      assertThat(validator.validate(registry)).isEmpty();

      registry.addressClusters.add(new Address[] {invalidAddress1, invalidAddress2, invalidAddress1, invalidAddress2});
      assertThat(validator.validate(registry).stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Registry.class.getName() + ".addressClusters[2][0].city cannot be null", //
         Registry.class.getName() + ".addressClusters[2][0].street cannot be null", //
         Registry.class.getName() + ".addressClusters[2][0].zipCode cannot be null", //
         Registry.class.getName() + ".addressClusters[2][1].city cannot be null", //
         Registry.class.getName() + ".addressClusters[2][1].street cannot be null", //
         Registry.class.getName() + ".addressClusters[2][1].zipCode cannot be null" //
      );

      registry.addressClusters.clear();

      // map with an entry with an empty map is valid
      registry.addressesByCityAndStreet.put("city1", new HashMap<String, Address[]>());
      assertThat(validator.validate(registry)).isEmpty();

      // map with an entry with an map with an element with an empty array is valid
      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[0]);
      assertThat(validator.validate(registry)).isEmpty();

      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[] {invalidAddress1, invalidAddress1, invalidAddress2, invalidAddress2});
      // still only two since invalidAddress1 and invalidAddress2 have already been validated
      assertThat(validator.validate(registry).stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][0].city cannot be null", //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][0].street cannot be null", //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][0].zipCode cannot be null", //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][2].city cannot be null", //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][2].street cannot be null", //
         Registry.class.getName() + ".addressesByCityAndStreet[\"city1\"][\"street1\"][2].zipCode cannot be null" //
      );
   }

   @Test
   public void testScalarValues() {
      final Validator validator = new Validator(new CustomAnnotationConfigurer());

      final Person p = new Person();
      p.firstName = "John";
      p.lastName = "Doe";
      assertThat(validator.validate(p)).isEmpty();

      final Address a = new Address();
      a.street = "The Street";
      a.city = "The City";
      a.zipCode = "12345";
      assertThat(validator.validate(a)).isEmpty();

      // make the address invalid
      a.zipCode = null;
      List<ConstraintViolation> violations = validator.validate(a);
      assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Address.class.getName() + ".zipCode cannot be null" //
      );
      assertThat(violations.stream().map(ConstraintViolation::getContextPathAsString)).containsOnly( //
         Address.class.getName() + ".zipCode" //
      );

      // associate the invalid address with the person check the person for validity
      p.homeAddress = a;
      violations = validator.validate(p);
      assertThat(violations.stream().map(ConstraintViolation::getContextPathAsString)).containsOnly( //
         Person.class.getName() + ".homeAddress.zipCode" //
      );
      assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Person.class.getName() + ".homeAddress.zipCode cannot be null" //
      );

      // test circular dependencies
      a.contact = p;
      violations = validator.validate(p);
      assertThat(violations.stream().map(ConstraintViolation::getContextPathAsString)).containsOnly( //
         Person.class.getName() + ".homeAddress.zipCode" //
      );
      assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
         Person.class.getName() + ".homeAddress.zipCode cannot be null" //
      );
   }
}
