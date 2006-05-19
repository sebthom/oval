/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.constraints;

import net.sf.oval.AbstractCheck;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.7 $
 */
public class LengthCheck extends AbstractCheck<Length>
{
	private int min;
	private int max;

	@Override
	public void configure(final Length constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.max());
		setMin(constraintAnnotation.min());
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{Integer.toString(min), Integer.toString(max)};
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value)
	{
		if (value == null) return true;

		final int len = value.toString().length();
		return len >= min && len <= max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(final int max)
	{
		this.max = max;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final int min)
	{
		this.min = min;
	}

}
