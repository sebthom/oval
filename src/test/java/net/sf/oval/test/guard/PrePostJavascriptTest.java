/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.oval.constraint.Assert;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
import net.sf.oval.guard.Pre;

/**
 * @author Sebastian Thomschke
 */
public class PrePostJavascriptTest extends TestCase {
    @Guarded
    public static class TestTransaction {
        protected Date date;
        protected String description;
        protected BigDecimal value;
        protected boolean buggyMode = false;

        /**
         * @return the value
         */
        public BigDecimal getValue() {
            return value;
        }

        @Post(expr = "_this.valuePost != null", lang = "js", message = "POST")
        public BigDecimal getValuePost() {
            return value;
        }

        @Post(expr = "_this.valuePostWithOld != null && _old != null", old = "_this.value", lang = "js", message = "POST")
        public BigDecimal getValuePostWithOld() {
            return value;
        }

        @Pre(expr = "_this.valuePre != null", lang = "js", message = "PRE")
        public BigDecimal getValuePre() {
            return value;
        }

        @Pre(expr = "_this.value!=null && value2add!=null && _args[0]!=null", lang = "js", message = "PRE")
        @Post(expr = "_this.value>_old", old = "_this.value", lang = "javascript", message = "POST")
        public void increase(@Assert(expr = "_value!=null", lang = "js", message = "ASSERT") final BigDecimal value2add) {
            if (buggyMode)
                value = value.subtract(value2add);
            else
                value = value.add(value2add);
        }
    }

    public void test1Pre() {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        final TestTransaction t = new TestTransaction();

        try {
            t.increase(BigDecimal.valueOf(1));
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "PRE");
        }

        t.value = BigDecimal.valueOf(2);
        try {
            t.increase(null);
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "ASSERT");
        }
        try {
            t.increase(BigDecimal.valueOf(1));
        } catch (final ConstraintsViolatedException ex) {
            System.out.println(ex.getConstraintViolations()[0].getMessage());
        }
    }

    public void test2Post() {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        final TestTransaction t = new TestTransaction();
        t.value = new BigDecimal(-2);
        t.buggyMode = true;
        try {
            t.increase(BigDecimal.valueOf(1));
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "POST");
        }
        t.buggyMode = false;

        t.increase(BigDecimal.valueOf(1));
    }

    public void test3CircularConditions() {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        final TestTransaction t = new TestTransaction();
        try {
            // test circular pre-condition
            t.getValuePre();
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "PRE");
        }

        try {
            // test circular post-condition
            t.getValuePost();
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "POST");
        }

        try {
            // test circular post-condition
            t.getValuePostWithOld();
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertEquals(ex.getConstraintViolations()[0].getMessage(), "POST");
        }

        t.value = BigDecimal.valueOf(0);
        t.getValuePre();
        t.getValuePost();
        t.getValuePostWithOld();
    }
}