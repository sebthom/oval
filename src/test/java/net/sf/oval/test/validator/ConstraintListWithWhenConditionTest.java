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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintListWithWhenConditionTest {

   static class Account {
      boolean isActive = false;

      @NotNull.List(value = @NotNull, when = "groovy:_this.isActive")
      String password;
   }

   static class Entity1 {
      @NotNull.List(value = @NotNull(appliesTo = ConstraintTarget.VALUES), when = "groovy:_value?.containsKey('foo')")
      Map<String, String> map;
   }

   static class Entity2 {
      @NotNull.List(value = @NotNull(appliesTo = ConstraintTarget.KEYS), when = "groovy:_value?.containsKey('foo')")
      Map<String, String> map;
   }

   static class Entity3 {
      @NotNull.List(value = @NotNull(appliesTo = {ConstraintTarget.KEYS, ConstraintTarget.VALUES}), when = "groovy:_value?.containsKey('foo')")
      Map<String, String> map;
   }

   static class Entity4 {
      @NotNull.List(value = @NotNull(appliesTo = ConstraintTarget.CONTAINER), when = "groovy:_value==null")
      Map<String, String> map;
   }

   @Test
   public void testValidateAccount() throws Exception {
      final Validator validator = new Validator();

      final Account account = new Account();

      assertThat(validator.validate(account).isEmpty()).isTrue();

      account.isActive = true;
      assertThat(validator.validate(account)).hasSize(1);
      assertThat(validator.validate(account).get(0).getMessageTemplate()).isEqualTo(NotNull.class.getName() + ".violated");
   }

   @Test
   public void testValidateEntity1() throws Exception {
      final Validator validator = new Validator();

      final Entity1 entity = new Entity1();

      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map = new HashMap<>();
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("foo", null);
      assertThat(validator.validate(entity)).hasSize(1);

      entity.map.put("foo", "bar");
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("bar", null);
      assertThat(validator.validate(entity)).hasSize(1);

      entity.map.put(null, "foobar");
      assertThat(validator.validate(entity)).hasSize(1);
   }

   @Test
   public void testValidateEntity2() throws Exception {
      final Validator validator = new Validator();

      final Entity2 entity = new Entity2();

      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map = new HashMap<>();
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("foo", null);
      assertThat(validator.validate(entity)).isEmpty();

      entity.map.put("foo", "bar");
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("bar", null);
      assertThat(validator.validate(entity)).isEmpty();

      entity.map.put(null, "foobar");
      assertThat(validator.validate(entity)).hasSize(1);
   }

   @Test
   public void testValidateEntity3() throws Exception {
      final Validator validator = new Validator();

      final Entity3 entity = new Entity3();

      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map = new HashMap<>();
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("foo", null);
      assertThat(validator.validate(entity)).hasSize(1);

      entity.map.put("foo", "bar");
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("bar", null);
      assertThat(validator.validate(entity)).hasSize(1);

      entity.map.put(null, "foobar");
      assertThat(validator.validate(entity)).hasSize(2);
   }

   @Test
   public void testValidateEntity4() throws Exception {
      final Validator validator = new Validator();

      final Entity4 entity = new Entity4();

      assertThat(validator.validate(entity)).hasSize(1);

      entity.map = new HashMap<>();
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("foo", null);
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("foo", "bar");
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put("bar", null);
      assertThat(validator.validate(entity).isEmpty()).isTrue();

      entity.map.put(null, "foobar");
      assertThat(validator.validate(entity).isEmpty()).isTrue();
   }
}
