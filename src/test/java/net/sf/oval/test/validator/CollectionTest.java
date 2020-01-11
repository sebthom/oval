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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MaxSize;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class CollectionTest extends TestCase {
   public static class Entity {
      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE}, message = "NOT_NULL")
      public final List<List<String>> listWithLists = new ArrayList<>();

      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES}, message = "NOT_NULL")
      public final List<List<String>> listWithLists2 = new ArrayList<>();

      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.KEYS, ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE}, message = "NOT_NULL")
      public final Map<List<String>, List<String>> mapWithLists = new HashMap<>();

      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.KEYS, ConstraintTarget.VALUES}, message = "NOT_NULL")
      public final Map<List<String>, List<String>> mapWithLists2 = new HashMap<>();

   }

   public static class Group {
      @MinSize(value = 1, message = "MIN_SIZE")
      @MaxSize(value = 4, message = "MAX_SIZE")
      @Length(min = 1, max = 7, message = "LENGTH")
      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES}, message = "NOT_NULL")
      public List<String> members = new ArrayList<>();

      @NotNull(appliesTo = {ConstraintTarget.VALUES}, message = "NOT_NULL2")
      public String[] secondaryMembers;
   }

   public void testListAndArray() {
      final Validator validator = new Validator();
      final Group group = new Group();

      // test min size
      List<ConstraintViolation> violations = validator.validate(group);
      assertEquals(1, violations.size());
      assertEquals("MIN_SIZE", violations.get(0).getMessage());

      // test valid
      group.members.add("member1");
      violations = validator.validate(group);
      assertEquals(0, violations.size());

      // test max size
      group.members.add("member2");
      group.members.add("member3");
      group.members.add("member4");
      group.members.add("member5");
      violations = validator.validate(group);
      assertEquals(1, violations.size());
      assertEquals("MAX_SIZE", violations.get(0).getMessage());

      // test attribute not null
      group.members = null;
      violations = validator.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());

      // test elements not null
      group.members = new ArrayList<String>();
      group.members.add(null);
      violations = validator.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());

      // test elements length
      group.members = new ArrayList<String>();
      group.members.add("");
      group.members.add("123456789");
      violations = validator.validate(group);
      assertEquals(2, violations.size());
      assertEquals("LENGTH", violations.get(0).getMessage());
      assertEquals("LENGTH", violations.get(1).getMessage());

      // test string array elements not null
      group.members = new ArrayList<String>();
      group.members.add("1234");
      group.secondaryMembers = new String[] {"foo", null, "bar"};
      violations = validator.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL2", violations.get(0).getMessage());
   }

   public void testListWithLists() {
      final Validator validator = new Validator();

      final Entity e = new Entity();

      /*
       * with ConstraintTarget.RECURSIVE
       */
      e.listWithLists.add(null);
      assertEquals(1, validator.validate(e).size());
      e.listWithLists.clear();

      e.listWithLists.add(new ArrayList<String>());
      assertEquals(0, validator.validate(e).size());
      e.listWithLists.get(0).add(null);
      assertEquals(1, validator.validate(e).size());
      e.listWithLists.clear();

      /*
       * without ConstraintTarget.RECURSIVE
       */
      e.listWithLists2.add(null);
      assertEquals(1, validator.validate(e).size());

      e.listWithLists2.clear();
      e.listWithLists2.add(new ArrayList<String>());
      assertEquals(0, validator.validate(e).size());
      e.listWithLists2.get(0).add(null);
      assertEquals(0, validator.validate(e).size());
   }

   public void testMapWithLists() {
      final Validator validator = new Validator();

      final Entity e = new Entity();

      final List<String> emptyList = Collections.emptyList();
      final List<String> listWithNull = Arrays.asList(new String[] {null});

      /*
       * with ConstraintTarget.RECURSIVE
       */
      e.mapWithLists.put(null, null);
      assertEquals(2, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(emptyList, null);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(null, emptyList);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(emptyList, emptyList);
      assertEquals(0, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(listWithNull, listWithNull);
      assertEquals(2, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(emptyList, listWithNull);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists.clear();

      e.mapWithLists.put(listWithNull, emptyList);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists.clear();

      /*
       * without ConstraintTarget.RECURSIVE
       */
      e.mapWithLists2.put(null, null);
      assertEquals(2, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(emptyList, null);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(null, emptyList);
      assertEquals(1, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(emptyList, emptyList);
      assertEquals(0, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(listWithNull, listWithNull);
      assertEquals(0, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(emptyList, listWithNull);
      assertEquals(0, validator.validate(e).size());
      e.mapWithLists2.clear();

      e.mapWithLists2.put(listWithNull, emptyList);
      assertEquals(0, validator.validate(e).size());
      e.mapWithLists2.clear();
   }

}
