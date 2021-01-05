/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.InstanceOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfTest extends AbstractContraintsTest {
   public static class ClassA implements InterfaceA {
      //
   }

   public static class ClassB implements InterfaceA, InterfaceB {
      //
   }

   public interface InterfaceA {
      //
   }

   public interface InterfaceB {
      //
   }

   @Test
   public void testInstanceOf() {
      final InstanceOfCheck check = new InstanceOfCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      check.setTypes(InterfaceA.class);
      assertThat(check.getTypes()[0]).isEqualTo(InterfaceA.class);

      assertThat(check.isSatisfied(null, new ClassA(), null)).isTrue();
      assertThat(check.isSatisfied(null, new ClassB(), null)).isTrue();
      assertThat(check.isSatisfied(null, "bla", null)).isFalse();

      check.setTypes(InterfaceA.class, InterfaceB.class);
      assertThat(check.getTypes()[0]).isEqualTo(InterfaceA.class);
      assertThat(check.getTypes()[1]).isEqualTo(InterfaceB.class);

      assertThat(check.isSatisfied(null, new ClassA(), null)).isFalse();
      assertThat(check.isSatisfied(null, new ClassB(), null)).isTrue();
      assertThat(check.isSatisfied(null, "bla", null)).isFalse();
   }
}
