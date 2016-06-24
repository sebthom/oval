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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.InstanceOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfTest extends AbstractContraintsTest
{
	public static class ClassA implements InterfaceA
	{
		//
	}

	public static class ClassB implements InterfaceA, InterfaceB
	{
		//
	}

	public interface InterfaceA
	{
		//
	}

	public interface InterfaceB
	{
		//
	}

	public void testInstanceOf()
	{
		final InstanceOfCheck check = new InstanceOfCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setTypes(InterfaceA.class);
		assertEquals(InterfaceA.class, check.getTypes()[0]);

		assertTrue(check.isSatisfied(null, new ClassA(), null, null));
		assertTrue(check.isSatisfied(null, new ClassB(), null, null));
		assertFalse(check.isSatisfied(null, "bla", null, null));

		check.setTypes(new Class< ? >[]{InterfaceA.class, InterfaceB.class});
		assertEquals(InterfaceA.class, check.getTypes()[0]);
		assertEquals(InterfaceB.class, check.getTypes()[1]);

		assertFalse(check.isSatisfied(null, new ClassA(), null, null));
		assertTrue(check.isSatisfied(null, new ClassB(), null, null));
		assertFalse(check.isSatisfied(null, "bla", null, null));
	}
}
