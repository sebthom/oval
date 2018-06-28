/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ObjectGraphTest extends TestCase {
   protected static class ClassA {
      @AssertValid
      ClassB classB;

      @AssertValid
      ClassC classC;
   }

   protected static class ClassB {
      @AssertValid
      ClassC classC;
   }

   protected static class ClassC {
      @AssertValid
      ClassA classA;

      @NotNull
      String name;
   }

   public void testObjectGraph() {
      final ClassA classA = new ClassA();
      classA.classB = new ClassB();
      classA.classC = new ClassC();
      classA.classC.classA = classA;
      classA.classB.classC = classA.classC;

      final Validator validator = new Validator();
      final List<ConstraintViolation> violations = validator.validate(classA);
      assertEquals(1, violations.size());
   }

}
