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

import java.lang.reflect.Field;
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
import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidTest {

   protected static class Address {
      @NotNull(message = "NOT_NULL_STREET")
      public String street;

      @NotNull
      public String city;

      @NotNull
      @Length(max = 6)
      @NotEmpty
      @MatchPattern(pattern = "^[0-9]*$")
      public String zipCode;

      @AssertValid(message = "ASSERT_VALID")
      public Person contact;
   }

   protected static class Person {
      @NotNull
      public String firstName;

      @NotNull
      public String lastName;

      @AssertValid(message = "ASSERT_VALID")
      public Address homeAddress;

      @AssertValid(message = "ASSERT_VALID")
      public List<Address> otherAddresses1;

      @AssertValid(message = "ASSERT_VALID", appliesTo = ConstraintTarget.CONTAINER)
      public Set<Address> otherAddresses2;

      @AssertValid(message = "ASSERT_VALID", appliesTo = {ConstraintTarget.VALUES, ConstraintTarget.CONTAINER})
      public Set<Address> otherAddresses3;

   }

   protected static class Registry {
      @AssertValid
      public List<Address[]> addressClusters;

      @AssertValid
      public Map<String, List<Person>> personsByCity;

      @AssertValid
      public Map<String, Map<String, Address[]>> addressesByCityAndStreet;
   }

   @Test
   public void testValidateFieldValue() throws SecurityException, NoSuchFieldException {
      final Validator validator = new Validator();

      final Person p = new Person();
      final Field fieldHomeAddress = p.getClass().getDeclaredField("homeAddress");
      final Address a = new Address();

      final List<ConstraintViolation> violations = validator.validateFieldValue(p, fieldHomeAddress, a);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("ASSERT_VALID");
      assertThat(violations.get(0).getInvalidValue()).isEqualTo(a);
      assertThat(violations.get(0).getCauses()).hasSize(3);
      for (final ConstraintViolation cv : violations.get(0).getCauses())
         if ("NOT_NULL_STREET".equals(cv.getMessage())) {
            assertThat(((FieldContext) cv.getContext()).getField().getName()).isEqualTo("street");
         }
      a.street = "The Street";
      a.city = "The City";
      a.zipCode = "12345";
      assertThat(validator.validateFieldValue(p, fieldHomeAddress, a)).isEmpty();
   }

   @Test
   public void testCollectionValues() {
      final Validator validator = new Validator();

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
      final Validator validator = new Validator();

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
      assertThat(validator.validate(registry)).hasSize(1);

      registry.personsByCity.put("city2", Arrays.asList(invalidPerson2));
      assertThat(validator.validate(registry)).hasSize(2);

      registry.personsByCity.clear();
      registry.personsByCity.put("city1", Arrays.asList(invalidPerson1, invalidPerson1, invalidPerson2, invalidPerson2));
      // still only two since invalidPerson1 and invalidPerson2 have already been validated
      assertThat(validator.validate(registry)).hasSize(2);

      registry.personsByCity.clear();

      // list with an array with empty elements is valid
      registry.addressClusters.add(new Address[10]);
      assertThat(validator.validate(registry)).isEmpty();

      final Address invalidAddress1 = new Address();
      final Address invalidAddress2 = new Address();

      registry.addressClusters.add(new Address[10]);
      assertThat(validator.validate(registry)).isEmpty();

      registry.addressClusters.add(new Address[] {invalidAddress1, invalidAddress2, invalidAddress1, invalidAddress2});
      // still only two since invalidPerson1 and invalidPerson2 have already been validated
      assertThat(validator.validate(registry)).hasSize(2);

      registry.addressClusters.clear();

      // map with an entry with an empty map is valid
      registry.addressesByCityAndStreet.put("city1", new HashMap<String, Address[]>());
      assertThat(validator.validate(registry)).isEmpty();

      // map with an entry with an map with an element with an empty array is valid
      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[0]);
      assertThat(validator.validate(registry)).isEmpty();

      registry.addressesByCityAndStreet.get("city1").put("street1", new Address[] {invalidAddress1, invalidAddress1, invalidAddress2, invalidAddress2});
      // still only two since invalidAddress1 and invalidAddress2 have already been validated
      assertThat(validator.validate(registry)).hasSize(2);
   }

   @Test
   public void testScalarValues() {
      final Validator validator = new Validator();

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
      assertThat(validator.validate(a)).hasSize(1);

      // associate the invalid address with the person check the person for validity
      p.homeAddress = a;
      List<ConstraintViolation> violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("ASSERT_VALID");

      // test circular dependencies
      a.contact = p;
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("ASSERT_VALID");
   }
}
