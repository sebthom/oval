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
        final FutureCheck isInFuture = new FutureCheck();
        super.testCheck(isInFuture);

        assertTrue(isInFuture.isSatisfied(null, null, null, null));

        final Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, 1);
        assertTrue(isInFuture.isSatisfied(null, cal, null, null));
        assertTrue(isInFuture.isSatisfied(null, cal.getTime(), null, null));
        assertTrue(isInFuture.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        cal.roll(Calendar.YEAR, -2);
        assertFalse(isInFuture.isSatisfied(null, cal, null, null));
        assertFalse(isInFuture.isSatisfied(null, cal.getTime(), null, null));
        assertFalse(isInFuture.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        assertFalse(isInFuture.isSatisfied(null, "bla", null, null));
    }

    public void testTolerance() {
        final FutureCheck isInFuture = new FutureCheck();

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -2000); // roll() does not work with milliseconds

        isInFuture.setTolerance(0);
        assertFalse(isInFuture.isSatisfied(null, cal, null, null));

        isInFuture.setTolerance(1000);
        assertFalse(isInFuture.isSatisfied(null, cal, null, null));

        isInFuture.setTolerance(5000);
        assertTrue(isInFuture.isSatisfied(null, cal, null, null));
    }
}
