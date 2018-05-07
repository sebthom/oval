/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
import net.sf.oval.constraint.Length;

/**
 * @author Sebastian Thomschke
 */
public class SerializationTest extends TestCase {
    protected static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        @Length(max = 5)
        public String firstName;
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
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
