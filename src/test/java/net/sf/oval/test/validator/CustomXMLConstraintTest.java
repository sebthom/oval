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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.oval.Validator;
import net.sf.oval.configuration.xml.XMLConfigurer;

/**
 * @author Sebastian Thomschke
 */
public class CustomXMLConstraintTest {
   public static class Entity {
      private String message;

      public String getMessage() {
         return message;
      }
   }

   @Test
   public void testCustomXMLConstraint() throws IOException {
      try (InputStream is = CustomXMLConstraintTest.class.getResourceAsStream("CustomXMLConstraintTest.xml")) {
         final Validator validator = new Validator(new XMLConfigurer(is));

         final Entity e = new Entity();
         assertThat(validator.validate(e)).hasSize(1);
         e.message = "123";
         assertThat(validator.validate(e)).hasSize(1);
         e.message = "12345";
         assertThat(validator.validate(e)).isEmpty();
      }
   }
}
