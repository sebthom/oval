/**
 * 
 */
package net.sf.oval.test.guard;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import junit.framework.TestCase;

/**
 * @author Sebastian Thomschke
 *
 */
public class OverridingHashCodeTest extends TestCase
{
	@Guarded
	public class Entity
	{
		@Override
		public int hashCode()
		{
			return super.hashCode();
		}

		public void setFoo(@NotNull
		String s)
		{
		//
		}
	}

	public void testGuarding()
	{
		try
		{
			new Entity().setFoo(null);
			fail("Violation expected");
		}
		catch (ConstraintsViolatedException e)
		{
			// expected
		}
	}
}
