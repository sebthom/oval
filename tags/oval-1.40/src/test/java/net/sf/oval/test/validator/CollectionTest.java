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
package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MaxSize;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class CollectionTest extends TestCase
{
	public class Group
	{
		@MinSize(value = 1, message = "MIN_SIZE")
		@MaxSize(value = 4, message = "MAX_SIZE")
		@Length(min = 1, max = 7, message = "LENGTH")
		@NotNull(appliesTo = {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES}, message = "NOT_NULL")
		public List<String> members = new ArrayList<String>();

	}

	public void testCollection()
	{
		Validator validator = new Validator();
		Group group = new Group();

		// test min size
		List<ConstraintViolation> violations = validator.validate(group);
		assertEquals(1, violations.size());
		assertEquals("MIN_SIZE", violations.get(0).getMessage());

		// test valid
		group.members.add("member1");
		violations = validator.validate(group);
		assertEquals(0, violations.size());

		// test max size
		group.members.add("member2");
		group.members.add("member3");
		group.members.add("member4");
		group.members.add("member5");
		violations = validator.validate(group);
		assertEquals(1, violations.size());
		assertEquals("MAX_SIZE", violations.get(0).getMessage());

		// test attribute not null
		group.members = null;
		violations = validator.validate(group);
		assertEquals(1, violations.size());
		assertEquals("NOT_NULL", violations.get(0).getMessage());
		
		// test elements not null
		group.members = new ArrayList<String>();
		group.members.add(null);
		violations = validator.validate(group);
		assertEquals(1, violations.size());
		assertEquals("NOT_NULL", violations.get(0).getMessage());
		
		// test elements length
		group.members = new ArrayList<String>();
		group.members.add("");
		group.members.add("123456789");
		violations = validator.validate(group);
		assertEquals(2, violations.size());
		assertEquals("LENGTH", violations.get(0).getMessage());
		assertEquals("LENGTH", violations.get(1).getMessage());
	}
}
