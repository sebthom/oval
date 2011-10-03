/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.MinLength;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.ValidationFailedException;

/**
 * @author Sebastian Thomschke
 */
public class TargetDefaultTest extends TestCase
{
	public static class Level1
	{
		@MinSize(value = 4, target = "level3.array", message = "LEVEL3_ARRAY_TOO_SMALL")
		@MinLength(value = 4, appliesTo = ConstraintTarget.VALUES, target = "level3.array", message = "LEVEL3_ARRAY_ITEM_TOO_SMALL")
		@NotNull(target = "level3.name", message = "LEVEL3_NAME_IS_NULL")
		protected Level2 level2a;

		// illegal path, results in an InvalidConfigurationException		
		@NotNull(target = "level3.foobar")
		protected Level2 level2b;
	}

	public static class Level2
	{
		protected Level3 level3;
	}

	public static class Level3
	{
		protected String name;

		protected String[] array;
	}

	public void testTarget()
	{
		final Validator v = new Validator();
		List<ConstraintViolation> violations;

		final Level1 lv1 = new Level1();
		assertEquals(0, v.validate(lv1).size());

		lv1.level2a = new Level2();
		lv1.level2b = new Level2();
		assertEquals(0, v.validate(lv1).size());

		lv1.level2a.level3 = new Level3();
		violations = v.validate(lv1);
		assertEquals(1, violations.size());
		lv1.level2a.level3.name = "foo";

		lv1.level2a.level3.array = new String[]{};
		violations = v.validate(lv1);
		assertEquals(1, violations.size());
		ConstraintViolation violation = violations.get(0);
		assertTrue(violation.getContext() instanceof FieldContext);
		assertEquals("LEVEL3_ARRAY_TOO_SMALL", violation.getMessage());

		lv1.level2a.level3.array = new String[]{"123", "1234", "1234", "1234"};
		violations = v.validate(lv1);
		assertEquals(1, violations.size());
		violation = violations.get(0);
		assertTrue(violation.getContext() instanceof FieldContext);
		assertEquals("LEVEL3_ARRAY_ITEM_TOO_SMALL", violation.getMessage());

		try
		{
			lv1.level2b.level3 = new Level3();
			v.validate(lv1);
			fail();
		}
		catch (final ValidationFailedException ex)
		{
			assertTrue(ex.getCause() instanceof InvalidConfigurationException);
		}
	}
}
