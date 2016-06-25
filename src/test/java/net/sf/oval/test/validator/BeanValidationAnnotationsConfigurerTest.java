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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 *
 */
public class BeanValidationAnnotationsConfigurerTest extends TestCase {
    @Entity
    protected static class TestEntity {
        @NotNull(message = "NOT_NULL")
        @Size(max = 4)
        public String code;

        public String description;

        @NotNull(message = "NOT_NULL")
        @Valid
        public TestEntity ref1;

        @Valid
        public TestEntity ref2;

        @Valid
        public Collection<TestEntity> refs;

        @NotNull(message = "NOT_NULL")
        public String getDescription() {
            return description;
        }
    }

    public void testBeanValidationAnnotationsConfigurer() {
        final Validator v = new Validator(new BeanValidationAnnotationsConfigurer());
        List<ConstraintViolation> violations;

        TestEntity entity;

        {
            entity = new TestEntity();

            violations = v.validate(entity);
            // code is null
            // description is null
            // ref1 is null
            assertEquals(3, violations.size());
            assertNull(violations.get(0).getInvalidValue());
            assertNull(violations.get(1).getInvalidValue());
            assertNull(violations.get(2).getInvalidValue());
            assertEquals("NOT_NULL", violations.get(0).getMessage());
            assertEquals("NOT_NULL", violations.get(1).getMessage());
            assertEquals("NOT_NULL", violations.get(2).getMessage());
        }

        {
            entity.code = "";
            entity.description = "";
            entity.ref1 = new TestEntity();

            violations = v.validate(entity);
            // ref1 is invalid
            assertEquals(1, violations.size());
        }

        {
            entity.ref1.code = "";
            entity.ref1.description = "";
            entity.ref1.ref1 = entity;

            violations = v.validate(entity);
            assertEquals(0, violations.size());
        }

        {
            entity.ref2 = new TestEntity();

            violations = v.validate(entity);
            // ref2 is invalid
            assertEquals(1, violations.size());
        }

        {
            entity.ref2.code = "";
            entity.ref2.description = "";
            entity.ref2.ref1 = entity;

            violations = v.validate(entity);
            assertEquals(0, violations.size());
        }

        // Size test
        {
            entity.code = "12345";
            violations = v.validate(entity);
            // code is too long
            assertEquals(1, violations.size());

            entity.code = "";
        }

        // Valid test
        {
            entity.refs = new ArrayList<TestEntity>();
            final TestEntity d = new TestEntity();
            entity.refs.add(d);

            violations = v.validate(entity);
            assertEquals(1, violations.size());

            d.code = "";
            d.description = "";
            d.ref1 = entity;

            violations = v.validate(entity);
            assertEquals(0, violations.size());
        }
    }
}
