/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 *
 */
public class JPAAnnotationsConfigurerTest extends TestCase
{
	@Entity
	protected static class TestEntity
	{
		@Basic(optional = false)
		@Column(length = 4)
		public String property1;

		@Column(nullable = false)
		public String property2;

		@ManyToOne(optional = false)
		public TestEntity property3;

		@OneToOne(optional = false)
		public TestEntity property4;
	}

	public void testJPAAnnotationsConfigurer()
	{
		Validator v = new Validator(new JPAAnnotationsConfigurer());

		TestEntity entity = new TestEntity();

		List<ConstraintViolation> violations = v.validate(entity);
		assertEquals(4, violations.size());

		entity.property1 = "";
		entity.property2 = "";
		entity.property3 = new TestEntity();
		entity.property4 = new TestEntity();

		violations = v.validate(entity);
		assertEquals(0, violations.size());

		entity.property1 = "12345";
		violations = v.validate(entity);
		assertEquals(1, violations.size());
	}
}
