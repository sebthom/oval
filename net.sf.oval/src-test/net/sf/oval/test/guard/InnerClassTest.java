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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
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
		protected static class InnerClassNonConstrain
		{
			@NotNull
			protected String name;

			/**
			 * the @PostValidateObject annotation should lead to a warning by the ApiUsageAuditor
			 */
			@PostValidateThis
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

		@Guarded(applyFieldConstraintsToSetter = true)
		protected static class InnerClassConstrain
		{
			@NotNull
			protected String name;

			@PostValidateThis
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
		TestGuardAspect.guard
				.setReportingMode(Guard.ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION);

		TestEntity.InnerClassNonConstrain instance = new TestEntity.InnerClassNonConstrain(null);
		instance.setName(null);
	}

	public void testInnerClassConstrain()
	{
		TestGuardAspect.guard
				.setReportingMode(Guard.ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION);

		try
		{
			new TestEntity.InnerClassConstrain(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}

		TestEntity.InnerClassConstrain instance = null;

		instance = new TestEntity.InnerClassConstrain("");

		try
		{
			instance.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{}
	}
}
