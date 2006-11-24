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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public class ValidateClassWithoutConstraintsTest extends TestCase
{
	protected static class TestEntity
	{
		protected String name;

		private TestEntity(final String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	public void testValidateClassWithoutConstraints()
	{
		final TestEntity e = new TestEntity(null);

		final Validator v = new Validator();
		List<ConstraintViolation> violations = v.validate(e);
		assertEquals(0, violations.size());
	}
}
