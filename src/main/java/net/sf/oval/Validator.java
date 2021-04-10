/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval;

import static java.lang.Boolean.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.collection.CollectionFactoryJDKImpl;
import net.sf.oval.collection.CollectionFactoryJavolutionImpl;
import net.sf.oval.collection.CollectionFactoryTroveImpl;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer;
import net.sf.oval.configuration.pojo.POJOConfigurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.configuration.xml.XMLConfigurer;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.AssertFieldConstraintsCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.ConstraintsCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.context.ClassContext;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.IterableElementContext;
import net.sf.oval.context.MapKeyContext;
import net.sf.oval.context.MapValueContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.context.ObjectGraphNavigationContext;
import net.sf.oval.exception.ConstraintSetAlreadyDefinedException;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.ExceptionTranslator;
import net.sf.oval.exception.FieldNotFoundException;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.MethodNotFoundException;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.exception.UndefinedConstraintSetException;
import net.sf.oval.exception.ValidationFailedException;
import net.sf.oval.expression.ExpressionLanguageRegistry;
import net.sf.oval.guard.ParameterNameResolver;
import net.sf.oval.guard.ParameterNameResolverEnumerationImpl;
import net.sf.oval.internal.ClassChecks;
import net.sf.oval.internal.ContextCache;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.MessageRenderer;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.CollectionUtils;
import net.sf.oval.internal.util.IdentityHashSet;
import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.localization.context.DefaultOValContextRenderer;
import net.sf.oval.localization.context.OValContextRenderer;
import net.sf.oval.localization.locale.LocaleProvider;
import net.sf.oval.localization.locale.ThreadLocalLocaleProvider;
import net.sf.oval.localization.message.MessageResolver;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;
import net.sf.oval.localization.value.MessageValueFormatter;
import net.sf.oval.localization.value.ToStringMessageValueFormatter;
import net.sf.oval.logging.LoggerFactory;
import net.sf.oval.ogn.ObjectGraphNavigationResult;
import net.sf.oval.ogn.ObjectGraphNavigatorRegistry;

/**
 * Instances of this class can validate objects based on declared constraints.
 * Constraints can either be declared using OVal's constraint annotations, XML configuration
 * files or EJB3 JPA annotations.<br/>
 * <br/>
 * This class is thread-safe.
 *
 * @author Sebastian Thomschke
 *
 * @see AnnotationsConfigurer
 * @see JPAAnnotationsConfigurer
 * @see POJOConfigurer
 * @see XMLConfigurer
 */
public class Validator implements IValidator {

   protected static final Check[] EMPTY_CHECKS = {};

   protected static final class DelegatingParameterNameResolver implements ParameterNameResolver {
      private ParameterNameResolver delegate;

      public DelegatingParameterNameResolver(final ParameterNameResolver delegate) {
         this.delegate = delegate;
      }

      public ParameterNameResolver getDelegate() {
         return delegate;
      }

      @Override
      public String[] getParameterNames(final Constructor<?> constructor) throws ReflectionException {
         return delegate.getParameterNames(constructor);
      }

      @Override
      public String[] getParameterNames(final Method method) throws ReflectionException {
         return delegate.getParameterNames(method);
      }

      public void setDelegate(final ParameterNameResolver delegate) {
         this.delegate = delegate;
      }
   }

   protected final class InternalValidationCycle implements ValidationCycle {
      public final String[] profiles;
      public IdentityHashSet<Object> validatedObjects = new IdentityHashSet<>(4);
      public final Object rootValidatedObject;
      public List<ConstraintViolation> violations = Collections.emptyList();
      public final List<OValContext> contextPath = collectionFactory.createList(4);
      public final List<OValContext> contextPathImmutable = Collections.unmodifiableList(contextPath);

      public InternalValidationCycle(final Object rootValidatedObject, final String[] profiles) {
         this.profiles = profiles;
         this.rootValidatedObject = rootValidatedObject;
      }

      @Override
      public void addConstraintViolation(final Check check, final String message, final Object invalidValue) {
         addConstraintViolation(new ConstraintViolation(check, message, rootValidatedObject, invalidValue, contextPathImmutable));
      }

      @Override
      public void addConstraintViolation(final ConstraintViolation violation) {
         if (violations.isEmpty()) {
            violations = collectionFactory.createList();
         }
         violations.add(violation);
      }

      @Override
      public List<OValContext> getContextPath() {
         return contextPathImmutable;
      }

      @Override
      public Object getRootObject() {
         return rootValidatedObject;
      }

      @Override
      public Validator getValidator() {
         return Validator.this;
      }
   }

   private static final Log LOG = Log.getLog(Validator.class);

   private static CollectionFactory collectionFactory = _createDefaultCollectionFactory();
   private static OValContextRenderer contextRenderer = DefaultOValContextRenderer.INSTANCE;
   private static LocaleProvider localeProvider = new ThreadLocalLocaleProvider();
   private static MessageResolver messageResolver;
   private static MessageValueFormatter messageValueFormatter = ToStringMessageValueFormatter.INSTANCE;

   private static CollectionFactory _createDefaultCollectionFactory() {
      // if Javolution collection classes are found use them by default
      if (ReflectionUtils.isClassPresent("javolution.util.FastMap") //
         && ReflectionUtils.isClassPresent("javolution.util.FastSet") //
         && ReflectionUtils.isClassPresent("javolution.util.FastTable")) {
         LOG.info("javolution.util collection classes are available.");
         return new CollectionFactoryJavolutionImpl();
      }

      // else if Trove collection classes are found use them by default
      if (ReflectionUtils.isClassPresent("gnu.trove.map.hash.THashMap") //
         && ReflectionUtils.isClassPresent("gnu.trove.set.hash.THashSet")) {
         LOG.info("gnu.trove collection classes are available.");
         return new CollectionFactoryTroveImpl();
      }

      // else use JDK collection classes by default
      return new CollectionFactoryJDKImpl();
   }

   /**
    * Returns a shared instance of the CollectionFactory
    */
   public static CollectionFactory getCollectionFactory() {
      return collectionFactory;
   }

   public static OValContextRenderer getContextRenderer() {
      return contextRenderer;
   }

   public static LocaleProvider getLocaleProvider() {
      return localeProvider;
   }

   public static LoggerFactory getLoggerFactory() {
      return Log.getLoggerFactory();
   }

   public static MessageResolver getMessageResolver() {
      /*
       * since ResourceBundleMessageResolver references getCollectionFactory() of this class
       * we are lazy referencing the resolvers shared instance.
       */
      if (messageResolver == null) {
         messageResolver = ResourceBundleMessageResolver.INSTANCE;
      }
      return messageResolver;
   }

   public static MessageValueFormatter getMessageValueFormatter() {
      return messageValueFormatter;
   }

   /**
    * @param factory the new collection factory to be used by all Validator instances
    */
   public static void setCollectionFactory(final CollectionFactory factory) throws IllegalArgumentException {
      Assert.argumentNotNull("factory", factory);
      Validator.collectionFactory = factory;
   }

   public static void setContextRenderer(final OValContextRenderer contextRenderer) {
      Assert.argumentNotNull("contextRenderer", contextRenderer);
      Validator.contextRenderer = contextRenderer;
   }

   public static void setLocaleProvider(final LocaleProvider localeProvider) {
      Assert.argumentNotNull("localeProvider", localeProvider);
      Validator.localeProvider = localeProvider;
   }

   public static void setLoggerFactory(final LoggerFactory loggerFactory) {
      Assert.argumentNotNull("loggerFactory", loggerFactory);
      Log.setLoggerFactory(loggerFactory);
   }

   /**
    * @throws IllegalArgumentException if <code>messageResolver == null</code>
    */
   public static void setMessageResolver(final MessageResolver messageResolver) throws IllegalArgumentException {
      Assert.argumentNotNull("messageResolver", messageResolver);
      Validator.messageResolver = messageResolver;
   }

   public static void setMessageValueFormatter(final MessageValueFormatter formatter) {
      Assert.argumentNotNull("formatter", formatter);
      Validator.messageValueFormatter = formatter;
   }

   private final ConcurrentMap<Class<?>, ClassChecks> checksByClass = collectionFactory.createConcurrentMap();
   private final Set<Configurer> configurers = new LinkedHashSet<>(4);
   private final Map<String, ConstraintSet> constraintSetsById = collectionFactory.createConcurrentMap(4);

   protected final ThreadLocal<LinkedList<InternalValidationCycle>> currentValidationCycles = ThreadLocal.withInitial(LinkedList::new);

   private ExceptionTranslator exceptionTranslator;

   protected final ExpressionLanguageRegistry expressionLanguageRegistry = new ExpressionLanguageRegistry();

   private final Set<String> disabledProfiles = collectionFactory.createSet();
   private final Set<String> enabledProfiles = collectionFactory.createSet();
   private boolean isAllProfilesEnabledByDefault = true;

   /**
    * Flag that indicates any configuration method related to profiles was called.
    * Used for performance improvements.
    */
   private boolean isProfilesFeatureUsed = false;

   protected final ObjectGraphNavigatorRegistry ognRegistry = new ObjectGraphNavigatorRegistry();

   protected final DelegatingParameterNameResolver parameterNameResolver = new DelegatingParameterNameResolver(new ParameterNameResolverEnumerationImpl());

   /**
    * Constructs a new instance and uses a new instance of AnnotationsConfigurer
    */
   public Validator() {
      ReflectionUtils.assertPrivateAccessAllowed();
      configurers.add(new AnnotationsConfigurer());
   }

   /**
    * Constructs a new instance and configures it using the given configurers
    */
   public Validator(final Collection<Configurer> configurers) {
      ReflectionUtils.assertPrivateAccessAllowed();
      if (configurers != null) {
         this.configurers.addAll(configurers);
      }
   }

   /**
    * Constructs a new instance and configures it using the given configurers
    */
   public Validator(final Configurer... configurers) {
      ReflectionUtils.assertPrivateAccessAllowed();
      if (configurers != null) {
         Collections.addAll(this.configurers, configurers);
      }
   }

   private void _addChecks(final ClassChecks cc, final ClassConfiguration classCfg) throws InvalidConfigurationException, ReflectionException {
      if (TRUE.equals(classCfg.overwrite)) {
         cc.clear();
      }

      if (classCfg.checkInvariants != null) {
         cc.isCheckInvariants = classCfg.checkInvariants;
      }

      // cache the result for better performance
      final boolean applyFieldConstraintsToConstructors = TRUE.equals(classCfg.applyFieldConstraintsToConstructors);
      final boolean applyFieldConstraintsToSetters = TRUE.equals(classCfg.applyFieldConstraintsToSetters);
      final boolean assertParametersNotNull = TRUE.equals(classCfg.assertParametersNotNull);
      final NotNullCheck sharedNotNullCheck = assertParametersNotNull ? new NotNullCheck() : null;

      try {
         /* ******************************
          * apply object level checks
          * ******************************/
         if (classCfg.objectConfiguration != null) {
            final ObjectConfiguration objectCfg = classCfg.objectConfiguration;

            if (TRUE.equals(objectCfg.overwrite)) {
               cc.clearObjectChecks();
            }
            cc.addObjectChecks(objectCfg.checks);
         }

         /* ******************************
          * apply field checks
          * ******************************/
         if (classCfg.fieldConfigurations != null) {
            for (final FieldConfiguration fieldCfg : classCfg.fieldConfigurations) {
               final Field field = classCfg.type.getDeclaredField(fieldCfg.name);

               if (TRUE.equals(fieldCfg.overwrite)) {
                  cc.clearFieldChecks(field);
               }

               if (fieldCfg.checks != null && !fieldCfg.checks.isEmpty()) {
                  cc.addFieldChecks(field, fieldCfg.checks);
               }
            }
         }

         /* ******************************
          * apply constructor parameter checks
          * ******************************/
         if (classCfg.constructorConfigurations != null) {
            for (final ConstructorConfiguration ctorCfg : classCfg.constructorConfigurations) {
               // ignore constructors without parameters
               if (ctorCfg.parameterConfigurations == null) {
                  continue;
               }

               final Class<?>[] paramTypes = new Class[ctorCfg.parameterConfigurations.size()];

               for (int i = 0, l = ctorCfg.parameterConfigurations.size(); i < l; i++) {
                  paramTypes[i] = ctorCfg.parameterConfigurations.get(i).type;
               }

               final Constructor<?> ctor = classCfg.type.getDeclaredConstructor(paramTypes);

               if (TRUE.equals(ctorCfg.overwrite)) {
                  cc.clearConstructorChecks(ctor);
               }

               if (TRUE.equals(ctorCfg.postCheckInvariants)) {
                  cc.methodsWithCheckInvariantsPost.add(ctor);
               }

               final String[] paramNames = parameterNameResolver.getParameterNames(ctor);

               for (int i = 0, l = ctorCfg.parameterConfigurations.size(); i < l; i++) {
                  final ParameterConfiguration paramCfg = ctorCfg.parameterConfigurations.get(i);

                  if (TRUE.equals(paramCfg.overwrite)) {
                     cc.clearConstructorParameterChecks(ctor, i);
                  }

                  if (paramCfg.hasChecks()) {
                     cc.addConstructorParameterChecks(ctor, i, paramCfg.checks);
                  }

                  if (paramCfg.hasCheckExclusions()) {
                     cc.addConstructorParameterCheckExclusions(ctor, i, paramCfg.checkExclusions);
                  }

                  if (assertParametersNotNull) {
                     cc.addConstructorParameterChecks(ctor, i, sharedNotNullCheck);
                  }

                  /* *******************
                   * applying field constraints to the single parameter of setter methods
                   * *******************/
                  if (applyFieldConstraintsToConstructors) {
                     final Field field = ReflectionUtils.getField(cc.clazz, paramNames[i]);

                     // check if a corresponding field has been found
                     if (field != null && paramTypes[i].isAssignableFrom(field.getType())) {
                        final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
                        check.setFieldName(field.getName());
                        cc.addConstructorParameterChecks(ctor, i, check);
                     }
                  }
               }
            }
         }

         /* ******************************
          * apply method parameter and return value checks and pre/post conditions
          * ******************************/
         if (classCfg.methodConfigurations != null) {
            for (final MethodConfiguration methodCfg : classCfg.methodConfigurations) {
               /* ******************************
                * determine the method
                * ******************************/
               final Method method;

               if (methodCfg.parameterConfigurations == null || methodCfg.parameterConfigurations.isEmpty()) {
                  method = classCfg.type.getDeclaredMethod(methodCfg.name);
               } else {
                  final Class<?>[] paramTypes = new Class[methodCfg.parameterConfigurations.size()];

                  for (int i = 0, l = methodCfg.parameterConfigurations.size(); i < l; i++) {
                     paramTypes[i] = methodCfg.parameterConfigurations.get(i).type;
                  }

                  method = classCfg.type.getDeclaredMethod(methodCfg.name, paramTypes);
               }

               if (TRUE.equals(methodCfg.overwrite)) {
                  cc.clearMethodChecks(method);
               }

               /* ******************************
                * applying field constraints to the single parameter of setter methods
                * ******************************/
               if (applyFieldConstraintsToSetters) {
                  final Field field = ReflectionUtils.getFieldForSetter(method);

                  // check if a corresponding field has been found
                  if (field != null) {
                     final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
                     check.setFieldName(field.getName());
                     cc.addMethodParameterChecks(method, 0, check);
                  }
               }

               /* ******************************
                * configure parameter constraints
                * ******************************/
               if (methodCfg.parameterConfigurations != null && !methodCfg.parameterConfigurations.isEmpty()) {
                  for (int i = 0, l = methodCfg.parameterConfigurations.size(); i < l; i++) {
                     final ParameterConfiguration paramCfg = methodCfg.parameterConfigurations.get(i);

                     if (TRUE.equals(paramCfg.overwrite)) {
                        cc.clearMethodParameterChecks(method, i);
                     }

                     if (paramCfg.hasChecks()) {
                        cc.addMethodParameterChecks(method, i, paramCfg.checks);
                     }

                     if (paramCfg.hasCheckExclusions()) {
                        cc.addMethodParameterCheckExclusions(method, i, paramCfg.checkExclusions);
                     }

                     if (assertParametersNotNull) {
                        cc.addMethodParameterChecks(method, i, sharedNotNullCheck);
                     }
                  }
               }

               /* ******************************
                * configure return value constraints
                * ******************************/
               if (methodCfg.returnValueConfiguration != null) {
                  if (TRUE.equals(methodCfg.returnValueConfiguration.overwrite)) {
                     cc.clearMethodReturnValueChecks(method);
                  }

                  if (methodCfg.returnValueConfiguration.checks != null && !methodCfg.returnValueConfiguration.checks.isEmpty()) {
                     cc.addMethodReturnValueChecks(method, methodCfg.isInvariant, methodCfg.returnValueConfiguration.checks);
                  }
               }

               if (TRUE.equals(methodCfg.preCheckInvariants)) {
                  cc.methodsWithCheckInvariantsPre.add(method);
               }

               /*
                * configure pre conditions
                */
               if (methodCfg.preExecutionConfiguration != null) {
                  if (TRUE.equals(methodCfg.preExecutionConfiguration.overwrite)) {
                     cc.clearMethodPreChecks(method);
                  }

                  if (methodCfg.preExecutionConfiguration.checks != null && !methodCfg.preExecutionConfiguration.checks.isEmpty()) {
                     cc.addMethodPreChecks(method, methodCfg.preExecutionConfiguration.checks);
                  }
               }

               if (TRUE.equals(methodCfg.postCheckInvariants)) {
                  cc.methodsWithCheckInvariantsPost.add(method);
               }

               /*
                * configure post conditions
                */
               if (methodCfg.postExecutionConfiguration != null) {
                  if (TRUE.equals(methodCfg.postExecutionConfiguration.overwrite)) {
                     cc.clearMethodPostChecks(method);
                  }

                  if (methodCfg.postExecutionConfiguration.checks != null && !methodCfg.postExecutionConfiguration.checks.isEmpty()) {
                     cc.addMethodPostChecks(method, methodCfg.postExecutionConfiguration.checks);
                  }
               }
            }
         }
      } catch (final NoSuchMethodException ex) {
         throw new MethodNotFoundException(ex);
      } catch (final NoSuchFieldException ex) {
         throw new FieldNotFoundException(ex);
      }
   }

   private void _checkConstraint(final Check check, final Object validatedObject, final Object valueToValidate, final InternalValidationCycle cycle) {
      /*
       * special handling of the AssertValid constraint
       */
      if (check instanceof AssertValidCheck) {
         checkConstraintAssertValid(valueToValidate, cycle);
         return;
      }

      /*
       * special handling of the constraint lists
       */
      if (check instanceof ConstraintsCheck) {
         for (final Check innerCheck : ((ConstraintsCheck) check).checks) {
            checkConstraint(innerCheck, validatedObject, valueToValidate, cycle.getContext(), cycle, false);
         }
         return;
      }

      /*
       * special handling of the FieldConstraints constraint
       */
      if (check instanceof AssertConstraintSetCheck) {
         checkConstraintAssertConstraintSet((AssertConstraintSetCheck) check, validatedObject, valueToValidate, cycle);
         return;
      }

      /*
       * special handling of the FieldConstraints constraint
       */
      if (check instanceof AssertFieldConstraintsCheck) {
         checkConstraintAssertFieldConstraints((AssertFieldConstraintsCheck) check, validatedObject, valueToValidate, cycle);
         return;
      }

      /*
       * standard constraints handling
       */
      if (!check.isSatisfied(validatedObject, valueToValidate, cycle)) {
         final String errorMessage = renderMessage(cycle.contextPath, valueToValidate, check.getMessage(), check.getMessageVariables());
         cycle.addConstraintViolation(check, errorMessage, valueToValidate);
      }
   }

   private Class<?> _getContainerElementType(final OValContext containerContext, final int typeArgumentIndex) {
      if (containerContext instanceof FieldContext) {
         final FieldContext ctx = (FieldContext) containerContext;
         return ReflectionUtils.getTypeArgument(ctx.getField(), typeArgumentIndex);
      }
      if (containerContext instanceof MethodParameterContext) {
         final MethodParameterContext ctx = (MethodParameterContext) containerContext;
         return ReflectionUtils.getTypeArgument(ctx.getMethod(), ctx.getParameterIndex(), typeArgumentIndex);
      }
      return null;
   }

   /**
    * Validate validatedObject based on the constraints of the given class.
    */
   private void _validateObjectInvariants(final Object validatedObject, final Class<?> clazz, final InternalValidationCycle cycle)
      throws ValidationFailedException {

      // abort if the root class has been reached
      if (clazz == Object.class)
         return;

      try {
         final ClassChecks cc = getClassChecks(clazz);

         // validate field constraints
         for (final Field field : cc.constrainedFields) {
            final Collection<Check> checks = cc.checksForFields.get(field);

            if (checks != null && !checks.isEmpty()) {
               final FieldContext ctx = ContextCache.getFieldContext(field);
               final Object valueToValidate = resolveValue(ctx, validatedObject);

               for (final Check check : checks) {
                  checkConstraint(check, validatedObject, valueToValidate, ctx, cycle, false);
               }
            }
         }

         // validate constraints on getter methods
         for (final Method getter : cc.constrainedMethods) {
            final Collection<Check> checks = cc.checksForMethodReturnValues.get(getter);

            if (checks != null && !checks.isEmpty()) {
               final MethodReturnValueContext ctx = ContextCache.getMethodReturnValueContext(getter);
               final Object valueToValidate = resolveValue(ctx, validatedObject);

               for (final Check check : checks) {
                  checkConstraint(check, validatedObject, valueToValidate, ctx, cycle, false);
               }
            }
         }

         // validate class-level constraints
         if (!cc.checksForObject.isEmpty()) {
            final ClassContext ctx = ContextCache.getClassContext(clazz);
            for (final Check check : cc.checksForObject) {
               checkConstraint(check, validatedObject, validatedObject, ctx, cycle, false);
            }
         }

         // validate checks applied to the super class (if any)
         _validateObjectInvariants(validatedObject, clazz.getSuperclass(), cycle);
      } catch (final OValException ex) {
         throw new ValidationFailedException("Object validation failed. Class: " + clazz + " Validated object: " + validatedObject, ex);
      }
   }

   /**
    * Validates the static field and static getter constrains of the given class.
    *
    * Constraints specified for super classes are not validated.
    */
   private void _validateStaticInvariants(final Class<?> validatedClass, final InternalValidationCycle cycle) throws ValidationFailedException {

      final ClassChecks cc = getClassChecks(validatedClass);

      // validate static field constraints
      for (final Field field : cc.constrainedStaticFields) {
         final Collection<Check> checks = cc.checksForFields.get(field);

         if (checks != null && !checks.isEmpty()) {
            final FieldContext ctx = ContextCache.getFieldContext(field);
            final Object valueToValidate = resolveValue(ctx, null);

            for (final Check check : checks) {
               checkConstraint(check, validatedClass, valueToValidate, ctx, cycle, false);
            }
         }
      }

      // validate constraints on getter methods
      for (final Method getter : cc.constrainedStaticMethods) {
         final Collection<Check> checks = cc.checksForMethodReturnValues.get(getter);

         if (checks != null && !checks.isEmpty()) {
            final MethodReturnValueContext ctx = ContextCache.getMethodReturnValueContext(getter);
            final Object valueToValidate = resolveValue(ctx, null);

            for (final Check check : checks) {
               checkConstraint(check, validatedClass, valueToValidate, ctx, cycle, false);
            }
         }
      }
   }

   /**
    * Registers object-level constraint checks
    *
    * @param clazz the class to register the checks for
    * @param checks the checks to add
    * @throws IllegalArgumentException if <code>clazz == null</code> or <code>checks == null</code> or checks is empty
    */
   public void addChecks(final Class<?> clazz, final Check... checks) throws IllegalArgumentException {
      Assert.argumentNotNull("clazz", clazz);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(clazz).addObjectChecks(checks);
   }

   /**
    * Registers constraint checks for the given field
    *
    * @param field the field to declare the checks for
    * @param checks the checks to add
    * @throws IllegalArgumentException if <code>field == null</code> or <code>checks == null</code> or checks is empty
    */
   public void addChecks(final Field field, final Check... checks) throws IllegalArgumentException {
      Assert.argumentNotNull("field", field);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(field.getDeclaringClass()).addFieldChecks(field, checks);
   }

   /**
    * Registers constraint checks for the given getter's return value
    *
    * @param invariantMethod a non-void, non-parameterized method (usually a JavaBean Getter style method)
    * @param checks the checks to add
    * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code>
    * @throws InvalidConfigurationException if getter is not a getter method
    */
   public void addChecks(final Method invariantMethod, final Check... checks) throws IllegalArgumentException, InvalidConfigurationException {
      Assert.argumentNotNull("invariantMethod", invariantMethod);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(invariantMethod.getDeclaringClass()).addMethodReturnValueChecks(invariantMethod, TRUE, checks);
   }

   /**
    * Registers a new constraint set.
    *
    * @param constraintSet cannot be null
    * @throws ConstraintSetAlreadyDefinedException if <code>overwrite == false</code> and
    *            a constraint set with the given id exists already
    * @throws IllegalArgumentException if <code>constraintSet == null</code>
    *            or <code>constraintSet.id == null</code>
    *            or <code>constraintSet.id.length == 0</code>
    * @throws IllegalArgumentException if <code>constraintSet.id</code> is <code>null</code> or blank
    */
   public void addConstraintSet(final ConstraintSet constraintSet, final boolean overwrite) throws ConstraintSetAlreadyDefinedException,
      IllegalArgumentException {
      Assert.argumentNotNull("constraintSet", constraintSet);
      Assert.argumentNotBlank("constraintSet.id", constraintSet.getId());

      if (!overwrite && constraintSetsById.containsKey(constraintSet.getId()))
         throw new ConstraintSetAlreadyDefinedException(constraintSet.getId());

      constraintSetsById.put(constraintSet.getId(), constraintSet);
   }

   @Override
   public void assertValid(final Object validatedObject) throws ValidationFailedException, ConstraintsViolatedException {
      final List<ConstraintViolation> violations = validate(validatedObject);
      if (!violations.isEmpty())
         throw translateException(new ConstraintsViolatedException(violations));
   }

   @Override
   public void assertValidFieldValue(final Object validatedObject, final Field validatedField, final Object fieldValueToValidate)
      throws ValidationFailedException, ConstraintsViolatedException {
      final List<ConstraintViolation> violations = validateFieldValue(validatedObject, validatedField, fieldValueToValidate);
      if (!violations.isEmpty())
         throw translateException(new ConstraintsViolatedException(violations));
   }

   /**
    * @param isContainerValue specifies if the value currently validated is an entry of a collection, map or array.
    */
   protected void checkConstraint(final Check check, Object validatedObject, Object valueToValidate, OValContext context, final InternalValidationCycle cycle,
      final boolean isContainerValue) throws OValException {
      if (!(check instanceof ConstraintsCheck) && !isAnyProfileEnabled(check.getProfiles(), cycle.profiles))
         return;

      if (!check.isActive(validatedObject, valueToValidate, cycle))
         return;

      int contextPathElementsAdded = 0;

      cycle.contextPath.add(context);
      contextPathElementsAdded++;

      // only process the target expression if we are not already on a value inside the container object (collection, array, map)
      if (!isContainerValue) {
         String target = check.getTarget();
         if (target != null) {
            target = target.trim();
            if (target.length() > 0) {
               if (valueToValidate == null)
                  return;
               final List<String> chunks = StringUtils.split(target, ':', 2);
               final String ognId, path;
               if (chunks.size() == 1) {
                  ognId = "";
                  path = chunks.get(0);
               } else {
                  ognId = chunks.get(0);
                  path = chunks.get(1);
               }
               final ObjectGraphNavigationResult ognResult = ognRegistry.getObjectGraphNavigator(ognId) //
                  .navigateTo(valueToValidate, path);
               if (ognResult == null)
                  return;

               if (ognResult.path.indexOf('.') > -1) {
                  cycle.contextPath.add(new ObjectGraphNavigationContext(StringUtils.substringBeforeLast(path, '.')));
                  contextPathElementsAdded++;
               }

               validatedObject = ognResult.targetParent;
               valueToValidate = ognResult.target;
               context = ognResult.targetAccessor instanceof Field //
                  ? ContextCache.getFieldContext((Field) ognResult.targetAccessor) //
                  : ContextCache.getMethodReturnValueContext((Method) ognResult.targetAccessor);

               cycle.contextPath.add(context);
               contextPathElementsAdded++;
            }
         }
      }

      final Class<?> compileTimeType = context.getCompileTimeType();

      final boolean isIterable = valueToValidate == null //
         ? compileTimeType != null && Iterable.class.isAssignableFrom(compileTimeType)
         : valueToValidate instanceof Iterable<?>;
      final boolean isMap = !isIterable //
         && (valueToValidate == null //
            ? compileTimeType != null && Map.class.isAssignableFrom(compileTimeType)
            : valueToValidate instanceof Map<?, ?> //
         );
      final boolean isArray = !isIterable //
         && !isMap //
         && (valueToValidate == null //
            ? compileTimeType != null && compileTimeType.isArray()
            : valueToValidate.getClass().isArray() //
         );
      final boolean isContainer = isIterable || isMap || isArray;

      final ConstraintTarget[] targets = check.getAppliesTo();

      if (isContainer && valueToValidate != null) {
         if (isIterable) {
            if (ArrayUtils.containsSame(targets, ConstraintTarget.VALUES) //
               && (!isContainerValue || ArrayUtils.containsSame(targets, ConstraintTarget.RECURSIVE))) {
               int i = 0;
               final Class<?> elementType = _getContainerElementType(context, 0);
               for (final Object item : (Iterable<?>) valueToValidate) {
                  final OValContext ctx = new IterableElementContext(elementType, i);
                  checkConstraint(check, validatedObject, item, ctx, cycle, true);
                  i++;
               }
            }
         } else if (isMap) {
            if (ArrayUtils.containsSame(targets, ConstraintTarget.KEYS) //
               && (!isContainerValue || ArrayUtils.containsSame(targets, ConstraintTarget.RECURSIVE))) {
               final Class<?> elementType = _getContainerElementType(context, 0);
               for (final Object key : ((Map<?, ?>) valueToValidate).keySet()) {
                  final OValContext ctx = new MapKeyContext(elementType, key);
                  checkConstraint(check, validatedObject, key, ctx, cycle, true);
               }
            }
            if (ArrayUtils.containsSame(targets, ConstraintTarget.VALUES) //
               && (!isContainerValue || ArrayUtils.containsSame(targets, ConstraintTarget.RECURSIVE))) {
               final Class<?> elementType = _getContainerElementType(context, 1);
               for (final Entry<?, ?> entry : ((Map<?, ?>) valueToValidate).entrySet()) {
                  final OValContext ctx = new MapValueContext(elementType, entry.getKey());
                  checkConstraint(check, validatedObject, entry.getValue(), ctx, cycle, true);
               }
            }
         } else { // array
            if (ArrayUtils.containsSame(targets, ConstraintTarget.VALUES) //
               && (!isContainerValue || ArrayUtils.containsSame(targets, ConstraintTarget.RECURSIVE))) {
               final Object fValidatedObject = validatedObject;
               final Class<?> elementType = valueToValidate.getClass().getComponentType();
               ArrayUtils.iterate(valueToValidate, (i, item) -> {
                  final OValContext ctx = new IterableElementContext(elementType, i);
                  checkConstraint(check, fValidatedObject, item, ctx, cycle, true);
               });
            }
         }
      }

      if (isContainerValue || !isContainer || isContainer && ArrayUtils.containsSame(targets, ConstraintTarget.CONTAINER)) {
         _checkConstraint(check, validatedObject, valueToValidate, cycle);
      }

      for (int i = 0; i < contextPathElementsAdded; i++) {
         CollectionUtils.removeLast(cycle.contextPath);
      }
   }

   protected void checkConstraintAssertConstraintSet(final AssertConstraintSetCheck check, final Object validatedObject, final Object valueToValidate,
      final InternalValidationCycle cycle) throws OValException {
      final ConstraintSet cs = getConstraintSet(check.getId());

      if (cs == null)
         throw new UndefinedConstraintSetException(check.getId());

      final Collection<Check> referencedChecks = cs.getChecks();
      if (referencedChecks != null && !referencedChecks.isEmpty()) {
         final OValContext context = CollectionUtils.removeLast(cycle.contextPath);
         for (final Check referencedCheck : referencedChecks) {
            checkConstraint(referencedCheck, validatedObject, valueToValidate, context, cycle, false);
         }
         cycle.contextPath.add(context);
      }
   }

   protected void checkConstraintAssertFieldConstraints(final AssertFieldConstraintsCheck check, final Object validatedObject, final Object valueToValidate,
      final InternalValidationCycle cycle) throws OValException {
      final Class<?> targetClass;

      final OValContext context = CollectionUtils.removeLast(cycle.contextPath);

      /*
       * set the targetClass based on the validation context
       */
      if (check.getDeclaringClass() != null && check.getDeclaringClass() != Void.class) {
         targetClass = check.getDeclaringClass();
      } else if (context instanceof ConstructorParameterContext) {
         // the class declaring the field must either be the class declaring the constructor or one of its super
         // classes
         targetClass = ((ConstructorParameterContext) context).getConstructor().getDeclaringClass();
      } else if (context instanceof MethodParameterContext) {
         // the class declaring the field must either be the class declaring the method or one of its super classes
         targetClass = ((MethodParameterContext) context).getMethod().getDeclaringClass();
      } else if (context instanceof MethodReturnValueContext) {
         // the class declaring the field must either be the class declaring the getter or one of its super classes
         targetClass = ((MethodReturnValueContext) context).getMethod().getDeclaringClass();
      } else {
         // the lowest class that is expected to declare the field (or one of its super classes)
         targetClass = validatedObject.getClass();
      }

      // the name of the field whose constraints shall be used
      String fieldName = check.getFieldName();

      /*
       * calculate the field name based on the validation context if the @AssertFieldConstraints constraint didn't specify the field name
       */
      if (fieldName == null || fieldName.length() == 0) {
         if (context instanceof ConstructorParameterContext) {
            fieldName = ((ConstructorParameterContext) context).getParameterName();
         } else if (context instanceof MethodParameterContext) {
            fieldName = ((MethodParameterContext) context).getParameterName();
         } else if (context instanceof MethodReturnValueContext) {
            fieldName = ReflectionUtils.guessFieldName(((MethodReturnValueContext) context).getMethod());
         }
      }

      /*
       * find the field based on fieldName and targetClass
       */
      final Field field = ReflectionUtils.getFieldRecursive(targetClass, fieldName);

      if (field == null)
         throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <" + targetClass + "> or its super classes.");

      final ClassChecks cc = getClassChecks(field.getDeclaringClass());
      final Collection<Check> referencedChecks = cc.checksForFields.get(field);
      if (referencedChecks != null && !referencedChecks.isEmpty()) {
         for (final Check referencedCheck : referencedChecks) {
            checkConstraint(referencedCheck, validatedObject, valueToValidate, context, cycle, false);
         }
      }

      cycle.contextPath.add(context);
   }

   protected void checkConstraintAssertValid(final Object valueToValidate, final InternalValidationCycle cycle) throws OValException {
      if (valueToValidate == null)
         return;

      // ignore circular dependencies
      if (isCurrentlyValidated(valueToValidate))
         return;

      validateInvariants(valueToValidate, cycle);
   }

   /**
    * Disables all constraints profiles globally, i.e. no configured constraint will be validated.
    */
   public synchronized void disableAllProfiles() {
      isProfilesFeatureUsed = true;
      isAllProfilesEnabledByDefault = false;

      enabledProfiles.clear();
      disabledProfiles.clear();
   }

   /**
    * Disables a constraints profile globally.
    *
    * @param profile the id of the profile
    */
   public void disableProfile(final String profile) {
      isProfilesFeatureUsed = true;

      if (isAllProfilesEnabledByDefault) {
         disabledProfiles.add(profile);
      } else {
         enabledProfiles.remove(profile);
      }
   }

   /**
    * Enables all constraints profiles globally, i.e. all configured constraint will be validated.
    */
   public synchronized void enableAllProfiles() {
      isProfilesFeatureUsed = true;
      isAllProfilesEnabledByDefault = true;

      enabledProfiles.clear();
      disabledProfiles.clear();
   }

   /**
    * Enables a constraints profile globally.
    *
    * @param profile the id of the profile
    */
   public void enableProfile(final String profile) {
      isProfilesFeatureUsed = true;

      if (isAllProfilesEnabledByDefault) {
         disabledProfiles.remove(profile);
      } else {
         enabledProfiles.add(profile);
      }
   }

   //CHECKSTYLE:IGNORE NoFinalize FOR NEXT LINE
   @Override
   protected void finalize() throws Throwable {
      try {
         // to lower the risk of potential memory leaks on hot-reloading/redeployment of validated classes
         ContextCache.clear();
      } finally {
         super.finalize();
      }
   }

   /**
    * Gets the object-level constraint checks for the given class
    *
    * @param clazz the class to get the checks for
    * @throws IllegalArgumentException if <code>clazz == null</code>
    */
   public Check[] getChecks(final Class<?> clazz) throws IllegalArgumentException {
      Assert.argumentNotNull("clazz", clazz);

      final ClassChecks cc = getClassChecks(clazz);
      final Set<Check> checks = cc.checksForObject;
      return checks == null ? EMPTY_CHECKS : checks.toArray(new Check[checks.size()]);
   }

   /**
    * Gets the constraint checks for the given field
    *
    * @param field the field to get the checks for
    * @throws IllegalArgumentException if <code>field == null</code>
    */
   public Check[] getChecks(final Field field) throws IllegalArgumentException {
      Assert.argumentNotNull("field", field);

      final ClassChecks cc = getClassChecks(field.getDeclaringClass());
      final Set<Check> checks = cc.checksForFields.get(field);
      return checks == null ? EMPTY_CHECKS : checks.toArray(new Check[checks.size()]);
   }

   /**
    * Gets the constraint checks for the given method's return value
    *
    * @param method the method to get the checks for
    * @throws IllegalArgumentException if <code>getter == null</code>
    */
   public Check[] getChecks(final Method method) throws IllegalArgumentException {
      Assert.argumentNotNull("method", method);

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());
      final Set<Check> checks = cc.checksForMethodReturnValues.get(method);
      return checks == null ? EMPTY_CHECKS : checks.toArray(new Check[checks.size()]);
   }

   /**
    * Returns the ClassChecks object for the particular class, allowing you to modify the checks
    *
    * @param clazz cannot be null
    * @return returns the ClassChecks for the given class
    * @throws IllegalArgumentException if <code>clazz == null</code>
    */
   protected ClassChecks getClassChecks(final Class<?> clazz) throws IllegalArgumentException, InvalidConfigurationException, ReflectionException {
      Assert.argumentNotNull("clazz", clazz);

      return checksByClass.computeIfAbsent(clazz, k -> {
         final ClassChecks newCC = new ClassChecks(k, parameterNameResolver);
         for (final Configurer configurer : configurers) {
            final ClassConfiguration classConfig = configurer.getClassConfiguration(k);
            if (classConfig != null) {
               _addChecks(newCC, classConfig);
            }
         }
         return newCC;
      });
   }

   /**
    * Returns the given constraint set.
    *
    * @param constraintSetId the id of the constraint set to retrieve
    * @return the constraint set or null if not found
    * @throws IllegalArgumentException if <code>constraintSetId</code> is null
    */
   public ConstraintSet getConstraintSet(final String constraintSetId) throws InvalidConfigurationException, IllegalArgumentException {
      Assert.argumentNotNull("constraintSetId", constraintSetId);

      ConstraintSet cs = constraintSetsById.get(constraintSetId);
      if (cs == null) {
         for (final Configurer configurer : configurers) {
            final ConstraintSetConfiguration csc = configurer.getConstraintSetConfiguration(constraintSetId);
            if (csc != null) {
               cs = new ConstraintSet(csc.id);
               cs.setChecks(csc.checks);

               addConstraintSet(cs, csc.overwrite != null && csc.overwrite);
            }
         }
      }
      return cs;
   }

   public ExceptionTranslator getExceptionTranslator() {
      return exceptionTranslator;
   }

   public ExpressionLanguageRegistry getExpressionLanguageRegistry() {
      return expressionLanguageRegistry;
   }

   public ObjectGraphNavigatorRegistry getObjectGraphNavigatorRegistry() {
      return ognRegistry;
   }

   /**
    * Determines if at least one of the given profiles is enabled
    *
    * @param enabledProfiles optional array of profiles (can be null)
    * @return Returns true if at least one of the given profiles is enabled.
    */
   protected boolean isAnyProfileEnabled(final String[] profilesOfCheck, final String[] enabledProfiles) {
      if (enabledProfiles == null) {
         // use the global profile configuration
         if (profilesOfCheck == null || profilesOfCheck.length == 0)
            return isProfileEnabled("default");

         for (final String profile : profilesOfCheck)
            if (isProfileEnabled(profile))
               return true;
      } else {
         // use the local profile configuration
         if (profilesOfCheck == null || profilesOfCheck.length == 0)
            return ArrayUtils.containsEqual(enabledProfiles, "default");

         for (final String profile : profilesOfCheck)
            if (ArrayUtils.containsEqual(enabledProfiles, profile))
               return true;
      }
      return false;
   }

   /**
    * Determines if the given object is currently validated in the current thread
    *
    * @return Returns true if the given object is currently validated in the current thread.
    */
   protected boolean isCurrentlyValidated(final Object object) {
      Assert.argumentNotNull("object", object);

      final LinkedList<InternalValidationCycle> cycles = currentValidationCycles.get();
      return !cycles.isEmpty() && cycles.getLast().validatedObjects.contains(object);
   }

   /**
    * Determines if the given profile is enabled.
    *
    * @return Returns true if the given profile is enabled.
    */
   public boolean isProfileEnabled(final String profileId) {
      Assert.argumentNotNull("profileId", profileId);

      if (isProfilesFeatureUsed) {
         if (isAllProfilesEnabledByDefault)
            return !disabledProfiles.contains(profileId);

         return enabledProfiles.contains(profileId);
      }
      return true;
   }

   /**
    * clears the checks and constraint sets => a reconfiguration using the
    * currently registered configurers will automatically happen
    */
   public void reconfigureChecks() {
      checksByClass.clear();
      constraintSetsById.clear();
   }

   /**
    * Removes object-level constraint checks
    *
    * @throws IllegalArgumentException if <code>clazz == null</code> or <code>checks == null</code> or checks is empty
    */
   public void removeChecks(final Class<?> clazz, final Check... checks) throws IllegalArgumentException {
      Assert.argumentNotNull("clazz", clazz);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(clazz).removeObjectChecks(checks);
   }

   /**
    * Removes constraint checks for the given field
    *
    * @throws IllegalArgumentException if <code>field == null</code> or <code>checks == null</code> or checks is empty
    */
   public void removeChecks(final Field field, final Check... checks) throws IllegalArgumentException {
      Assert.argumentNotNull("field", field);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(field.getDeclaringClass()).removeFieldChecks(field, checks);
   }

   /**
    * Removes constraint checks for the given getter's return value
    *
    * @param getter a JavaBean Getter style method
    * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code>
    */
   public void removeChecks(final Method getter, final Check... checks) throws IllegalArgumentException {
      Assert.argumentNotNull("getter", getter);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(getter.getDeclaringClass()).removeMethodReturnValueChecks(getter, checks);
   }

   /**
    * Removes the constraint set with the given id
    *
    * @param id the id of the constraint set to remove, cannot be null
    * @return the removed constraint set
    * @throws IllegalArgumentException if <code>id == null</code>
    */
   public ConstraintSet removeConstraintSet(final String id) throws IllegalArgumentException {
      Assert.argumentNotNull("id", id);

      return constraintSetsById.remove(id);
   }

   protected String renderMessage(final List<OValContext> contextPath, final Object invalidValue, final String messageKey, final Map<String, ?> messageValues) {
      String message = MessageRenderer.renderMessage(messageKey, messageValues);

      // if there are no place holders in the message simply return it
      if (message.indexOf('{') == -1)
         return message;

      message = StringUtils.replaceAll(message, "{context}", contextRenderer.render(contextPath));
      message = StringUtils.replaceAll(message, "{invalidValue}", messageValueFormatter.format(invalidValue));

      return message;
   }

   /**
    * Reports an additional constraint violation for the current validation cycle.
    * This method is intended to be executed by check implementations only.
    *
    * @param constraintViolation the constraint violation
    * @throws IllegalStateException if no validation is currently in progress by the current thread
    *
    * @deprecated use {@link ValidationCycle#addConstraintViolation(Check, String, Object)} or
    *             {@link ValidationCycle#addConstraintViolation(ConstraintViolation)}
    */
   @Deprecated
   public void reportConstraintViolation(final ConstraintViolation constraintViolation) throws IllegalStateException {
      Assert.argumentNotNull("constraintViolation", constraintViolation);

      if (currentValidationCycles.get().isEmpty())
         throw new IllegalStateException("No active validation cycle found for the current thread.");
      currentValidationCycles.get().getLast().addConstraintViolation(constraintViolation);
   }

   /**
    * @param validatedObject may be null for static fields
    */
   protected Object resolveValue(final FieldContext ctx, final Object validatedObject) {
      return ReflectionUtils.getFieldValue(ctx.getField(), validatedObject);
   }

   /**
    * @param validatedObject may be null for static methods
    */
   protected Object resolveValue(final MethodReturnValueContext ctx, final Object validatedObject) {
      return ReflectionUtils.invokeMethod(ctx.getMethod(), validatedObject);
   }

   public void setExceptionTranslator(final ExceptionTranslator exceptionTranslator) {
      this.exceptionTranslator = exceptionTranslator;
   }

   protected RuntimeException translateException(final OValException ex) {
      if (exceptionTranslator != null) {
         final RuntimeException rex = exceptionTranslator.translateException(ex);
         if (rex != null)
            return rex;
      }
      return ex;
   }

   @Override
   public List<ConstraintViolation> validate(final Object validatedObject) throws ValidationFailedException {
      Assert.argumentNotNull("validatedObject", validatedObject);

      final InternalValidationCycle cycle = new InternalValidationCycle(validatedObject, null);
      currentValidationCycles.get().add(cycle);
      try {
         validateInvariants(validatedObject, cycle);
         return cycle.violations;
      } finally {
         currentValidationCycles.get().removeLast();
      }
   }

   @Override
   public List<ConstraintViolation> validate(final Object validatedObject, final String... profiles) throws ValidationFailedException {
      Assert.argumentNotNull("validatedObject", validatedObject);

      final InternalValidationCycle cycle = new InternalValidationCycle(validatedObject, profiles);
      currentValidationCycles.get().add(cycle);
      try {
         validateInvariants(validatedObject, cycle);
         return cycle.violations;
      } finally {
         currentValidationCycles.get().removeLast();
      }
   }

   @Override
   public List<ConstraintViolation> validateFieldValue(final Object validatedObject, final Field validatedField, final Object fieldValueToValidate)
      throws ValidationFailedException {
      Assert.argumentNotNull("validatedObject", validatedObject);
      Assert.argumentNotNull("validatedField", validatedField);

      final InternalValidationCycle cycle = new InternalValidationCycle(validatedObject, null);
      currentValidationCycles.get().add(cycle);
      try {
         final ClassChecks cc = getClassChecks(validatedField.getDeclaringClass());
         final Collection<Check> checks = cc.checksForFields.get(validatedField);

         if (checks == null || checks.isEmpty())
            return cycle.violations;

         final FieldContext context = ContextCache.getFieldContext(validatedField);

         for (final Check check : checks) {
            checkConstraint(check, validatedObject, fieldValueToValidate, context, cycle, false);
         }
         return cycle.violations;
      } catch (final OValException ex) {
         throw new ValidationFailedException("Field validation failed. Field: " + validatedField + " Validated object: " + validatedObject, ex);
      } finally {
         currentValidationCycles.get().removeLast();
      }
   }

   /**
    * Validates the field and getter constrains of the given object.
    *
    * If the given object is a class the static fields and getters are validated.
    */
   protected void validateInvariants(final Object validatedObject, final InternalValidationCycle cycle) throws ValidationFailedException {
      currentValidationCycles.get().getLast().validatedObjects.add(validatedObject);
      if (validatedObject instanceof Class<?>) {
         _validateStaticInvariants((Class<?>) validatedObject, cycle);
      } else {
         _validateObjectInvariants(validatedObject, validatedObject.getClass(), cycle);
      }
   }
}
