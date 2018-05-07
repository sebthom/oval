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

import java.text.DateFormat;
import java.util.Calendar;

import net.sf.oval.constraint.FutureCheck;

/**
 * @author Sebastian Thomschke
 */
public class FutureTest extends AbstractContraintsTest {
    public void testFuture() {
        final FutureCheck check = new FutureCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        final Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, 1);
        assertTrue(check.isSatisfied(null, cal, null, null));
        assertTrue(check.isSatisfied(null, cal.getTime(), null, null));
        assertTrue(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        cal.roll(Calendar.YEAR, -2);
        assertFalse(check.isSatisfied(null, cal, null, null));
        assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
        assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        assertFalse(check.isSatisfied(null, "bla", null, null));
    }

    public void testTolerance() {
        final FutureCheck check = new FutureCheck();

        final Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.SECOND, -2);
        assertFalse(check.isSatisfied(null, cal, null, null));
        check.setTolerance(1500);
        assertFalse(check.isSatisfied(null, cal, null, null));
        check.setTolerance(5000);
        assertTrue(check.isSatisfied(null, cal, null, null));
    }
}
