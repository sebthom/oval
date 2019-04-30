/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.integration.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import net.sf.oval.integration.spring.SpringCheckInitializationListener;

/**
 * @author Sebastian Thomschke
 */
public class SpringInjectorTest extends TestCase {
   public static class Entity {
      @SpringNullContraint
      protected String field;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   @Constraint(checkWith = SpringNullContraintCheck.class)
   public @interface SpringNullContraint {
      //nothing
   }

   /**
    * constraint check implementation requiring Spring managed beans
    */
   public static class SpringNullContraintCheck extends AbstractAnnotationCheck<SpringNullContraint> {
      private static final long serialVersionUID = 1L;

      @Autowired
      @Qualifier("SPRING_MANAGED_BEAN")
      private Integer springManagedBean;

      @Override
      public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
         throws OValException {
         return springManagedBean == 10 && valueToValidate != null;
      }
   }

   public void testWithoutSpringInjector() {
      final Validator v = new Validator();
      final Entity e = new Entity();
      try {
         v.validate(e);
         fail("NPE expected.");
      } catch (final NullPointerException ex) {
         // expected
      }
   }

   public void testWithSpringInjector() {
      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringInjectorTest.xml", SpringInjectorTest.class);
      try {
         final AnnotationsConfigurer myConfigurer = new AnnotationsConfigurer();
         myConfigurer.addCheckInitializationListener(SpringCheckInitializationListener.INSTANCE);
         final Validator v = new Validator(myConfigurer);

         final Entity e = new Entity();
         assertEquals(1, v.validate(e).size());
         e.field = "whatever";
         assertEquals(0, v.validate(e).size());
      } finally {
         ctx.close();
      }
   }
}
