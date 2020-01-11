/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
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
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.configuration.CheckInitializationListener;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPostExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPreExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.constraint.ConstraintsCheck;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PostValidateThis;
import net.sf.oval.guard.Pre;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.guard.PreValidateThis;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Configurer that configures constraints based on annotations tagged with {@link Constraint}
 *
 * @author Sebastian Thomschke
 * @author Chris Pheby - interface based method parameter validation {@link Guarded#inspectInterfaces}
 */
public class AnnotationsConfigurer implements Configurer {

   protected final Set<CheckInitializationListener> listeners = new LinkedHashSet<>(2);

   private List<ParameterConfiguration> _createParameterConfigs(final Class<?>[] paramTypes, final Annotation[][] paramAnnos,
      final AnnotatedType[] annotatedParamTypes) {
      final CollectionFactory cf = getCollectionFactory();

      final List<ParameterConfiguration> paramCfgs = cf.createList(paramAnnos.length);

      List<Check> paramChecks = cf.createList(2);
      List<CheckExclusion> paramCheckExclusions = cf.createList(2);

      // loop over all parameters of the current constructor
      for (int i = 0; i < paramAnnos.length; i++) {

         // loop over all annotations of the current constructor parameter
         for (final Annotation anno : paramAnnos[i]) {
            // check if the current annotation is a constraint annotation
            if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
               paramChecks.add(initializeCheck(anno));
            } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
               initializeChecks(anno, paramChecks);
            } else if (anno.annotationType().isAnnotationPresent(Exclusion.class)) {
               paramCheckExclusions.add(initializeExclusion(anno));
            }
         }

         initializeGenericTypeChecks(paramTypes[i], annotatedParamTypes[i], paramChecks);

         final ParameterConfiguration paramCfg = new ParameterConfiguration();
         paramCfgs.add(paramCfg);
         paramCfg.type = paramTypes[i];
         if (paramChecks.size() > 0) {
            paramCfg.checks = paramChecks;
            paramChecks = cf.createList(2); // create a new list for the next parameter having checks
         }
         if (paramCheckExclusions.size() > 0) {
            paramCfg.checkExclusions = paramCheckExclusions;
            paramCheckExclusions = cf.createList(2); // create a new list for the next parameter having check exclusions
         }
      }
      return paramCfgs;
   }

   public boolean addCheckInitializationListener(final CheckInitializationListener listener) {
      Assert.argumentNotNull("listener", "[listener] must not be null");
      return listeners.add(listener);
   }

   protected void configureCtorParamChecks(final ClassConfiguration classCfg) {
      for (final Constructor<?> ctor : classCfg.type.getDeclaredConstructors()) {

         /*
          * determine parameter checks
          */
         final List<ParameterConfiguration> paramCfgs = _createParameterConfigs( //
            ctor.getParameterTypes(), //
            ctor.getParameterAnnotations(), //
            ctor.getAnnotatedParameterTypes() //
         );

         /*
          * check if anything has been configured for this constructor at all
          */
         final boolean postValidateThis = ctor.isAnnotationPresent(PostValidateThis.class);
         if (postValidateThis || paramCfgs.size() > 0) {
            if (classCfg.constructorConfigurations == null) {
               classCfg.constructorConfigurations = getCollectionFactory().createSet(2);
            }

            final ConstructorConfiguration cc = new ConstructorConfiguration();
            cc.parameterConfigurations = paramCfgs;
            cc.postCheckInvariants = postValidateThis;
            classCfg.constructorConfigurations.add(cc);
         }
      }
   }

   protected void configureFieldChecks(final ClassConfiguration classCfg) {
      final CollectionFactory cf = getCollectionFactory();

      List<Check> checks = cf.createList(2);

      for (final Field field : classCfg.type.getDeclaredFields()) {
         // loop over all annotations of the current field
         for (final Annotation anno : field.getAnnotations()) {
            // check if the current annotation is a constraint annotation
            if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
               checks.add(initializeCheck(anno));
            } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
               initializeChecks(anno, checks);
            }
         }

         initializeGenericTypeChecks(field.getType(), field.getAnnotatedType(), checks);

         /*
          * check if anything has been configured for this field at all
          */
         if (checks.size() > 0) {
            if (classCfg.fieldConfigurations == null) {
               classCfg.fieldConfigurations = cf.createSet(2);
            }

            final FieldConfiguration fc = new FieldConfiguration();
            fc.name = field.getName();
            fc.checks = checks;
            classCfg.fieldConfigurations.add(fc);
            checks = cf.createList(2); // create a new list for the next field with checks
         }
      }
   }

   /**
    * configure method return value and parameter checks
    */
   protected void configureMethodChecks(final ClassConfiguration classCfg) {
      final CollectionFactory cf = getCollectionFactory();

      List<Check> returnValueChecks = cf.createList(2);
      List<PreCheck> preChecks = cf.createList(2);
      List<PostCheck> postChecks = cf.createList(2);

      for (final Method method : classCfg.type.getDeclaredMethods()) {
         /*
          * determine method return value checks and method pre/post conditions
          */
         boolean preValidateThis = false;
         boolean postValidateThis = false;

         // loop over all annotations
         for (final Annotation anno : ReflectionUtils.getAnnotations(method, Boolean.TRUE.equals(classCfg.inspectInterfaces))) {
            if (anno instanceof Pre) {
               final PreCheck pc = new PreCheck();
               pc.configure((Pre) anno);
               preChecks.add(pc);
            } else if (anno instanceof PreValidateThis) {
               preValidateThis = true;
            } else if (anno instanceof Post) {
               final PostCheck pc = new PostCheck();
               pc.configure((Post) anno);
               postChecks.add(pc);
            } else if (anno instanceof PostValidateThis) {
               postValidateThis = true;
            } else if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
               returnValueChecks.add(initializeCheck(anno));
            } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
               initializeChecks(anno, returnValueChecks);
            }
         }

         initializeGenericTypeChecks(method.getReturnType(), method.getAnnotatedReturnType(), returnValueChecks);

         /*
          * determine parameter checks
          */
         final List<ParameterConfiguration> paramCfg = _createParameterConfigs( //
            method.getParameterTypes(), //
            ReflectionUtils.getParameterAnnotations(method, Boolean.TRUE.equals(classCfg.inspectInterfaces)), //
            method.getAnnotatedParameterTypes() //
         );

         /*
          * check if anything has been configured for this method at all
          */
         if (preValidateThis || postValidateThis || paramCfg.size() > 0 || returnValueChecks.size() > 0 || preChecks.size() > 0 || postChecks.size() > 0) {
            if (classCfg.methodConfigurations == null) {
               classCfg.methodConfigurations = cf.createSet(2);
            }

            final MethodConfiguration mc = new MethodConfiguration();
            mc.name = method.getName();
            mc.parameterConfigurations = paramCfg;
            mc.isInvariant = ReflectionUtils.isAnnotationPresent(method, IsInvariant.class, Boolean.TRUE.equals(classCfg.inspectInterfaces));
            mc.preCheckInvariants = preValidateThis;
            mc.postCheckInvariants = postValidateThis;
            if (returnValueChecks.size() > 0) {
               mc.returnValueConfiguration = new MethodReturnValueConfiguration();
               mc.returnValueConfiguration.checks = returnValueChecks;
               returnValueChecks = cf.createList(2); // create a new list for the next method having return value checks
            }
            if (preChecks.size() > 0) {
               mc.preExecutionConfiguration = new MethodPreExecutionConfiguration();
               mc.preExecutionConfiguration.checks = preChecks;
               preChecks = cf.createList(2); // create a new list for the next method having pre checks
            }
            if (postChecks.size() > 0) {
               mc.postExecutionConfiguration = new MethodPostExecutionConfiguration();
               mc.postExecutionConfiguration.checks = postChecks;
               postChecks = cf.createList(2); // create a new list for the next method having post checks
            }
            classCfg.methodConfigurations.add(mc);
         }
      }
   }

   protected void configureObjectLevelChecks(final ClassConfiguration classCfg) {
      final List<Check> checks = getCollectionFactory().createList(2);

      for (final Annotation anno : ReflectionUtils.getAnnotations(classCfg.type, Boolean.TRUE.equals(classCfg.inspectInterfaces)))
         // check if the current annotation is a constraint annotation
         if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
            checks.add(initializeCheck(anno));
         } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
            initializeChecks(anno, checks);
         }

      if (checks.size() > 0) {
         classCfg.objectConfiguration = new ObjectConfiguration();
         classCfg.objectConfiguration.checks = checks;
      }
   }

   @Override
   public ClassConfiguration getClassConfiguration(final Class<?> clazz) {
      final ClassConfiguration classCfg = new ClassConfiguration();
      classCfg.type = clazz;

      final Guarded guarded = clazz.getAnnotation(Guarded.class);

      if (guarded == null) {
         classCfg.applyFieldConstraintsToConstructors = false;
         classCfg.applyFieldConstraintsToSetters = false;
         classCfg.assertParametersNotNull = false;
         classCfg.checkInvariants = false;
         classCfg.inspectInterfaces = false;
      } else {
         classCfg.applyFieldConstraintsToConstructors = guarded.applyFieldConstraintsToConstructors();
         classCfg.applyFieldConstraintsToSetters = guarded.applyFieldConstraintsToSetters();
         classCfg.assertParametersNotNull = guarded.assertParametersNotNull();
         classCfg.checkInvariants = guarded.checkInvariants();
         classCfg.inspectInterfaces = guarded.inspectInterfaces();
      }

      configureObjectLevelChecks(classCfg);
      configureFieldChecks(classCfg);
      configureCtorParamChecks(classCfg);
      configureMethodChecks(classCfg);

      return classCfg;
   }

   @Override
   public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId) {
      return null;
   }

   @SuppressWarnings("unchecked")
   protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(final ConstraintAnnotation constraintAnnotation,
      final ConstraintTarget... targetOverrides) throws ReflectionException {
      assert constraintAnnotation != null;

      final Constraint constraint = constraintAnnotation.annotationType().getAnnotation(Constraint.class);

      // determine the check class
      final Class<AnnotationCheck<ConstraintAnnotation>> checkClass = (Class<AnnotationCheck<ConstraintAnnotation>>) constraint.checkWith();

      // instantiate the appropriate check for the found constraint
      final AnnotationCheck<ConstraintAnnotation> check = newCheckInstance(checkClass);
      check.configure(constraintAnnotation);
      if (targetOverrides.length > 0) {
         check.setAppliesTo(targetOverrides);
      }
      for (final CheckInitializationListener listener : listeners) {
         listener.onCheckInitialized(check);
      }
      return check;
   }

   /**
    * handles list of annotations like @Assert.List(...)
    */
   protected <ConstraintsAnnotation extends Annotation> void initializeChecks(final ConstraintsAnnotation constraintsAnnotation, final List<Check> checks,
      final ConstraintTarget... targetOverrides) throws ReflectionException {
      try {
         final Method getValue = constraintsAnnotation.annotationType().getDeclaredMethod("value", (Class<?>[]) null);
         final Object[] constraintAnnotations = (Object[]) getValue.invoke(constraintsAnnotation, (Object[]) null);

         final ConstraintsCheck constraintsCheck = new ConstraintsCheck();
         constraintsCheck.configure(constraintsAnnotation);
         constraintsCheck.checks = new ArrayList<>(constraintAnnotations.length);
         for (final Object ca : constraintAnnotations) {
            constraintsCheck.checks.add(initializeCheck((Annotation) ca, targetOverrides));
         }
         checks.add(constraintsCheck);
      } catch (final ReflectionException ex) {
         throw ex;
      } catch (final Exception ex) {
         throw new ReflectionException("Cannot initialize constraint check " + constraintsAnnotation.annotationType().getName(), ex);
      }
   }

   @SuppressWarnings("unchecked")
   protected <ExclusionAnnotation extends Annotation> AnnotationCheckExclusion<ExclusionAnnotation> initializeExclusion(
      final ExclusionAnnotation exclusionAnnotation) throws ReflectionException {
      assert exclusionAnnotation != null;

      final Exclusion constraint = exclusionAnnotation.annotationType().getAnnotation(Exclusion.class);

      // determine the check class
      final Class<?> exclusionClass = constraint.excludeWith();

      try {
         // instantiate the appropriate exclusion for the found annotation
         final AnnotationCheckExclusion<ExclusionAnnotation> exclusion = (AnnotationCheckExclusion<ExclusionAnnotation>) exclusionClass.newInstance();
         exclusion.configure(exclusionAnnotation);
         return exclusion;
      } catch (final Exception ex) {
         throw new ReflectionException("Cannot initialize constraint exclusion " + exclusionClass.getName(), ex);
      }
   }

   protected void initializeGenericTypeChecks(final Class<?> type, final AnnotatedType annotatedType, final List<Check> checks) {
      if (annotatedType instanceof AnnotatedParameterizedType) {
         final AnnotatedParameterizedType fieldAPType = (AnnotatedParameterizedType) annotatedType;

         if (Collection.class.isAssignableFrom(type)) {
            final AnnotatedType genericArgType = fieldAPType.getAnnotatedActualTypeArguments()[0];
            for (final Annotation anno : genericArgType.getAnnotations()) {
               if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
                  checks.add(initializeCheck(anno, ConstraintTarget.VALUES));
               } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
                  initializeChecks(anno, checks, ConstraintTarget.VALUES);
               }
            }
         } else if (Map.class.isAssignableFrom(type)) {

            // Keys
            {
               final AnnotatedType genericArgType = fieldAPType.getAnnotatedActualTypeArguments()[0];
               for (final Annotation anno : genericArgType.getAnnotations()) {
                  if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
                     checks.add(initializeCheck(anno, ConstraintTarget.KEYS));
                  } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
                     initializeChecks(anno, checks, ConstraintTarget.KEYS);
                  }
               }
            }

            // Values
            {
               final AnnotatedType genericArgType = fieldAPType.getAnnotatedActualTypeArguments()[1];
               for (final Annotation anno : genericArgType.getAnnotations()) {
                  if (anno.annotationType().isAnnotationPresent(Constraint.class)) {
                     checks.add(initializeCheck(anno, ConstraintTarget.VALUES));
                  } else if (anno.annotationType().isAnnotationPresent(Constraints.class)) {
                     initializeChecks(anno, checks, ConstraintTarget.VALUES);
                  }
               }
            }
         }
      }
   }

   /**
    * @return a new instance of the given constraint check implementation class
    */
   protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> newCheckInstance(
      final Class<AnnotationCheck<ConstraintAnnotation>> checkClass) throws OValException {
      try {
         return checkClass.newInstance();
      } catch (final InstantiationException ex) {
         throw new ReflectionException("Cannot initialize constraint check " + checkClass.getName(), ex);
      } catch (final IllegalAccessException ex) {
         throw new ReflectionException("Cannot initialize constraint check " + checkClass.getName(), ex);
      }
   }

   public boolean removeCheckInitializationListener(final CheckInitializationListener listener) {
      return listeners.remove(listener);
   }
}
