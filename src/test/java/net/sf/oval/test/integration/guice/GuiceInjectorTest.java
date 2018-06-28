/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.integration.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import net.sf.oval.integration.guice.GuiceCheckInitializationListener;

/**
 * @author Sebastian Thomschke
 */
public class GuiceInjectorTest extends TestCase {
   public static class Entity {
      @GuiceNullContraint
      protected String field;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   @Constraint(checkWith = GuiceNullContraintCheck.class)
   public @interface GuiceNullContraint {
      //nothing
   }

   /**
    * constraint check implementation requiring Guice injected members
    */
   public static class GuiceNullContraintCheck extends AbstractAnnotationCheck<GuiceNullContraint> {
      private static final long serialVersionUID = 1L;

      @Inject
      @Named("GUICE_MANAGED_OBJECT")
      private Integer guiceManagedObject;

      @Override
      public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
         throws OValException {
         return guiceManagedObject == 10 && valueToValidate != null;
      }
   }

   public void testWithGuiceInjector() {
      final Injector injector = Guice.createInjector(new Module() {
         @Override
         public void configure(final Binder binder) {
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

   public void testWithoutGuiceInjector() {
      final Validator v = new Validator();
      final Entity e = new Entity();
      try {
         v.validate(e);
         fail("NPE expected.");
      } catch (final NullPointerException ex) {
         // expected
      }
   }

}
