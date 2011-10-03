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
package net.sf.oval.test.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.oval.constraint.MinSizeCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinSizeTest extends AbstractContraintsTest
{
	public void testMinSize()
	{
		final MinSizeCheck check = new MinSizeCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMin(2);
		assertEquals(2, check.getMin());

		assertFalse(check.isSatisfied(null, new Object[0], null, null));
		assertFalse(check.isSatisfied(null, new Object[1], null, null));
		assertTrue(check.isSatisfied(null, new Object[2], null, null));
		assertTrue(check.isSatisfied(null, new Object[3], null, null));

		List<Object> list = new ArrayList<Object>();
		assertFalse(check.isSatisfied(null, list, null, null));
		list.add(1);
		assertFalse(check.isSatisfied(null, list, null, null));
		list.add(2);
		assertTrue(check.isSatisfied(null, list, null, null));
		list.add(3);
		assertTrue(check.isSatisfied(null, list, null, null));

		Set<Object> set = new HashSet<Object>();
		assertFalse(check.isSatisfied(null, set, null, null));
		set.add(1);
		assertFalse(check.isSatisfied(null, set, null, null));
		set.add(2);
		assertTrue(check.isSatisfied(null, set, null, null));
		set.add(3);
		assertTrue(check.isSatisfied(null, set, null, null));

		Map<Object, Object> map = new HashMap<Object, Object>();
		assertFalse(check.isSatisfied(null, map, null, null));
		map.put(1, 1);
		assertFalse(check.isSatisfied(null, map, null, null));
		map.put(2, 2);
		assertTrue(check.isSatisfied(null, map, null, null));
		map.put(3, 3);
		assertTrue(check.isSatisfied(null, map, null, null));

		assertFalse(check.isSatisfied(null, "bla", null, null));
	}
}
