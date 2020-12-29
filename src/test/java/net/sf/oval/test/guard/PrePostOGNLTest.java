/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import net.sf.oval.constraint.Assert;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
import net.sf.oval.guard.Pre;

/**
 * @author Sebastian Thomschke
 */
public class PrePostOGNLTest {

   @Guarded
   public static class TestTransaction {
      protected Date date;
      protected String description;
      protected BigDecimal value;
      protected boolean buggyMode = false;

      public BigDecimal getValue() {
         return value;
      }

      @Post(expr = "_this.valuePost != null", lang = "ognl", message = "POST")
      public BigDecimal getValuePost() {
         return value;
      }

      @Post(expr = "_this.valuePostWithOld != null && _old.value != null", old = "#{\"value\":_this.value}", lang = "ognl", message = "POST")
      public BigDecimal getValuePostWithOld() {
         return value;
      }

      @Pre(expr = "_this.valuePre != null", lang = "ognl", message = "PRE")
      public BigDecimal getValuePre() {
         return value;
      }

      @Pre(expr = "_this.value!=null && value2add!=null && _args[0]!=null", lang = "ognl", message = "PRE")
      @Post(expr = "_this.value>_old.value", old = "#{\"value\":_this.value}", lang = "ognl", message = "POST")
      public void increase(@Assert(expr = "_value!=null", lang = "ognl", message = "ASSERT") final BigDecimal value2add) {
         if (buggyMode) {
            value = value.subtract(value2add);
         } else {
            value = value.add(value2add);
         }
      }
   }

   @Test
   public void test1Pre() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestTransaction t = new TestTransaction();

      try {
         t.increase(BigDecimal.valueOf(1));
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("PRE");
      }

      t.value = BigDecimal.valueOf(2);
      try {
         t.increase(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("ASSERT");
      }

      t.increase(BigDecimal.valueOf(1));
   }

   @Test
   public void test2Post() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestTransaction t = new TestTransaction();
      t.value = new BigDecimal(-2);
      t.buggyMode = true;
      try {
         t.increase(BigDecimal.valueOf(1));
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("POST");
      }
      t.buggyMode = false;

      t.increase(BigDecimal.valueOf(1));
   }

   @Test
   public void test3CircularConditions() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestTransaction t = new TestTransaction();
      try {
         // test circular pre-condition
         t.getValuePre();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("PRE");
      }

      try {
         // test circular post-condition
         t.getValuePost();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("POST");
      }

      try {
         // test circular post-condition
         t.getValuePostWithOld();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("POST");
      }

      t.value = BigDecimal.valueOf(0);
      t.getValuePre();
      t.getValuePost();
      t.getValuePostWithOld();
   }
}
