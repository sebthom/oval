/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 *
 */
public class OverridingEqualsTest extends TestCase
{
	@Guarded
	public static class Entity
	{
		protected int foo;

		@Override
		public boolean equals(final Object o)
		{
			final boolean retVal;
			if (o == null)
			{
				retVal = false;
			}
			else if (o instanceof Entity)
			{
				retVal = ((Entity) o).foo == foo;
			}
			else
			{
				retVal = false;
			}
			return retVal;
		}
	}

	public void testGuarding()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		final Entity a1 = new Entity();
		a1.foo = 2;
		final Entity a2 = new Entity();
		a2.foo = 2;

		assertEquals(a1, a2);
	}
}
