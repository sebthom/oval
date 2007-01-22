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
import net.sf.oval.constraints.NotNull;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;

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
			@PostValidateThis
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

		@Guarded(applyFieldConstraintsToSetter = true)
		protected static class InnerClassGuarded
		{
			@NotNull
			protected String name;

			@PostValidateThis
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
		TestEntity.InnerClassNotGuarded instance = new TestEntity.InnerClassNotGuarded(null);
		instance.setName(null);
	}

	public void testInnerClassGuarded()
	{
		try
		{
			new TestEntity.InnerClassGuarded(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}

		TestEntity.InnerClassGuarded instance = null;

		instance = new TestEntity.InnerClassGuarded("");

		try
		{
			instance.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}
	}
}
