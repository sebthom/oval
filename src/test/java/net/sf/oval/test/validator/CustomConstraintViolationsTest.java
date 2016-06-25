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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.AbstractCheck;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class CustomConstraintViolationsTest extends TestCase {
    public static class CustomCheck extends AbstractCheck {
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         */
        public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
            final Entity entity = (Entity) validatedObject;
            if (entity.message == null)
                validator.reportConstraintViolation(new ConstraintViolation(this, "message cannot be null", validatedObject, null, new FieldContext(
                    Entity.class, "message")));

            if (entity.name == null)
                validator.reportConstraintViolation(new ConstraintViolation(this, "name cannot be null", validatedObject, null, new FieldContext(Entity.class,
                    "name")));

            return true;
        }
    }

    public final class Entity {
        String name;
        String message;
    }

    public void testMessages() {
        final Validator val = new Validator();
        val.addChecks(Entity.class, new CustomCheck());
        final List<ConstraintViolation> violations = val.validate(new Entity());
        assertEquals(2, violations.size());
    }
}
