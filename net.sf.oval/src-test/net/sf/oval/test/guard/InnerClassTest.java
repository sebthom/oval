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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InnerClassTest extends TestCase
{

	@Guarded
	protected static class TestEntity
	{
		protected static class InnerClassNotGuarded
		{
			@NotNull
			protected String name;

			/**
			 * the @PostValidateObject annotation should lead to a warning by the ApiUsageAuditor
			 */
			private InnerClassNotGuarded(String name)
			{
				this.name = name;
			}

			/**
			 * @param name the name to set
			 */
			public void setName(String name)
			{
				this.name = name;
			}
		}

		@Guarded(applyFieldConstraintsToSetters = true)
		protected static class InnerClassGuarded
		{
			@NotNull
			protected String name;

			private InnerClassGuarded(String name)
			{
				this.name = name;
			}

			/**
			 * @param name the name to set
			 */
			public void setName(String name)
			{
				this.name = name;
			}
		}
	}

	/**
	 * test that specified constraints for inner classes not marked with @Constrained
	 * are ignored
	 */
	public void testInnerClassNotGuarded()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
		
		TestEntity.InnerClassNotGuarded instance = new TestEntity.InnerClassNotGuarded(null);
		instance.setName(null);
	}

	public void testInnerClassGuarded()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
		guard.setInvariantsEnabled(true);
		
		try
		{
			new TestEntity.InnerClassGuarded(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			// expected
		}

		TestEntity.InnerClassGuarded instance = null;

		instance = new TestEntity.InnerClassGuarded("");

		try
		{
			instance.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			// expected
		}
	}
}
