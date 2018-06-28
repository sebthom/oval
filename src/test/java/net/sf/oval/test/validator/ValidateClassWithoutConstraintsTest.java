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
import net.sf.oval.configuration.xml.XMLConfigurer;

/**
 * @author Sebastian Thomschke
 */
public class ValidateClassWithoutConstraintsTest extends TestCase {
   protected static class TestEntity {
      protected String name;

      protected TestEntity(final String name) {
         this.name = name;
      }

      /**
       * @param name the name to set
       */
      public void setName(final String name) {
         this.name = name;
      }
   }

   public void testClassWithoutConstraints() {
      final TestEntity e = new TestEntity(null);

      final Validator v = new Validator();
      final List<ConstraintViolation> violations = v.validate(e);
      assertEquals(0, violations.size());
   }

   public void testEmptyXmlConfigurer() {
      final XMLConfigurer xmlConfigurer = new XMLConfigurer();
      final Validator v = new Validator(xmlConfigurer);

      final TestEntity e = new TestEntity(null);

      final List<ConstraintViolation> violations = v.validate(e);
      assertEquals(0, violations.size());
   }
}
