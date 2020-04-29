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
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class CollectionTest extends TestCase {

   static class Group {
      @MinSize(value = 1, message = "MIN_SIZE")
      @MaxSize(value = 4, message = "MAX_SIZE")
      @Length(min = 1, max = 7, message = "LENGTH")
      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES}, message = "NOT_NULL")
      List<String> members = new ArrayList<>();

      @NotNull(appliesTo = {ConstraintTarget.VALUES}, message = "NOT_NULL2")
      String[] secondaryMembers;
   }

   static class ListWithListNonRecursiveCheck {
      @NotNull(appliesTo = {ConstraintTarget.VALUES}, message = "NOT_NULL")
      final List<List<String>> list = new ArrayList<>();
   }

   static class ListWithListRecursiveCheck {
      @NotNull(appliesTo = {ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE}, message = "NOT_NULL")
      final List<List<String>> list = new ArrayList<>();
   }

   static class ListWithListRecursiveCheckV2 {
      final List<List<@Min(value = 0, message = "MIN") Integer>> list = new ArrayList<>();
   }

   static class MapWithListNonRecursiveCheck {
      @NotNull(appliesTo = {ConstraintTarget.KEYS, ConstraintTarget.VALUES}, message = "NOT_NULL")
      final Map<List<String>, List<String>> map = new HashMap<>();
   }

   static class MapWithListRecursiveCheck {
      @NotNull(appliesTo = {ConstraintTarget.KEYS, ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE}, message = "NOT_NULL")
      final Map<List<String>, List<String>> map = new HashMap<>();
   }

   static final List<String> EMPTY_LIST = Collections.emptyList();
   static final List<String> LIST_WITH_NULL_VALUE = Collections.unmodifiableList(Arrays.asList(new String[] {null}));
   static final Validator VALIDATOR = new Validator();

   public void testListAndArray() {
      final Group group = new Group();

      // test min size
      List<ConstraintViolation> violations = VALIDATOR.validate(group);
      assertEquals(1, violations.size());
      assertEquals("MIN_SIZE", violations.get(0).getMessage());

      // test valid
      group.members.add("member1");
      violations = VALIDATOR.validate(group);
      assertEquals(0, violations.size());

      // test max size
      group.members.add("member2");
      group.members.add("member3");
      group.members.add("member4");
      group.members.add("member5");
      violations = VALIDATOR.validate(group);
      assertEquals(1, violations.size());
      assertEquals("MAX_SIZE", violations.get(0).getMessage());

      // test attribute not null
      group.members = null;
      violations = VALIDATOR.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());

      // test elements not null
      group.members = new ArrayList<>();
      group.members.add(null);
      violations = VALIDATOR.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());

      // test elements length
      group.members = new ArrayList<>();
      group.members.add("");
      group.members.add("123456789");
      violations = VALIDATOR.validate(group);
      assertEquals(2, violations.size());
      assertEquals("LENGTH", violations.get(0).getMessage());
      assertEquals("LENGTH", violations.get(1).getMessage());

      // test string array elements not null
      group.members = new ArrayList<>();
      group.members.add("1234");
      group.secondaryMembers = new String[] {"foo", null, "bar"};
      violations = VALIDATOR.validate(group);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL2", violations.get(0).getMessage());
   }

   public void testListWithListNonRecursiveCheck() {
      final ListWithListNonRecursiveCheck entity = new ListWithListNonRecursiveCheck();

      entity.list.add(null);
      assertEquals(1, VALIDATOR.validate(entity).size());

      entity.list.clear();
      entity.list.add(new ArrayList<String>());
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.list.get(0).add(null);
      assertEquals(0, VALIDATOR.validate(entity).size());
   }

   public void testListWithListRecursiveCheck() {
      final ListWithListRecursiveCheck entity = new ListWithListRecursiveCheck();

      entity.list.add(null);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.list.clear();

      entity.list.add(new ArrayList<String>());
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.list.get(0).add(null);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.list.clear();
   }

   /*
    * TODO, see AnnotationsConfigurer#initializeGenericTypeChecks()
    */
   public void ignore_testListWithListRecursiveCheckV2() {
      final ListWithListRecursiveCheckV2 entity = new ListWithListRecursiveCheckV2();

      entity.list.add(new ArrayList<Integer>());
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.list.get(0).add(-1);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.list.clear();
   }

   public void testMapWithListNonRecursiveCheck() {
      final MapWithListNonRecursiveCheck entity = new MapWithListNonRecursiveCheck();

      entity.map.put(null, null);
      assertEquals(2, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, null);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(null, EMPTY_LIST);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, EMPTY_LIST);
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, LIST_WITH_NULL_VALUE);
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, LIST_WITH_NULL_VALUE);
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, EMPTY_LIST);
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.map.clear();
   }

   public void testMapWithListRecursiveCheck() {
      final MapWithListRecursiveCheck entity = new MapWithListRecursiveCheck();

      entity.map.put(null, null);
      assertEquals(2, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, null);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(null, EMPTY_LIST);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, EMPTY_LIST);
      assertEquals(0, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, LIST_WITH_NULL_VALUE);
      assertEquals(2, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(EMPTY_LIST, LIST_WITH_NULL_VALUE);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, EMPTY_LIST);
      assertEquals(1, VALIDATOR.validate(entity).size());
      entity.map.clear();
   }
}
