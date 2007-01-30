/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
package net.sf.oval.checks;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.constraints.Future;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class FutureCheck extends AbstractAnnotationCheck<Future>
{
	private static final long serialVersionUID = 1L;

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		// check if the value is a Date
		if (value instanceof Date)
		{
			return ((Date) value).after(new Date());
		}

		// check if the value is a Calendar
		if (value instanceof Calendar)
		{
			return ((Calendar) value).after(Calendar.getInstance());
		}

		// see if we can extract a date based on the object's String representation
		final String stringValue = value.toString();
		try
		{
			Date date = DateFormat.getDateTimeInstance().parse(stringValue);
			return date.after(new Date());
		}
		catch (ParseException ex)
		{
			return false;
		}
	}
}
