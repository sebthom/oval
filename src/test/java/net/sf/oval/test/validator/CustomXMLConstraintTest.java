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

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.xml.XMLConfigurer;

/**
 * @author Sebastian Thomschke
 */
public class CustomXMLConstraintTest extends TestCase {
   public static class Entity {
      private String message;

      public String getMessage() {
         return message;
      }
   }

   public void testCustomXMLConstraint() {
      final Validator validator = new Validator(new XMLConfigurer(CustomXMLConstraintTest.class.getResourceAsStream("CustomXMLConstraintTest.xml")));

      final Entity e = new Entity();
      assertEquals(1, validator.validate(e).size());
      e.message = "123";
      assertEquals(1, validator.validate(e).size());
      e.message = "12345";
      assertEquals(0, validator.validate(e).size());
   }
}
