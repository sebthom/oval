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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.ConstraintsViolatedAdapter;
import net.sf.oval.ConstraintsEnforcer.Mode;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.PostValidateThis;
import net.sf.oval.annotations.PreValidateThis;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class PrePostValidateThisTest extends TestCase
{
	@Constrained(applyFieldConstraintsToSetter = false)
	public static class TestEntity
	{
		@NotNull
		public String name;

		public TestEntity()
		{}

		@PostValidateThis
		public TestEntity(String name)
		{
			this.name = name;
		}

		@PreValidateThis
		public String getName()
		{
			return name;
		}

		@PostValidateThis
		public void setName(String name)
		{
			this.name = name;
		}
	}

	public void testConstructorValidationInNotifyListenersMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.NOTIFY_LISTENERS, TestEntity.class);

		try
		{
			new TestEntity(null);

			// even in silent mode an exception should be thrown to prevent the returning of a reference to that new object instance
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		try
		{
			new TestEntity("test");
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}
	}

	public void testConstructorValidationInThrowExceptionMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.THROW_EXCEPTION, TestEntity.class);

		try
		{
			new TestEntity(null);

			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		try
		{
			new TestEntity("test");
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}
	}

	public void testMethodValidationInNotifyListenersMode()
	{
		final TestEntity t = new TestEntity();

		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.NOTIFY_LISTENERS, t);

		final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestEnforcerAspect.constraintsEnforcer.addListener(va, t);

		try
		{
			t.getName();
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getCheck() instanceof NotNullCheck);
			va.clear();

			t.setName(null);
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getCheck() instanceof NotNullCheck);
			va.clear();

			t.setName("the name");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
			assertTrue(va.getConstraintViolations().size() == 0);

			assertNotNull(t.getName());
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
			assertTrue(va.getConstraintViolations().size() == 0);
		}
		catch (ConstraintsViolatedException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testMethodValidationInThrowExceptionMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.THROW_EXCEPTION, TestEntity.class);

		final TestEntity t = new TestEntity();

		try
		{
			t.getName();
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length > 0);
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		t.setName("test");

		try
		{
			t.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length > 0);
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}
		try
		{
			t.setName("the name");
			assertNotNull(t.getName());
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}
	}
}
