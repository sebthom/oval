/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
import java.util.Date;

import net.sf.oval.constraint.DateRangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class DateRangeTest extends AbstractContraintsTest
{
	public void testDateRange() throws InterruptedException
	{
		final DateRangeCheck check = new DateRangeCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));
		check.setFormat("yyyy-MM-dd HH:mm:ss");

		check.setMin("2000-03-03 09:09:09");
		assertFalse(check.isSatisfied(null, "2000-03-03 09:09:08", null, null));
		assertTrue(check.isSatisfied(null, "2000-03-03 09:09:09", null, null));
		assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));
		assertTrue(check.isSatisfied(null, "2900-03-03 09:09:09", null, null));

		check.setMax("2080-08-08 10:10:10");
		assertTrue(check.isSatisfied(null, "2080-08-08 10:10:09", null, null));
		assertTrue(check.isSatisfied(null, "2080-08-08 10:10:10", null, null));
		assertFalse(check.isSatisfied(null, "2080-08-08 10:10:11", null, null));

		final Calendar cal = Calendar.getInstance();
		assertTrue(check.isSatisfied(null, cal, null, null));
		assertTrue(check.isSatisfied(null, cal.getTime(), null, null));
		assertTrue(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

		cal.add(Calendar.YEAR, -100);
		assertFalse(check.isSatisfied(null, cal, null, null));
		assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
		assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

		cal.add(Calendar.YEAR, 200);
		assertFalse(check.isSatisfied(null, cal, null, null));
		assertFalse(check.isSatisfied(null, cal.getTime(), null, null));
		assertFalse(check.isSatisfied(null, DateFormat.getDateTimeInstance().format(cal.getTime()), null, null));

		assertFalse(check.isSatisfied(null, "bla", null, null));
	}

	public void testLiterals()
	{
		final DateRangeCheck check = new DateRangeCheck();
		check.setFormat("yyyy-MM-dd HH:mm:ss");

		check.setMin("now");
		assertTrue(check.isSatisfied(null, new Date(), null, null));
		check.setMin("today");
		assertTrue(check.isSatisfied(null, new Date(), null, null));
		check.setMin("tomorrow");
		assertFalse(check.isSatisfied(null, new Date(), null, null));
		check.setMin("yesterday");
		assertTrue(check.isSatisfied(null, new Date(), null, null));
		check.setMin("");

		check.setMax("now");
		assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() + 4000), null, null));
		assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() - 4000), null, null));
		check.setMax("today");
		assertTrue(check.isSatisfied(null, new Date(), null, null));
		assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));
		check.setMax("tomorrow");
		assertTrue(check.isSatisfied(null, new Date(), null, null));
		assertTrue(check.isSatisfied(null, "2000-03-03 09:09:10", null, null));
		check.setMax("yesterday");
		assertFalse(check.isSatisfied(null, new Date(), null, null));
		check.setMax("");
	}

	public void testTolerance()
	{
		final DateRangeCheck check = new DateRangeCheck();
		check.setMax("now");
		check.setTolerance(4000);
		assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
		check.setTolerance(0);
		assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
		check.setMax("");

		check.setMax("now");
		check.setTolerance(4000);
		assertTrue(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
		check.setTolerance(0);
		assertFalse(check.isSatisfied(null, new Date(System.currentTimeMillis() + 2000), null, null));
		check.setMax("");
	}
}
