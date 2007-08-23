/**
 * 
 */
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.ConstraintsViolatedException;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

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
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
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
