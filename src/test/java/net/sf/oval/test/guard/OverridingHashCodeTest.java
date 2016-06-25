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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class OverridingHashCodeTest extends TestCase {
    @Guarded
    public static class Entity {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public void setFoo(@NotNull final String s) {
            //
        }
    }

    public void testGuarding() {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);
        try {
            new Entity().setFoo(null);
            fail("Violation expected");
        } catch (final ConstraintsViolatedException e) {
            // expected
        }
    }
}
