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
import net.sf.oval.constraint.Max;
import net.sf.oval.constraint.MaxSize;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class PrimitiveArrayTest extends TestCase {
    public static class Account {
        @MinSize(value = 1, message = "MIN_SIZE")
        @MaxSize(value = 4, message = "MAX_SIZE")
        @Max(value = 10, message = "MAX")
        @NotNull(message = "NOT_NULL")
        public int[] items = new int[] {};

    }

    public void testPrimitiveArray() {
        final Validator validator = new Validator();
        final Account account = new Account();

        // test min size
        List<ConstraintViolation> violations = validator.validate(account);
        assertEquals(1, violations.size());
        assertEquals("MIN_SIZE", violations.get(0).getMessage());

        // test valid
        account.items = new int[] { 1 };
        violations = validator.validate(account);
        assertEquals(0, violations.size());

        // test max size
        account.items = new int[] { 1, 2, 3, 4, 5 };
        violations = validator.validate(account);
        assertEquals(1, violations.size());
        assertEquals("MAX_SIZE", violations.get(0).getMessage());

        // test attribute not null
        account.items = null;
        violations = validator.validate(account);
        assertEquals(1, violations.size());
        assertEquals("NOT_NULL", violations.get(0).getMessage());

        // test elements max
        account.items = new int[] { 1, 100 };
        violations = validator.validate(account);
        assertEquals(1, violations.size());
        assertEquals("MAX", violations.get(0).getMessage());
    }
}
