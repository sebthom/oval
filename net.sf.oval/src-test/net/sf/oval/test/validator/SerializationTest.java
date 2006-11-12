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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraints.Length;

/**
 * @author Sebastian Thomschke
 */
public class SerializationTest extends TestCase
{
	private static class Person implements Serializable
	{
		private static final long serialVersionUID = 1L;

		@Length(max = 5)
		public String firstName;
	}

	@SuppressWarnings("unchecked")
	public void testSerialization() throws IOException, ClassNotFoundException
	{
		final Validator validator = new Validator();

		final Person p = new Person();
		p.firstName = "123456";
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 1);

		// serialize the violations
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(violations);
		oos.flush();
		byte[] bytes = bos.toByteArray();

		// deserialize the violations
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		assertTrue(ois.readObject() instanceof List);
	}
}
