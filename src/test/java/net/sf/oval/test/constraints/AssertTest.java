/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.AssertCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertTest extends AbstractContraintsTest {
   private void testAssert(final String language, final String expr1, final String expr2) {
      final AssertCheck check = new AssertCheck();
      super.testCheck(check);

      check.setLang(language);
      assertEquals(check.getLang(), language);

      check.setExpr(expr1);
      assertEquals(check.getExpr(), expr1);
      assertFalse(check.isSatisfied(this, null, null, validator));
      assertTrue(check.isSatisfied(this, "", null, validator));

      check.setExpr(expr2);
      assertEquals(check.getExpr(), expr2);
      assertFalse(check.isSatisfied(null, null, null, validator));
      assertTrue(check.isSatisfied(this, null, null, validator));
   }

   public void testAssertBeanshell() {
      testAssert("bsh", "_value!=null", "_this!=null");
      testAssert("beanshell", "_value!=null", "_this!=null");
   }

   public void testAssertGroovy() {
      testAssert("groovy", "_value!=null", "_this!=null");
   }

   public void testAssertJavascript() {
      testAssert("js", "_value!=null", "_this!=null");
      testAssert("javascript", "_value!=null", "_this!=null");
   }

   public void testAssertMVEL() {
      testAssert("mvel", "_value!=null", "_this!=null");
   }

   public void testAssertOGNL() {
      testAssert("ognl", "_value!=null", "_this!=null");
   }

   public void testAssertRuby() {
      testAssert("ruby", "_value!=nil", "_this!=nil");
   }
}
