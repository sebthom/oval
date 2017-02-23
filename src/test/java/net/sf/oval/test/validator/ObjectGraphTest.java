/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ObjectGraphTest extends TestCase {
    protected static class ClassA {
        @AssertValid
        ClassB classB;

        @AssertValid
        ClassC classC;
    }

    protected static class ClassB {
        @AssertValid
        ClassC classC;
    }

    protected static class ClassC {
        @AssertValid
        ClassA classA;

        @NotNull
        String name;
    }

    public void testObjectGraph() {
        final ClassA classA = new ClassA();
        classA.classB = new ClassB();
        classA.classC = new ClassC();
        classA.classC.classA = classA;
        classA.classB.classC = classA.classC;

        final Validator validator = new Validator();
        final List<ConstraintViolation> violations = validator.validate(classA);
        assertEquals(1, violations.size());
    }

}
