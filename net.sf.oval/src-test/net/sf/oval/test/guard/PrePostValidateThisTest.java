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
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;
import net.sf.oval.guard.PreValidateThis;

/**
 * @author Sebastian Thomschke
 */
public class PrePostValidateThisTest extends TestCase
{
	@Guarded(applyFieldConstraintsToSetters = true, checkInvariants = false)
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

		public void setName(String name)
		{
			this.name = name;
		}

		@PostValidateThis
		public void setNamePost(String name)
		{
			this.name = name;
		}
	}

	public void testConstructorValidation()
	{
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

	public void testMethodValidationInProbeMode()
	{
		final TestEntity t = new TestEntity();

		TestGuardAspect.aspectOf().getGuard().setInProbeMode(t, true);

		final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestGuardAspect.aspectOf().getGuard().addListener(va, t);

		// test non-getter precondition failed
		t.getName();
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
		va.clear();

		t.setName(null);
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
		va.clear();

		// test post-condition ignored even if pre-conditions satisfied
		t.setNamePost(null);
		assertTrue(va.getConstraintsViolatedExceptions().size() == 0);

		// test setter 
		t.setName("the name");
		assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		assertTrue(va.getConstraintViolations().size() == 0);

		// test getter returns null because we are in probe mode
		t.name = "the name";
		assertNull(t.getName());
		assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		assertTrue(va.getConstraintViolations().size() == 0);
	}

	public void testMethodValidation()
	{
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
			assertTrue(violations[0].getContext() instanceof MethodParameterContext);
		}

		t.setName("the name");
		assertNotNull(t.getName());
	}
}
