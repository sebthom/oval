/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.ValidationCycle;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertTest extends AbstractContraintsTest {

   private void testAssert(final String language, final String expr1, final String expr2) {
      final AssertCheck check = new AssertCheck();
      super.testCheck(check);

      check.setLang(language);
      assertThat(language).isEqualTo(check.getLang());

      check.setExpr(expr1);
      final ValidationCycle cycle = new ValidationCycle() {
         @Override
         public void addConstraintViolation(final ConstraintViolation violation) {
            throw new UnsupportedOperationException();
         }

         @Override
         public List<OValContext> getContextPath() {
            return null;
         }

         @Override
         public Object getRootObject() {
            return AssertTest.this;
         }

         @Override
         public Validator getValidator() {
            return validator;
         }
      };
      assertThat(expr1).isEqualTo(check.getExpr());
      assertThat(check.isSatisfied(this, null, cycle)).isFalse();
      assertThat(check.isSatisfied(this, "", cycle)).isTrue();

      check.setExpr(expr2);
      assertThat(expr2).isEqualTo(check.getExpr());
      assertThat(check.isSatisfied(null, null, cycle)).isFalse();
      assertThat(check.isSatisfied(this, null, cycle)).isTrue();
   }

   @Test
   public void testAssertBeanshell() {
      testAssert("bsh", "_value!=null", "_this!=null");
      testAssert("beanshell", "_value!=null", "_this!=null");
   }

   @Test
   public void testAssertGroovy() {
      testAssert("groovy", "_value!=null", "_this!=null");
   }

   @Test
   public void testAssertJavascript() {
      testAssert("js", "_value!=null", "_this!=null");
      testAssert("javascript", "_value!=null", "_this!=null");
   }

   @Test
   public void testAssertMVEL() {
      testAssert("mvel", "_value!=null", "_this!=null");
   }

   @Test
   public void testAssertOGNL() {
      testAssert("ognl", "_value!=null", "_this!=null");
   }

   @Test
   public void testAssertRuby() {
      testAssert("ruby", "_value!=nil", "_this!=nil");
   }
}
