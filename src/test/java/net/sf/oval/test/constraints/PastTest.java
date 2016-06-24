/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.test.constraints;

import java.text.DateFormat;
import java.util.Calendar;

import net.sf.oval.constraint.PastCheck;

/**
 * @author Sebastian Thomschke
 */
public class PastTest extends AbstractContraintsTest
{
	public void testPast()
	{
		final PastCheck check = new PastCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		final Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, -1);
		assertTrue(check.isSatisfied(null, cal, null, null));
		assertTrue(check.isSatisfied(null, cal.getTime(), null, null));
		assertTrue(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

		cal.roll(Calendar.YEAR, 2);
		assertFalse(check.isSatisfied(null, cal, null, null));
		assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
		assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

		assertFalse(check.isSatisfied(null, "bla", null, null));
	}

	public void testTolerance()
	{
		final PastCheck check = new PastCheck();

		final Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.SECOND, 3);
		assertFalse(check.isSatisfied(null, cal, null, null));
		check.setTolerance(1000);
		assertFalse(check.isSatisfied(null, cal, null, null));
		check.setTolerance(5000);
		assertTrue(check.isSatisfied(null, cal, null, null));
	}
}
