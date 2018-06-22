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

import net.sf.oval.constraint.PastCheck;

/**
 * @author Sebastian Thomschke
 */
public class PastTest extends AbstractContraintsTest {

    public void testPast() {
        final PastCheck isInPast = new PastCheck();
        super.testCheck(isInPast);
        assertTrue(isInPast.isSatisfied(null, null, null, null));

        final Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, -1);
        assertTrue(isInPast.isSatisfied(null, cal, null, null));
        assertTrue(isInPast.isSatisfied(null, cal.getTime(), null, null));
        assertTrue(isInPast.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        cal.roll(Calendar.YEAR, 2);
        assertFalse(isInPast.isSatisfied(null, cal, null, null));
        assertFalse(isInPast.isSatisfied(null, cal.getTime(), null, null));
        assertFalse(isInPast.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

        assertFalse(isInPast.isSatisfied(null, "bla", null, null));
    }

    public void testTolerance() {
        final PastCheck isInPast = new PastCheck();

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, 2000); // roll() does not work with milliseconds

        isInPast.setTolerance(0);
        assertFalse(isInPast.isSatisfied(null, cal, null, null));

        isInPast.setTolerance(1000);
        assertFalse(isInPast.isSatisfied(null, cal, null, null));

        isInPast.setTolerance(5000);
        assertTrue(isInPast.isSatisfied(null, cal, null, null));
    }
}
