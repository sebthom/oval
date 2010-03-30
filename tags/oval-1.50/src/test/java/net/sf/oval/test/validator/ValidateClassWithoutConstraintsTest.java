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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.xml.XMLConfigurer;

/**
 * @author Sebastian Thomschke
 */
public class ValidateClassWithoutConstraintsTest extends TestCase
{
	protected static class TestEntity
	{
		protected String name;

		protected TestEntity(final String name)
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

	public void testClassWithoutConstraints()
	{
		final TestEntity e = new TestEntity(null);

		final Validator v = new Validator();
		final List<ConstraintViolation> violations = v.validate(e);
		assertEquals(0, violations.size());
	}

	public void testEmptyXmlConfigurer()
	{
		final XMLConfigurer xmlConfigurer = new XMLConfigurer();
		final Validator v = new Validator(xmlConfigurer);

		final TestEntity e = new TestEntity(null);

		final List<ConstraintViolation> violations = v.validate(e);
		assertEquals(0, violations.size());
	}
}
