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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.InstanceOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfTest extends AbstractContraintsTest
{
	public void testInstanceOf()
	{
		final InstanceOfCheck check = new InstanceOfCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setType(InstanceOfTest.class);
		assertEquals(InstanceOfTest.class, check.getType());

		assertTrue(check.isSatisfied(null, this, null, null));
		assertFalse(check.isSatisfied(null, "bla", null, null));
	}
}
