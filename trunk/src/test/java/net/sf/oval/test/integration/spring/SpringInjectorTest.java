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
package net.sf.oval.test.integration.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import net.sf.oval.integration.spring.BeanInjectingCheckInitializationListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
* @author Sebastian Thomschke
*/
public class SpringInjectorTest extends TestCase
{
	public static class Entity
	{
		@SpringNullContraint
		private String field;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@Constraint(checkWith = SpringNullContraintCheck.class)
	public @interface SpringNullContraint
	{
		//nothing
	}

	public static class SpringNullContraintCheck extends AbstractAnnotationCheck<SpringNullContraint>
	{
		private static final long serialVersionUID = 1L;

		@Autowired()
		private Object springConstraintBean;

		public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
				final OValContext context, final Validator validator) throws OValException
		{
			return springConstraintBean != null && valueToValidate != null;
		}
	}

	public void testSpringInjector()
	{
		@SuppressWarnings("unused")
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringInjectorTest.xml",
				SpringInjectorTest.class);
		final AnnotationsConfigurer myConfigurer = new AnnotationsConfigurer();
		myConfigurer.addCheckInitializationListener(BeanInjectingCheckInitializationListener.INSTANCE);
		final Validator v = new Validator(myConfigurer);

		final Entity e = new Entity();
		assertEquals(1, v.validate(e).size());
		e.field = "whatever";
		assertEquals(0, v.validate(e).size());
	}
}
