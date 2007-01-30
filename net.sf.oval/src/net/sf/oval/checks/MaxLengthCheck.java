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

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.constraints.MaxLength;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MaxLengthCheck extends AbstractAnnotationCheck<MaxLength>
{
	private static final long serialVersionUID = 1L;

	private int max;

	@Override
	public void configure(final MaxLength constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.value());
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
		return new String[]{Integer.toString(max)};
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		final int len = value.toString().length();
		return len <= max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(final int max)
	{
		this.max = max;
	}
}
