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
package net.sf.oval.test.enforcer;

import junit.framework.TestCase;
import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.PostValidateObject;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public class InnerClassTest extends TestCase
{

	@Constrained
	protected static class TestEntity
	{
		protected static class InnerClassNonConstrain
		{
			@NotNull
			protected String name;

			/**
			 * the @PostValidateObject annotation should lead to a warning by the ApiUsageAuditor
			 */
			@PostValidateObject
			private InnerClassNonConstrain(String name)
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

		@Constrained(applyFieldConstraintsToSetter = true)
		protected static class InnerClassConstrain
		{
			@NotNull
			protected String name;

			@PostValidateObject
			private InnerClassConstrain(String name)
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
	public void testInnerClassNonConstrain()
	{
		ConstraintsEnforcer.setMode(ConstraintsEnforcer.Mode.THROW_EXCEPTION);

		try
		{
			TestEntity.InnerClassNonConstrain instance = new TestEntity.InnerClassNonConstrain(null);
			instance.setName(null);
		}
		catch (ConstraintsViolatedException ex)
		{
			fail();
		}
	}

	public void testInnerClassConstrain()
	{
		ConstraintsEnforcer.setMode(ConstraintsEnforcer.Mode.THROW_EXCEPTION);

		try
		{
			new TestEntity.InnerClassConstrain(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}

		TestEntity.InnerClassConstrain instance = null;

		try
		{
			instance = new TestEntity.InnerClassConstrain("");
		}
		catch (ConstraintsViolatedException ex)
		{
			fail();
		}

		try
		{
			instance.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}
	}
}
