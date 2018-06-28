/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.annotation;

import static net.sf.oval.Validator.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import net.sf.oval.Check;
import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.Range;
import net.sf.oval.constraint.RangeCheck;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Constraints configurer that interprets certain EJB3 JPA annotations:
 *
 * <pre>
 * * javax.persistence.Basic(optional=false)     => net.sf.oval.constraint.NotNullCheck
 * * javax.persistence.OneToOne(optional=false)  => net.sf.oval.constraint.NotNullCheck and
 *                                                  net.sf.oval.constraint.AssertValidCheck (if addAssertValidConstraints=true)
 * * javax.persistence.ManyToOne(optional=false) => net.sf.oval.constraint.NotNullCheck and
 *                                                  net.sf.oval.constraint.AssertValidCheck (if addAssertValidConstraints=true)
 * * javax.persistence.ManyToMany                => net.sf.oval.constraint.AssertValidCheck (if addAssertValidConstraints=true)
 * * javax.persistence.Column(nullable=false)    => net.sf.oval.constraint.NotNullCheck
 * * javax.persistence.Column(length=5)          => net.sf.oval.constraint.LengthCheck
 * * javax.persistence.Column(precision>0)       => net.sf.oval.constraint.RangeCheck (for Numbers only)
 * </pre>
 *
 * <b>Important:</b> by default AssertValidChecks are added for n-m relationships. This may be a problem when using lazy loading. Read
 * <a href="http://sourceforge.net/p/oval/discussion/488110/thread/6ec11584/#4ae0">this post</a> for more details.
 * To avoid this override the method {@link #addAssertValidCheckIfRequired(Annotation, Collection, AccessibleObject)} with an empty method body, for example
 *
 * <pre>
 * JPAAnnotationsConfigurer configurer = new JPAAnnotationsConfigurer() {
 *    protected void addAssertValidCheckIfRequired(Annotation constraintAnnotation, Collection&lt;Check&gt; checks, AccessibleObject fieldOrMethod) {
 *       // do nothing
 *    }
 * };
 * </pre>
 *
 * @author Sebastian Thomschke
 */
@SuppressWarnings("javadoc")
public class JPAAnnotationsConfigurer implements Configurer {
   protected Boolean applyFieldConstraintsToSetters;
   protected Boolean applyFieldConstraintsToConstructors;

   protected void addAssertValidCheckIfRequired(final Annotation constraintAnnotation, final Collection<Check> checks,
      @SuppressWarnings("unused") /*parameter for potential use by subclasses*/final AccessibleObject fieldOrMethod) {
      if (containsCheckOfType(checks, AssertValidCheck.class))
         return;

      if (constraintAnnotation instanceof OneToOne || constraintAnnotation instanceof OneToMany || constraintAnnotation instanceof ManyToOne
         || constraintAnnotation instanceof ManyToMany) {
         checks.add(new AssertValidCheck());
      }
   }

   protected boolean containsCheckOfType(final Collection<Check> checks, final Class<? extends Check> checkClass) {
      for (final Check check : checks)
         if (checkClass.isInstance(check))
            return true;
      return false;
   }

   public Boolean getApplyFieldConstraintsToConstructors() {
      return applyFieldConstraintsToConstructors;
   }

   @Override
   public ClassConfiguration getClassConfiguration(final Class<?> clazz) {
      final CollectionFactory cf = getCollectionFactory();

      final ClassConfiguration config = new ClassConfiguration();
      config.type = clazz;
      config.applyFieldConstraintsToConstructors = applyFieldConstraintsToConstructors;
      config.applyFieldConstraintsToSetters = applyFieldConstraintsToSetters;

      List<Check> checks = cf.createList(2);

      /*
       * determine field checks
       */
      for (final Field field : config.type.getDeclaredFields()) {

         // loop over all annotations of the current field
         for (final Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof Basic) {
               initializeChecks((Basic) annotation, checks);
            } else if (annotation instanceof Column) {
               initializeChecks((Column) annotation, checks, field);
            } else if (annotation instanceof OneToOne) {
               initializeChecks((OneToOne) annotation, checks);
            } else if (annotation instanceof ManyToOne) {
               initializeChecks((ManyToOne) annotation, checks);
            } else if (annotation instanceof ManyToMany) {
               initializeChecks((ManyToMany) annotation, checks);
            } else if (annotation instanceof OneToMany) {
               initializeChecks((OneToMany) annotation, checks);
            }

            addAssertValidCheckIfRequired(annotation, checks, field);
         }

         if (checks.size() > 0) {
            if (config.fieldConfigurations == null) {
               config.fieldConfigurations = cf.createSet(8);
            }

            final FieldConfiguration fc = new FieldConfiguration();
            fc.name = field.getName();
            fc.checks = checks;
            checks = cf.createList(); // create a new list for the next field with checks
            config.fieldConfigurations.add(fc);
         }
      }

      /*
       * determine getter checks
       */
      for (final Method method : config.type.getDeclaredMethods()) {
         // consider getters only
         if (!ReflectionUtils.isGetter(method)) {
            continue;
         }

         // loop over all annotations
         for (final Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof Basic) {
               initializeChecks((Basic) annotation, checks);
            } else if (annotation instanceof Column) {
               initializeChecks((Column) annotation, checks, method);
            } else if (annotation instanceof OneToOne) {
               initializeChecks((OneToOne) annotation, checks);
            } else if (annotation instanceof ManyToOne) {
               initializeChecks((ManyToOne) annotation, checks);
            } else if (annotation instanceof ManyToMany) {
               initializeChecks((ManyToMany) annotation, checks);
            } else if (annotation instanceof OneToMany) {
               initializeChecks((OneToMany) annotation, checks);
            }

            addAssertValidCheckIfRequired(annotation, checks, method);
         }

         // check if anything has been configured for this method at all
         if (checks.size() > 0) {
            if (config.methodConfigurations == null) {
               config.methodConfigurations = cf.createSet(2);
            }

            final MethodConfiguration mc = new MethodConfiguration();
            mc.name = method.getName();
            mc.isInvariant = true;
            mc.returnValueConfiguration = new MethodReturnValueConfiguration();
            mc.returnValueConfiguration.checks = checks;
            checks = cf.createList(); // create a new list for the next method having return value checks
            config.methodConfigurations.add(mc);
         }
      }
      return config;
   }

   @Override
   public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId) {
      return null;
   }

   protected void initializeChecks(final Basic annotation, final Collection<Check> checks) {
      if (!annotation.optional() && !containsCheckOfType(checks, NotNullCheck.class)) {
         checks.add(new NotNullCheck());
      }
   }

   protected void initializeChecks(final Column annotation, final Collection<Check> checks, final AccessibleObject fieldOrMethod) {
      /* If the value is generated (annotated with @GeneratedValue) it is allowed to be null
       * before the entity has been persisted, same is true in case of optimistic locking
       * when a field is annotated with @Version.
       * Therefore and because of the fact that there is no generic way to determine if an entity
       * has been persisted already, a not-null check will not be performed for such fields.
       */
      if (!annotation.nullable() //
         && !fieldOrMethod.isAnnotationPresent(GeneratedValue.class) //
         && !fieldOrMethod.isAnnotationPresent(Version.class) //
         && !fieldOrMethod.isAnnotationPresent(NotNull.class) //
         && !containsCheckOfType(checks, NotNullCheck.class) //
      ) {
         checks.add(new NotNullCheck());
      }

      // add Length check based on Column.length parameter, but only:
      if (!fieldOrMethod.isAnnotationPresent(Lob.class) && // if @Lob is not present
         !fieldOrMethod.isAnnotationPresent(Enumerated.class) && // if @Enumerated is not present
         !fieldOrMethod.isAnnotationPresent(Length.class) // if an explicit @Length constraint is not present
      ) {
         final LengthCheck lengthCheck = new LengthCheck();
         lengthCheck.setMax(annotation.length());
         checks.add(lengthCheck);
      }

      // add Range check based on Column.precision/scale parameters, but only:
      if (!fieldOrMethod.isAnnotationPresent(Range.class) // if an explicit @Range is not present
         && annotation.precision() > 0 // if precision is > 0
         && Number.class.isAssignableFrom(fieldOrMethod instanceof Field //
            ? ((Field) fieldOrMethod).getType() //
            : ((Method) fieldOrMethod).getReturnType()) // if numeric field type
      ) {
         /* precision = 6, scale = 2  => -9999.99<=x<=9999.99
          * precision = 4, scale = 1  =>   -999.9<=x<=999.9
          */
         final RangeCheck rangeCheck = new RangeCheck();
         rangeCheck.setMax(Math.pow(10, annotation.precision() - annotation.scale()) - Math.pow(0.1, annotation.scale()));
         rangeCheck.setMin(-1 * rangeCheck.getMax());
         checks.add(rangeCheck);
      }
   }

   @SuppressWarnings("unused")
   protected void initializeChecks(final ManyToMany annotation, final Collection<Check> checks) {
      // override if required
   }

   protected void initializeChecks(final ManyToOne annotation, final Collection<Check> checks) {
      if (!annotation.optional() && !containsCheckOfType(checks, NotNullCheck.class)) {
         checks.add(new NotNullCheck());
      }
   }

   @SuppressWarnings("unused")
   protected void initializeChecks(final OneToMany annotation, final Collection<Check> checks) {
      // override if required
   }

   protected void initializeChecks(final OneToOne annotation, final Collection<Check> checks) {
      if (!annotation.optional() && !containsCheckOfType(checks, NotNullCheck.class)) {
         checks.add(new NotNullCheck());
      }
   }

   public Boolean isApplyFieldConstraintsToSetter() {
      return applyFieldConstraintsToSetters;
   }

   public void setApplyFieldConstraintsToConstructors(final Boolean applyFieldConstraintsToConstructors) {
      this.applyFieldConstraintsToConstructors = applyFieldConstraintsToConstructors;
   }

   public void setApplyFieldConstraintsToSetters(final Boolean applyFieldConstraintsToSetters) {
      this.applyFieldConstraintsToSetters = applyFieldConstraintsToSetters;
   }
}
