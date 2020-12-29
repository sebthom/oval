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

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.Assert;

/**
 * @author Sebastian Thomschke
 */
public class AssertGroovyTest {

   @Assert( //
      expr = "_this.firstName!=null && _this.lastName!=null && (_this.firstName.length() + _this.lastName.length() > 9)", //
      lang = "groovy", //
      errorCode = "C0" //
   )
   protected static class Person {
      @Assert(expr = "_value!=null", lang = "groovy", errorCode = "C1")
      public String firstName;

      @Assert(expr = "_value!=null", lang = "groovy", errorCode = "C2")
      public String lastName;

      @Assert(expr = "_value!=null && _value.length()>0 && _value.length()<7", lang = "groovy", errorCode = "C3")
      public String zipCode;
   }

   private static class TestRunner implements Runnable {
      private final boolean[] failed;
      private final Validator validator;
      private final Person person;

      TestRunner(final Validator validator, final Person person, final boolean[] failed) {
         this.validator = validator;
         this.person = person;
         this.failed = failed;
      }

      @Override
      public void run() {
         for (int i = 0; i < 500; i++) {
            // test not null
            if (validator.validate(person).size() != 4) {
               failed[0] = true;
            }

            try {
               Thread.sleep(2);
            } catch (final InterruptedException e) {
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   @Test
   public void testConcurrency() throws InterruptedException {
      final Validator validator = new Validator();

      final Person person = new Person();

      final boolean[] failed = {false};
      final Thread thread1 = new Thread(new TestRunner(validator, person, failed));
      final Thread thread2 = new Thread(new TestRunner(validator, person, failed));
      thread1.start();
      thread2.start();
      thread1.join();
      thread2.join();
      assertThat(failed[0]).isFalse();
   }

   @Test
   public void testGroovyExpression() {
      final Validator validator = new Validator();

      // test not null
      final Person p = new Person();
      List<ConstraintViolation> violations = validator.validate(p);
      assertThat(violations).hasSize(4);

      // test max length
      p.firstName = "Mike";
      p.lastName = "Mahoney";
      p.zipCode = "1234567"; // too long
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getErrorCode()).isEqualTo("C3");

      // test not empty
      p.zipCode = "";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getErrorCode()).isEqualTo("C3");

      // test ok
      p.zipCode = "wqeew";
      violations = validator.validate(p);
      assertThat(violations).isEmpty();

      // test object-level constraint
      p.firstName = "12345";
      p.lastName = "1234";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getErrorCode()).isEqualTo("C0");
   }
}
