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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.EqualToFieldCheck;

/**
 * @author Sebastian Thomschke
 */
public class EqualToFieldTest extends AbstractContraintsTest
{
	public static class EnrichedEntity extends Entity
	{
		protected String password1;

		protected String password1Repeated;

		protected String password2Repeated;
	}

	public static class Entity
	{
		protected String password1 = "mug";
		protected String password2DifferentName;

		public String getPassword2()
		{
			return password2DifferentName;
		}

	}

	public void testEqualToField()
	{
		final EqualToFieldCheck check = new EqualToFieldCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		final EnrichedEntity entity = new EnrichedEntity();
		entity.password1 = "secret";
		entity.password1Repeated = "zecret";

		check.setFieldName("password1");
		check.setUseGetter(false);

		assertFalse(check.isSatisfied(entity, entity.password1Repeated, null, null));
		entity.password1Repeated = "secret";
		assertTrue(check.isSatisfied(entity, entity.password1Repeated, null, null));

		entity.password2DifferentName = "secret";
		entity.password2Repeated = "zecret";

		check.setFieldName("password2");
		check.setUseGetter(true);

		assertFalse(check.isSatisfied(entity, entity.password2Repeated, null, null));
		entity.password2Repeated = "secret";
		assertTrue(check.isSatisfied(entity, entity.password2Repeated, null, null));

	}
}
