/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.oval.localization.value;

/**
 * @author Sebastian Thomschke
 */
public class ToStringMessageValueFormatter implements MessageValueFormatter
{
	public static final ToStringMessageValueFormatter INSTANCE = new ToStringMessageValueFormatter();

	/**
	 * {@inheritDoc}
	 */
	public String format(final Object value)
	{
		if (value == null) return "null";
		return value.toString();
	}
}