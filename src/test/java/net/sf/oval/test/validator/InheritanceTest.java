/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class InheritanceTest extends TestCase
{
	public abstract static class AbstractEntity
	{
		@NotNull(message = "NOT_NULL")
		private String name;

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	public static class EntityImpl extends AbstractEntity
	{
		// do nothing
	}

	public void testInheritance()
	{
		final Validator validator = new Validator();

		final AbstractEntity e = new EntityImpl();

		final List<ConstraintViolation> violations = validator.validate(e);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
	}
}
