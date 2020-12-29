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
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class FieldConstraintsValidationTest {
   protected static class Person {
      @NotNull
      public String firstName;

      @NotNull
      public String lastName;

      @NotNull
      @Length(max = 6, message = "LENGTH")
      @NotEmpty(message = "NOT_EMPTY")
      @MatchPattern(pattern = PATTERN_ZIP_CODE, message = "MATCH_PATTERN")
      public String zipCode;
   }

   private static final String PATTERN_ZIP_CODE = "^[0-9]*$";

   @Test
   public void testFieldValidation() {
      final Validator validator = new Validator();

      // test @NotNull
      final Person p = new Person();
      List<ConstraintViolation> violations = validator.validate(p);
      assertThat(violations).hasSize(3);

      // test @Length(max=)
      p.firstName = "Mike";
      p.lastName = "Mahoney";
      p.zipCode = "1234567";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("LENGTH");

      // test @NotEmpty
      p.zipCode = "";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_EMPTY");

      // test @RegEx
      p.zipCode = "dffd34";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MATCH_PATTERN");
   }
}
