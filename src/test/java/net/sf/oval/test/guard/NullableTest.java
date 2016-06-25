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
import net.sf.oval.constraint.exclusion.Nullable;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class NullableTest extends TestCase {
    @Guarded(assertParametersNotNull = true)
    protected static class TestEntity1 {
        protected TestEntity1(final String param1, @Nullable final String param2) {
            // nothing
        }

        public void setParam1(final String param1) {
            // nothing
        }

        public void setParam2(@Nullable final String param2) {
            // nothing
        }
    }

    // assertParametersNotNull is false by default
    @Guarded
    protected static class TestEntity2 {
        protected TestEntity2(final String param1, @Nullable final String param2) {
            // nothing
        }

        public void setParam1(final String param1) {
            // nothing
        }

        public void setParam2(@Nullable final String param2) {
            // nothing
        }
    }

    @SuppressWarnings("unused")
    public void testNullable1() {
        try {
            new TestEntity1(null, "foo");
            fail("ConstraintsViolatedException expected");
        } catch (final ConstraintsViolatedException ex) {
            // nothing
        }

        final TestEntity1 t = new TestEntity1("foo", null);

        try {
            t.setParam1(null);
        } catch (final ConstraintsViolatedException ex) {
            // nothing
        }
        t.setParam2(null);
    }

    @SuppressWarnings("unused")
    public void testNullable2() {
        new TestEntity2(null, "foo");
        final TestEntity2 t = new TestEntity2("foo", null);
        t.setParam1(null);
        t.setParam2(null);
    }
}
