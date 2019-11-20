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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintListWithWhenConditionTest extends TestCase {
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

   public void testValidateAccount() throws Exception {
      final Validator validator = new Validator();

      final Account account = new Account();

      assertTrue(validator.validate(account).isEmpty());

      account.isActive = true;
      assertEquals(1, validator.validate(account).size());
      assertEquals(NotNull.class.getName() + ".violated", validator.validate(account).get(0).getMessageTemplate());
   }

   public void testValidateEntity1() throws Exception {
      final Validator validator = new Validator();

      final Entity1 entity = new Entity1();

      assertTrue(validator.validate(entity).isEmpty());

      entity.map = new HashMap<>();
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("foo", null);
      assertEquals(1, validator.validate(entity).size());

      entity.map.put("foo", "bar");
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("bar", null);
      assertEquals(1, validator.validate(entity).size());

      entity.map.put(null, "foobar");
      assertEquals(1, validator.validate(entity).size());
   }

   public void testValidateEntity2() throws Exception {
      final Validator validator = new Validator();

      final Entity2 entity = new Entity2();

      assertTrue(validator.validate(entity).isEmpty());

      entity.map = new HashMap<>();
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("foo", null);
      assertEquals(0, validator.validate(entity).size());

      entity.map.put("foo", "bar");
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("bar", null);
      assertEquals(0, validator.validate(entity).size());

      entity.map.put(null, "foobar");
      assertEquals(1, validator.validate(entity).size());
   }

   public void testValidateEntity3() throws Exception {
      final Validator validator = new Validator();

      final Entity3 entity = new Entity3();

      assertTrue(validator.validate(entity).isEmpty());

      entity.map = new HashMap<>();
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("foo", null);
      assertEquals(1, validator.validate(entity).size());

      entity.map.put("foo", "bar");
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("bar", null);
      assertEquals(1, validator.validate(entity).size());

      entity.map.put(null, "foobar");
      assertEquals(2, validator.validate(entity).size());
   }

   public void testValidateEntity4() throws Exception {
      final Validator validator = new Validator();

      final Entity4 entity = new Entity4();

      assertEquals(1, validator.validate(entity).size());

      entity.map = new HashMap<>();
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("foo", null);
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("foo", "bar");
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put("bar", null);
      assertTrue(validator.validate(entity).isEmpty());

      entity.map.put(null, "foobar");
      assertTrue(validator.validate(entity).isEmpty());
   }
}
