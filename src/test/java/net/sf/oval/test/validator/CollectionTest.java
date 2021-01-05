/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

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
public class CollectionTest {

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

   @Test
   public void testListAndArray() {
      final Group group = new Group();

      // test min size
      List<ConstraintViolation> violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MIN_SIZE");

      // test valid
      group.members.add("member1");
      violations = VALIDATOR.validate(group);
      assertThat(violations).isEmpty();

      // test max size
      group.members.add("member2");
      group.members.add("member3");
      group.members.add("member4");
      group.members.add("member5");
      violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MAX_SIZE");

      // test attribute not null
      group.members = null;
      violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");

      // test elements not null
      group.members = new ArrayList<>();
      group.members.add(null);
      violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");

      // test elements length
      group.members = new ArrayList<>();
      group.members.add("");
      group.members.add("123456789");
      violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(2);
      assertThat(violations.get(0).getMessage()).isEqualTo("LENGTH");
      assertThat(violations.get(1).getMessage()).isEqualTo("LENGTH");

      // test string array elements not null
      group.members = new ArrayList<>();
      group.members.add("1234");
      group.secondaryMembers = new String[] {"foo", null, "bar"};
      violations = VALIDATOR.validate(group);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL2");
   }

   @Test
   public void testListWithListNonRecursiveCheck() {
      final ListWithListNonRecursiveCheck entity = new ListWithListNonRecursiveCheck();

      entity.list.add(null);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);

      entity.list.clear();
      entity.list.add(new ArrayList<String>());
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.list.get(0).add(null);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
   }

   @Test
   public void testListWithListRecursiveCheck() {
      final ListWithListRecursiveCheck entity = new ListWithListRecursiveCheck();

      entity.list.add(null);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.list.clear();

      entity.list.add(new ArrayList<String>());
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.list.get(0).add(null);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.list.clear();
   }

   /**
    * TODO, see AnnotationsConfigurer#initializeGenericTypeChecks()
    */
   @Ignore("see AnnotationsConfigurer#initializeGenericTypeChecks()")
   @Test
   public void testListWithListRecursiveCheckV2() {
      final ListWithListRecursiveCheckV2 entity = new ListWithListRecursiveCheckV2();

      entity.list.add(new ArrayList<Integer>());
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.list.get(0).add(-1);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.list.clear();
   }

   @Test
   public void testMapWithListNonRecursiveCheck() {
      final MapWithListNonRecursiveCheck entity = new MapWithListNonRecursiveCheck();

      entity.map.put(null, null);
      assertThat(VALIDATOR.validate(entity)).hasSize(2);
      entity.map.clear();

      entity.map.put(EMPTY_LIST, null);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();

      entity.map.put(null, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();

      entity.map.put(EMPTY_LIST, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, LIST_WITH_NULL_VALUE);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.map.clear();

      entity.map.put(EMPTY_LIST, LIST_WITH_NULL_VALUE);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.map.clear();
   }

   @Test
   public void testMapWithListRecursiveCheck() {
      final MapWithListRecursiveCheck entity = new MapWithListRecursiveCheck();

      entity.map.put(null, null);
      assertThat(VALIDATOR.validate(entity)).hasSize(2);
      entity.map.clear();

      entity.map.put(EMPTY_LIST, null);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();

      entity.map.put(null, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();

      entity.map.put(EMPTY_LIST, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).isEmpty();
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, LIST_WITH_NULL_VALUE);
      assertThat(VALIDATOR.validate(entity)).hasSize(2);
      entity.map.clear();

      entity.map.put(EMPTY_LIST, LIST_WITH_NULL_VALUE);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();

      entity.map.put(LIST_WITH_NULL_VALUE, EMPTY_LIST);
      assertThat(VALIDATOR.validate(entity)).hasSize(1);
      entity.map.clear();
   }
}
