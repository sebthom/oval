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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class InvalidValuePathTest {

   public static class Parent {
      @AssertValid(message = "INVALID_REF")
      @NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE}, message = "NOT_NULL_REF")
      Map<String, List<Child[][]>> refsByKey = new HashMap<>();

      @NotNull(message = "NOT_NULL_NAME")
      String name;

      @NotEmpty(message = "NOT_EMPTY_FAVORITE_NAME", target = "name")
      Child favorite;
   }

   public static class Child {

      @NotNull(message = "NOT_NULL_NAME")
      String name;
   }

   @Test
   public void testValuePath() {
      final Validator v = new Validator();

      final Parent e = new Parent();
      e.name = "foo";

      {
         final List<ConstraintViolation> violations = v.validate(e);
         assertThat(violations).isEmpty();
      }

      {
         e.refsByKey.clear();
         e.refsByKey.put("foo", null);
         final List<ConstraintViolation> violations = v.validate(e);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getContextPathAsString()).isEqualTo(Parent.class.getName() + ".refsByKey[\"foo\"]");
      }

      {
         e.refsByKey.clear();
         e.favorite = new Child();
         e.favorite.name = "";
         final List<ConstraintViolation> violations = v.validate(e);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("NOT_EMPTY_FAVORITE_NAME");
         assertThat(violations.get(0).getContextPathAsString()).isEqualTo(Parent.class.getName() + ".favorite.name");
         e.favorite = null;
      }

      {
         e.refsByKey.clear();
         final List<Child[][]> refs = new ArrayList<>();
         e.refsByKey.put("foo", refs);
         final Child c = new Child();
         c.name = "bar";
         refs.add(new Child[][] {{}, {}});
         refs.add(new Child[][] {null});
         refs.add(new Child[][] {{}, {}, null});
         refs.add(new Child[][] {{}, {}, {c, c, c, null}});
         final List<ConstraintViolation> violations = v.validate(e);
         assertThat(violations.stream().map(ConstraintViolation::getContextPathAsString)).containsOnly( //
            Parent.class.getName() + ".refsByKey[\"foo\"][1][0]", //
            Parent.class.getName() + ".refsByKey[\"foo\"][2][2]", //
            Parent.class.getName() + ".refsByKey[\"foo\"][3][2][3]" //
         );
      }

      {
         e.refsByKey.clear();
         final List<Child[][]> refs = new ArrayList<>();
         e.refsByKey.put("foo", refs);
         refs.add(new Child[][] {{new Child()}});
         final List<ConstraintViolation> violations = v.validate(e);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL_NAME");
         assertThat(violations.get(0).getContextPathAsString()).isEqualTo(Parent.class.getName() + ".refsByKey[\"foo\"][0][0][0].name");
      }
   }
}
