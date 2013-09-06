/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ChainedConstructorsTest extends TestCase
{
	@Guarded
	public static final class Entity
	{
		public Entity(@NotNull final Object param)
		{
			this(param.toString(), "whatever");
		}

		public Entity(@NotNull final String param1, @NotNull final String params2)
		{
			// do stuff
		}
	}

	@SuppressWarnings("unused")
	public void testConstructorChaining()
	{
		try
		{
			new Entity(null);
			fail();
		}
		catch (final Exception ex)
		{
			// TODO: currently fails with an NPE instead of a ConstraintViolationException https://sourceforge.net/p/oval/bugs/83/
		}
	}
}
