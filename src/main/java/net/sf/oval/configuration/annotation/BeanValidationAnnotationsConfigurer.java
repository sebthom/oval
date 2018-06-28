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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import net.sf.oval.Check;
import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.constraint.AssertFalseCheck;
import net.sf.oval.constraint.AssertNullCheck;
import net.sf.oval.constraint.AssertTrueCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.DateRangeCheck;
import net.sf.oval.constraint.DigitsCheck;
import net.sf.oval.constraint.FutureCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.MaxCheck;
import net.sf.oval.constraint.MinCheck;
import net.sf.oval.constraint.NotBlankCheck;
import net.sf.oval.constraint.NotEmptyCheck;
import net.sf.oval.constraint.NotNegativeCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.PastCheck;
import net.sf.oval.constraint.SizeCheck;
import net.sf.oval.guard.Guarded;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Constraints configurer that interprets the built-in Java Bean Validation annotations.
 * Requires validation-api-1.0.0.jar or validation-api-2.0.0.jar to be on the classpath.
 *
 * <p>
 * <b>JSR-303:</b>
 * <ul>
 * <li>javax.validation.constraints.AssertFalse => net.sf.oval.constraint.AssertFalseCheck
 * <li>javax.validation.constraints.AssertTrue => net.sf.oval.constraint.AssertTrueCheck
 * <li>javax.validation.constraints.DecimalMax => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.DecimalMin => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.Digits => net.sf.oval.constraint.DigitsCheck
 * <li>javax.validation.constraints.Future => net.sf.oval.constraint.FutureCheck
 * <li>javax.validation.constraints.Max => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.Min => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.NotNull => net.sf.oval.constraint.NotNullCheck
 * <li>javax.validation.constraints.Null => net.sf.oval.constraint.AssertNullCheck
 * <li>javax.validation.constraints.Past => net.sf.oval.constraint.PastCheck
 * <li>javax.validation.constraints.Pattern => net.sf.oval.constraint.PatternCheck
 * <li>javax.validation.constraints.Size => net.sf.oval.constraint.SizeCheck
 * <li>javax.validation.Valid => net.sf.oval.constraint.AssertValidCheck
 * </ul>
 * <p>
 * <b>JSR-380:</b>
 * <ul>
 * <li>javax.validation.constraints.Email => net.sf.oval.constraint.EmailCheck
 * <li>javax.validation.constraints.FutureOrPresent => net.sf.oval.constraint.DateRangeCheck(min="now")
 * <li>javax.validation.constraints.Negative => net.sf.oval.constraint.MaxCheck(max=0, inclusive=false)
 * <li>javax.validation.constraints.NegativeOrZero => net.sf.oval.constraint.MaxCheck(max=0, inclusive=true)
 * <li>javax.validation.constraints.NotBlank => net.sf.oval.constraint.NotBlankCheck
 * <li>javax.validation.constraints.NotEmpty => net.sf.oval.constraint.NotEmptyCheck
 * <li>javax.validation.constraints.PastOrPresent => net.sf.oval.constraint.DateRange(max="now")
 * <li>javax.validation.constraints.Positive => net.sf.oval.constraint.MinCheck(min=0, inclusive=false)
 * <li>javax.validation.constraints.PositiveOrZero => net.sf.oval.constraint.NotNegativeCheck
 * </ul>
 *
 * @author Sebastian Thomschke
 */
public class BeanValidationAnnotationsConfigurer implements Configurer {

   private interface ConstraintMapper {
      Check map(Annotation annotation);
   }

   private static class JSR303Mapper implements ConstraintMapper {

      @Override
      public Check map(final Annotation annotation) {
         if (annotation instanceof NotNull)
            return new NotNullCheck();
         if (annotation instanceof Null)
            return new AssertNullCheck();
         if (annotation instanceof Valid)
            return new AssertValidCheck();
         if (annotation instanceof AssertTrue)
            return new AssertTrueCheck();
         if (annotation instanceof AssertFalse)
            return new AssertFalseCheck();
         if (annotation instanceof DecimalMax) {
            final MaxCheck check = new MaxCheck();
            check.setMax(Double.parseDouble(((DecimalMax) annotation).value()));
            final Method getInclusive = ReflectionUtils.getMethod(annotation.annotationType(), "inclusive");
            if (getInclusive != null) {
               check.setInclusive((Boolean) ReflectionUtils.invokeMethod(getInclusive, annotation));
            }
            return check;
         }
         if (annotation instanceof DecimalMin) {
            final MinCheck check = new MinCheck();
            check.setMin(Double.parseDouble(((DecimalMin) annotation).value()));
            final Method getInclusive = ReflectionUtils.getMethod(annotation.annotationType(), "inclusive");
            if (getInclusive != null) {
               check.setInclusive((Boolean) ReflectionUtils.invokeMethod(getInclusive, annotation));
            }
            return check;
         }
         if (annotation instanceof Max) {
            final MaxCheck check = new MaxCheck();
            check.setMax(((Max) annotation).value());
            return check;
         }
         if (annotation instanceof Min) {
            final MinCheck check = new MinCheck();
            check.setMin(((Min) annotation).value());
            return check;
         }
         if (annotation instanceof Future)
            return new FutureCheck();
         if (annotation instanceof Past)
            return new PastCheck();
         if (annotation instanceof Pattern) {
            final MatchPatternCheck check = new MatchPatternCheck();
            int iflag = 0;
            for (final Flag flag : ((Pattern) annotation).flags()) {
               iflag = iflag | flag.getValue();
            }
            check.setPattern(((Pattern) annotation).regexp(), iflag);
            return check;
         }
         if (annotation instanceof Digits) {
            final DigitsCheck check = new DigitsCheck();
            check.setMaxFraction(((Digits) annotation).fraction());
            check.setMaxInteger(((Digits) annotation).integer());
            return check;
         }
         if (annotation instanceof Size) {
            final SizeCheck check = new SizeCheck();
            check.setMax(((Size) annotation).max());
            check.setMin(((Size) annotation).min());
            return check;
         }
         return null;
      }

   }

   private static class JSR380Mapper extends JSR303Mapper {
      @Override
      public Check map(final Annotation annotation) {
         final Check jsr303check = super.map(annotation);
         if (jsr303check != null)
            return jsr303check;

         if (annotation instanceof FutureOrPresent) {
            final DateRangeCheck check = new DateRangeCheck();
            check.setMin("now");
            return check;
         }
         if (annotation instanceof Negative) {
            final MaxCheck check = new MaxCheck();
            check.setInclusive(false);
            check.setMax(0);
            return check;
         }
         if (annotation instanceof NegativeOrZero) {
            final MaxCheck check = new MaxCheck();
            check.setInclusive(true);
            check.setMax(0);
            return check;
         }
         if (annotation instanceof NotBlank)
            return new NotBlankCheck();
         if (annotation instanceof NotEmpty)
            return new NotEmptyCheck();
         if (annotation instanceof PastOrPresent) {
            final DateRangeCheck check = new DateRangeCheck();
            check.setMax("now");
            return check;
         }
         if (annotation instanceof Positive) {
            final MinCheck check = new MinCheck();
            check.setInclusive(false);
            check.setMin(0);
            return check;
         }
         if (annotation instanceof PositiveOrZero)
            return new NotNegativeCheck();
         return null;
      }
   }

   private static final Log LOG = Log.getLog(BeanValidationAnnotationsConfigurer.class);

   private static final ConstraintMapper CONSTRAINT_MAPPER;
   static {
      ConstraintMapper constraintMapper = null;
      try {
         // first try if bean validation API 2.0 is available
         constraintMapper = new JSR380Mapper();
      } catch (final LinkageError ex) {
         // fallback to bean validation API 1.0
         constraintMapper = new JSR303Mapper();
      }
      CONSTRAINT_MAPPER = constraintMapper;
   }

   public BeanValidationAnnotationsConfigurer() {
      super();
   }

   private List<ParameterConfiguration> _createParameterConfiguration(final Annotation[][] paramAnnotations, final Class<?>[] parameterTypes) {
      final CollectionFactory cf = getCollectionFactory();

      final List<ParameterConfiguration> paramCfg = cf.createList(paramAnnotations.length);

      List<Check> paramChecks = cf.createList(2);

      // loop over all parameters of the current constructor
      for (int i = 0; i < paramAnnotations.length; i++) {
         // loop over all annotations of the current constructor parameter
         for (final Annotation annotation : paramAnnotations[i]) {
            initializeChecks(annotation, paramChecks);
         }

         final ParameterConfiguration pc = new ParameterConfiguration();
         paramCfg.add(pc);
         pc.type = parameterTypes[i];
         if (paramChecks.size() > 0) {
            pc.checks = paramChecks;
            paramChecks = cf.createList(2); // create a new list for the next parameter having checks
         }
      }
      return paramCfg;
   }

   protected void configureConstructorParameterChecks(final ClassConfiguration classCfg) {
      final CollectionFactory cf = getCollectionFactory();

      for (final Constructor<?> ctor : classCfg.type.getDeclaredConstructors()) {
         final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(ctor.getParameterAnnotations(), ctor.getParameterTypes());

         if (paramCfg.size() > 0) {
            if (classCfg.constructorConfigurations == null) {
               classCfg.constructorConfigurations = cf.createSet(2);
            }

            final ConstructorConfiguration cc = new ConstructorConfiguration();
            cc.parameterConfigurations = paramCfg;
            cc.postCheckInvariants = false;
            classCfg.constructorConfigurations.add(cc);
         }
      }
   }

   protected void configureFieldChecks(final ClassConfiguration classCfg) {
      final CollectionFactory cf = getCollectionFactory();

      List<Check> checks = cf.createList(2);

      for (final Field field : classCfg.type.getDeclaredFields()) {
         // loop over all annotations of the current field
         for (final Annotation annotation : field.getAnnotations()) {
            initializeChecks(annotation, checks);
         }

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

      for (final Method method : classCfg.type.getDeclaredMethods()) {
         // loop over all annotations
         for (final Annotation annotation : ReflectionUtils.getAnnotations(method, Boolean.TRUE.equals(classCfg.inspectInterfaces))) {
            initializeChecks(annotation, returnValueChecks);
         }

         /*
          * determine parameter checks
          */
         final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(ReflectionUtils.getParameterAnnotations(method, Boolean.TRUE.equals(
            classCfg.inspectInterfaces)), method.getParameterTypes());

         // check if anything has been configured for this method at all
         if (paramCfg.size() > 0 || returnValueChecks.size() > 0) {
            if (classCfg.methodConfigurations == null) {
               classCfg.methodConfigurations = cf.createSet(2);
            }

            final MethodConfiguration mc = new MethodConfiguration();
            mc.name = method.getName();
            mc.parameterConfigurations = paramCfg;
            mc.isInvariant = ReflectionUtils.isGetter(method);
            if (returnValueChecks.size() > 0) {
               mc.returnValueConfiguration = new MethodReturnValueConfiguration();
               mc.returnValueConfiguration.checks = returnValueChecks;
               returnValueChecks = cf.createList(2); // create a new list for the next method having return value checks
            }
            classCfg.methodConfigurations.add(mc);
         }
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

      configureFieldChecks(classCfg);
      configureConstructorParameterChecks(classCfg);
      configureMethodChecks(classCfg);

      return classCfg;
   }

   @Override
   public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId) {
      return null;
   }

   protected void initializeChecks(final Annotation annotation, final Collection<Check> checks) {
      assert annotation != null;
      assert checks != null;

      final Class<?> annotationClass = annotation.annotationType();

      /*
       * process bean validation annotations
       */
      if (annotationClass.getAnnotation(javax.validation.Constraint.class) != null || annotation instanceof Valid) {

         final Check check = CONSTRAINT_MAPPER.map(annotation);

         if (check != null) {
            final Method getMessage = ReflectionUtils.getMethod(annotationClass, "message");
            if (getMessage != null) {
               final String message = ReflectionUtils.invokeMethod(getMessage, annotation);
               if (message != null && !message.startsWith("{javax.validation.constraints.")) {
                  check.setMessage(message);
               }
            }

            final Method getGroups = ReflectionUtils.getMethod(annotationClass, "groups");
            if (getGroups != null) {
               final Class<?>[] groups = ReflectionUtils.invokeMethod(getGroups, annotation);
               if (groups != null && groups.length > 0) {
                  final String[] profiles = new String[groups.length];
                  for (int i = 0, l = groups.length; i < l; i++) {
                     profiles[i] = groups[i].getName();
                  }
                  check.setProfiles(profiles);
               }
            }
            checks.add(check);
            return;
         }

         LOG.warn("Ignoring unsupported bean validation constraint annotation {1}", annotation);
         return;
      }

      /*
       * process bean validation List annotations
       */
      if (annotationClass.getPackage().getName().equals("javax.validation.constraints") && "List".equals(annotationClass.getSimpleName())) {
         final Annotation[] list = ReflectionUtils.invokeMethod(ReflectionUtils.getMethod(annotationClass, "value"), annotation);
         if (list != null) {
            for (final Annotation anno : list) {
               initializeChecks(anno, checks);
            }
         }
      }
   }
}
