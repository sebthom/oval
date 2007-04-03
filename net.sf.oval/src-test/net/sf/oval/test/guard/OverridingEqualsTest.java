/**
 * 
 */
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 *
 */
public class OverridingEqualsTest extends TestCase
{
	@Guarded
	public class Entity
	{
		private int foo;

		@Override
		public boolean equals(Object o)
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
		Entity a1 = new Entity();
		a1.foo = 2;
		Entity a2 = new Entity();
		a2.foo = 2;

		assertEquals(a1, a2);
	}
}
