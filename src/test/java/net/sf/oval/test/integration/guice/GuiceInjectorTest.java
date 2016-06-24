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
package net.sf.oval.test.integration.guice;

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
import net.sf.oval.integration.guice.GuiceCheckInitializationListener;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
* @author Sebastian Thomschke
*/
public class GuiceInjectorTest extends TestCase
{
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@Constraint(checkWith = GuiceNullContraintCheck.class)
	public @interface GuiceNullContraint
	{
		//nothing
	}

	public static class Entity
	{
		@GuiceNullContraint
		protected String field;
	}

	/**
	 * constraint check implementation requiring Guice injected members
	 */
	public static class GuiceNullContraintCheck extends AbstractAnnotationCheck<GuiceNullContraint>
	{
		private static final long serialVersionUID = 1L;

		@Inject
		@Named("GUICE_MANAGED_OBJECT")
		private Integer guiceManagedObject;

		public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
				final Validator validator) throws OValException
		{
			return guiceManagedObject == 10 && valueToValidate != null;
		}
	}

	public void testWithGuiceInjector()
	{
		final Injector injector = Guice.createInjector(new Module()
			{
				public void configure(final Binder binder)
				{
					binder.bind(Integer.class).annotatedWith(Names.named("GUICE_MANAGED_OBJECT")).toInstance(10);
				}
			});

		final AnnotationsConfigurer myConfigurer = new AnnotationsConfigurer();
		myConfigurer.addCheckInitializationListener(new GuiceCheckInitializationListener(injector));
		final Validator v = new Validator(myConfigurer);

		final Entity e = new Entity();
		assertEquals(1, v.validate(e).size());
		e.field = "whatever";
		assertEquals(0, v.validate(e).size());
	}

	public void testWithoutGuiceInjector()
	{
		final Validator v = new Validator();
		final Entity e = new Entity();
		try
		{
			v.validate(e);
			fail("NPE expected.");
		}
		catch (final NullPointerException ex)
		{}
	}

}
