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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;
import net.sf.oval.guard.PreValidateThis;

/**
 * @author Sebastian Thomschke
 */
public class PrePostValidateThisTest extends TestCase
{
	@Guarded(applyFieldConstraintsToSetter = false)
	public static class TestEntity
	{
		@NotNull(message = "NOT_NULL")
		public String name;

		public TestEntity()
		{
			// do nothing
		}

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

	public void testConstructorValidationInSwallowExceptionMode()
	{
		TestGuardAspect.guard.setSuppressPreConditionExceptions(TestEntity.class, true);

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
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		new TestEntity("test");
	}

	public void testConstructorValidationInThrowExceptionMode()
	{
		TestGuardAspect.guard.setSuppressPreConditionExceptions(TestEntity.class, false);

		try
		{
			new TestEntity(null);

			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		new TestEntity("test");
	}

	public void testMethodValidationInSwallowExceptionMode()
	{
		final TestEntity t = new TestEntity();

		TestGuardAspect.guard.setSuppressPreConditionExceptions(t, true);

		final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestGuardAspect.guard.addListener(va, t);

		// don't swallow for non-getter methods
		try
		{
			t.getName();
			fail("Should throw exception");
		}
		catch (ConstraintsViolatedException ex)
		{
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
			va.clear();
		}

		// don't swallow post condition exceptions for setter
		try
		{
			t.setName(null);
			fail("Should throw exception");
		}
		catch (ConstraintsViolatedException ex)
		{
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
			va.clear();
		}

		t.setName("the name");
		assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		assertTrue(va.getConstraintViolations().size() == 0);

		assertNotNull(t.getName());
		assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		assertTrue(va.getConstraintViolations().size() == 0);
	}

	public void testMethodValidationInThrowExceptionMode()
	{
		TestGuardAspect.guard.setSuppressPreConditionExceptions(TestEntity.class, false);

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
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
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
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			assertTrue(violations[0].getContext() instanceof FieldContext);
		}

		t.setName("the name");
		assertNotNull(t.getName());
	}
}
