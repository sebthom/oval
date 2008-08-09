/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.ConstraintSet;
import net.sf.oval.constraint.AssertConstraintSet;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.NotEmptyCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetTest extends TestCase
{
	@Guarded
	protected static class Person
	{
		private String zipCode;

		public String getZipCode()
		{
			return zipCode;
		}

		public void setZipCode(@AssertConstraintSet(id = "zipCode") final String zipCode)
		{
			this.zipCode = zipCode;
		}
	}

	public void testConstraintSetValidation()
	{
		final ConstraintSet constraintSet = new ConstraintSet("zipCode");
		final List<Check> checks = new ArrayList<Check>();
		constraintSet.setChecks(checks);

		final NotNullCheck notNull = new NotNullCheck();
		notNull.setMessage("NOT_NULL");
		checks.add(notNull);

		final LengthCheck length = new LengthCheck();
		length.setMessage("LENGTH");
		length.setMax(6);
		checks.add(length);

		final NotEmptyCheck notEmpty = new NotEmptyCheck();
		notEmpty.setMessage("NOT_EMPTY");
		checks.add(notEmpty);

		final MatchPatternCheck matchPattern = new MatchPatternCheck();
		matchPattern.setMessage("MATCH_PATTERN");
		matchPattern.setPattern("^[0-9]*$", 0);
		checks.add(matchPattern);

		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		guard.addConstraintSet(constraintSet, false);

		{
			final Person p = new Person();

			TestGuardAspect.aspectOf().getGuard().setInProbeMode(p, true);

			final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
			TestGuardAspect.aspectOf().getGuard().addListener(va, p);

			// test @Length(max=)
			p.setZipCode("1234567");
			assertEquals(va.getConstraintsViolatedExceptions().size(), 1);
			assertEquals(va.getConstraintViolations().size(), 1);
			assertEquals(va.getConstraintViolations().get(0).getMessage(), "LENGTH");
			va.clear();

			// test @NotEmpty
			p.setZipCode("");
			assertEquals(va.getConstraintsViolatedExceptions().size(), 1);
			assertEquals(va.getConstraintViolations().size(), 1);
			assertEquals(va.getConstraintViolations().get(0).getMessage(), "NOT_EMPTY");
			va.clear();

			// test @MatchPattern
			p.setZipCode("dffd34");
			assertEquals(va.getConstraintsViolatedExceptions().size(), 1);
			assertEquals(va.getConstraintViolations().size(), 1);
			assertEquals(va.getConstraintViolations().get(0).getMessage(), "MATCH_PATTERN");
			va.clear();
		}

	}
}
