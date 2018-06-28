/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodExitContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.ValidationFailedException;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.internal.ClassChecks;
import net.sf.oval.internal.ContextCache;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.ParameterChecks;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.IdentitySet;
import net.sf.oval.internal.util.Invocable;
import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.ThreadLocalList;
import net.sf.oval.internal.util.ThreadLocalWeakHashMap;

/**
 * Extended version of the validator to realize programming by contract.
 *
 * @author Sebastian Thomschke
 */
public class Guard extends Validator {
   /**
    * <b>Note:</b> Only required until AspectJ allows throwing of checked exceptions
    */
   protected static final class GuardMethodPreResult {
      protected final boolean checkInvariants;
      protected final Method method;
      protected final Object[] args;
      protected final ClassChecks cc;
      protected final List<ConstraintViolation> violations;
      protected final Map<PostCheck, Object> postCheckOldValues;
      protected final Object guardedObject;

      protected GuardMethodPreResult( //
         final Object guardedObject, //
         final Method method, //
         final Object[] args, //
         final ClassChecks cc, //
         final boolean checkInvariants, //
         final Map<PostCheck, Object> postCheckOldValues, //
         final List<ConstraintViolation> violations //
      ) {
         this.guardedObject = guardedObject;
         this.method = method;
         this.args = args;
         this.cc = cc;
         this.checkInvariants = checkInvariants;
         this.postCheckOldValues = postCheckOldValues;
         this.violations = violations;
      }
   }

   /**
    * <b>Note:</b> Only required until AspectJ allows throwing of checked exceptions
    */
   protected static final GuardMethodPreResult DO_NOT_PROCEED = new GuardMethodPreResult(null, null, null, null, false, null, null);

   private static final Log LOG = Log.getLog(Guard.class);

   /**
    * string based on validated object hashcode + method hashcode for currently validated method return values
    */
   private static final ThreadLocalList<String> CURRENTLY_CHECKED_METHOD_RETURN_VALUES = new ThreadLocalList<String>();

   /**
    * string based on validated object hashcode + method hashcode for currently validated method pre-conditions
    */
   private static final ThreadLocalList<String> CURRENTLY_CHECKED_PRE_CONDITIONS = new ThreadLocalList<String>();

   /**
    * string based on validated object hashcode + method hashcode for currently validated method post-conditions
    */
   private static final ThreadLocalList<String> CURRENTLY_CHECKED_POST_CONDITIONS = new ThreadLocalList<String>();

   private boolean isActivated = true;
   private boolean isInvariantsEnabled = true;
   private boolean isPreConditionsEnabled = true;
   private boolean isPostConditionsEnabled = true;

   /**
    * Flag that indicates if any listeners were registered at any time. Used for improved performance.
    */
   private boolean isListenersFeatureUsed = false;
   /**
    * Flag that indicates if exception suppressing was used at any time. Used for improved performance.
    */
   private boolean isProbeModeFeatureUsed = false;

   private final Set<ConstraintsViolatedListener> listeners = new IdentitySet<ConstraintsViolatedListener>(4);

   private final Map<Class<?>, Set<ConstraintsViolatedListener>> listenersByClass = new WeakHashMap<Class<?>, Set<ConstraintsViolatedListener>>(4);

   private final Map<Object, Set<ConstraintsViolatedListener>> listenersByObject = new WeakHashMap<Object, Set<ConstraintsViolatedListener>>(4);

   /**
    * Objects for OVal suppresses occurring ConstraintViolationExceptions for pre condition violations on setter methods
    * for the current thread.
    */
   private final ThreadLocalWeakHashMap<Object, ProbeModeListener> objectsInProbeMode = new ThreadLocalWeakHashMap<Object, ProbeModeListener>();

   /**
    * Constructs a new guard object and uses a new instance of AnnotationsConfigurer
    */
   public Guard() {
      super();
   }

   public Guard(final Collection<Configurer> configurers) {
      super(configurers);
   }

   public Guard(final Configurer... configurers) {
      super(configurers);
   }

   private List<CheckExclusion> _getActiveExclusions(final Set<CheckExclusion> exclusions) {
      final List<CheckExclusion> activeExclusions = new LinkedList<CheckExclusion>(exclusions);
      for (final Iterator<CheckExclusion> it = activeExclusions.iterator(); it.hasNext();) {
         final CheckExclusion exclusion = it.next();
         if (!isAnyProfileEnabled(exclusion.getProfiles(), null)) {
            it.remove();
         }
      }
      return activeExclusions.size() == 0 ? null : activeExclusions;
   }

   private void _validateParameterChecks(final ParameterChecks checks, final Object validatedObject, final Object valueToValidate, final OValContext context,
      final List<ConstraintViolation> violations) {
      // determine the active exclusions based on the active profiles
      final List<CheckExclusion> activeExclusions = checks.hasExclusions() ? _getActiveExclusions(checks.checkExclusions) : null;

      // check the constraints
      for (final Check check : checks.checks) {
         boolean skip = false;

         if (activeExclusions != null) {
            for (final CheckExclusion exclusion : activeExclusions)
               if (exclusion.isActive(validatedObject, valueToValidate, this) && exclusion.isCheckExcluded(check, validatedObject, valueToValidate, context,
                  this)) {
                  // skip if this check should be excluded
                  skip = true;
                  continue;
               }
         }
         if (!skip) {
            checkConstraint(violations, check, validatedObject, valueToValidate, context, null, false);
         }
      }
   }

   /**
    * Registers constraint checks for the given constructor parameter
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>exclusions == null</code> or exclusions is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void addCheckExclusions(final Constructor<?> ctor, final int paramIndex, final CheckExclusion... exclusions) throws IllegalArgumentException,
      InvalidConfigurationException {
      Assert.argumentNotNull("ctor", ctor);
      Assert.argumentNotEmpty("exclusions", exclusions);

      getClassChecks(ctor.getDeclaringClass()).addConstructorParameterCheckExclusions(ctor, paramIndex, exclusions);
   }

   /**
    * Registers constraint checks for the given method parameter
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>exclusions == null</code> or exclusions is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void addCheckExclusions(final Method method, final int paramIndex, final CheckExclusion... exclusions) throws IllegalArgumentException,
      InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("exclusions", exclusions);

      getClassChecks(method.getDeclaringClass()).addMethodParameterCheckExclusions(method, paramIndex, exclusions);
   }

   /**
    * Registers constraint checks for the given constructor parameter
    *
    * @throws IllegalArgumentException if <code>constructor == null</code> or <code>checks == null</code> or checks is
    *            empty
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void addChecks(final Constructor<?> ctor, final int paramIndex, final Check... checks) throws IllegalArgumentException,
      InvalidConfigurationException {
      Assert.argumentNotNull("ctor", ctor);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(ctor.getDeclaringClass()).addConstructorParameterChecks(ctor, paramIndex, checks);
   }

   /**
    * Registers constraint checks for the given method's return value
    *
    * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if method does not declare a return type (void), or the declaring class is
    *            not guarded
    */
   @Override
   public void addChecks(final Method method, final Check... checks) throws IllegalArgumentException, InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).addMethodReturnValueChecks(method, null, checks);
   }

   /**
    * Registers constraint checks for the given method parameter
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void addChecks(final Method method, final int paramIndex, final Check... checks) throws IllegalArgumentException, InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).addMethodParameterChecks(method, paramIndex, checks);
   }

   /**
    * Registers post condition checks to a method's return value
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded
    */
   public void addChecks(final Method method, final PostCheck... checks) throws IllegalArgumentException, InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).addMethodPostChecks(method, checks);
   }

   /**
    * Registers pre condition checks to a method's return value
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded
    */
   public void addChecks(final Method method, final PreCheck... checks) throws IllegalArgumentException, InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).addMethodPreChecks(method, checks);
   }

   /**
    * Registers the given listener for <b>all</b> thrown ConstraintViolationExceptions
    *
    * @param listener the listener to register
    * @return <code>true</code> if the listener was not yet registered
    * @throws IllegalArgumentException if <code>listener == null</code>
    */
   public boolean addListener(final ConstraintsViolatedListener listener) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);

      isListenersFeatureUsed = true;
      return listeners.add(listener);
   }

   /**
    * Registers the given listener for all thrown ConstraintViolationExceptions on objects of the given class
    *
    * @param listener the listener to register
    * @param guardedClass guarded class or interface
    * @return <code>true</code> if the listener was not yet registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code>
    */
   public boolean addListener(final ConstraintsViolatedListener listener, final Class<?> guardedClass) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedClass", guardedClass);

      isListenersFeatureUsed = true;

      synchronized (listenersByClass) {
         Set<ConstraintsViolatedListener> classListeners = listenersByClass.get(guardedClass);

         if (classListeners == null) {
            classListeners = getCollectionFactory().createSet();
            listenersByClass.put(guardedClass, classListeners);
         }
         return classListeners.add(listener);
      }
   }

   /**
    * Registers the given listener for all thrown ConstraintViolationExceptions on objects of the given object
    *
    * @param listener the listener to register
    * @return <code>true</code> if the listener was not yet registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code>
    */
   public boolean addListener(final ConstraintsViolatedListener listener, final Object guardedObject) {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedObject", guardedObject);

      isListenersFeatureUsed = true;

      synchronized (listenersByObject) {
         Set<ConstraintsViolatedListener> objectListeners = listenersByObject.get(guardedObject);

         if (objectListeners == null) {
            objectListeners = getCollectionFactory().createSet(2);
            listenersByObject.put(guardedObject, objectListeners);
         }
         return objectListeners.add(listener);
      }
   }

   /**
    * Evaluates the old expression
    *
    * @return null if no violation, otherwise a list
    */
   protected Map<PostCheck, Object> calculateMethodPostOldValues(final Object validatedObject, final Method method, final Object[] args)
      throws ValidationFailedException {
      try {
         final ClassChecks cc = getClassChecks(method.getDeclaringClass());
         final Set<PostCheck> postChecks = cc.checksForMethodsPostExcecution.get(method);

         // shortcut: check if any post checks for this method exist
         if (postChecks == null)
            return null;

         final String[] parameterNames = parameterNameResolver.getParameterNames(method);
         final boolean hasParameters = parameterNames.length > 0;

         final Map<PostCheck, Object> oldValues = getCollectionFactory().createMap(postChecks.size());

         for (final PostCheck check : postChecks)
            if (isAnyProfileEnabled(check.getProfiles(), null) && check.getOld() != null && check.getOld().length() > 0) {
               final ExpressionLanguage eng = expressionLanguageRegistry.getExpressionLanguage(check.getLanguage());
               final Map<String, Object> values = getCollectionFactory().createMap();
               values.put("_this", validatedObject);
               if (hasParameters) {
                  values.put("_args", args);
                  for (int i = 0; i < args.length; i++) {
                     values.put(parameterNames[i], args[i]);
                  }
               } else {
                  values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);
               }

               oldValues.put(check, eng.evaluate(check.getOld(), values));
            }

         return oldValues;
      } catch (final OValException ex) {
         throw new ValidationFailedException("Method post conditions validation failed. Method: " + method + " Validated object: " + validatedObject, ex);
      }
   }

   /**
    * Disables the probe mode for the given object in the current thread.
    *
    * @param guardedObject the object to disable the probe mode for
    * @throws IllegalArgumentException if <code>guardedObject == null</code>
    * @throws IllegalStateException in case probe mode was not enabled for the given object
    */
   public ProbeModeListener disableProbeMode(final Object guardedObject) throws IllegalArgumentException, IllegalStateException {
      Assert.argumentNotNull("guardedObject", guardedObject);

      return objectsInProbeMode.get().remove(guardedObject);
   }

   /**
    * Enables the probe mode for the given object in the current thread. In probe mode calls to methods of an
    * object are not actually executed. OVal only validates method pre-conditions and notifies
    * ConstraintViolationListeners but does not throw ConstraintViolationExceptions. Methods with return values will
    * return null.
    *
    * @param guardedObject the object to enable the probe mode for
    * @throws IllegalArgumentException if <code>guardedObject == null</code>
    * @throws IllegalStateException if the probe mode is already enabled
    */
   public void enableProbeMode(final Object guardedObject) throws IllegalArgumentException, IllegalStateException {
      Assert.argumentNotNull("guardedObject", guardedObject);

      if (guardedObject instanceof Class<?>) {
         LOG.warn("Enabling probe mode for a class looks like a programming error. Class: {1}", guardedObject);
      }
      isProbeModeFeatureUsed = true;

      if (objectsInProbeMode.get().get(guardedObject) != null)
         throw new IllegalStateException("The object is already in probe mode.");

      objectsInProbeMode.get().put(guardedObject, new ProbeModeListener(guardedObject));
   }

   /**
    * Returns the registers constraint pre condition checks for the given method parameter
    *
    * @throws IllegalArgumentException if <code>method == null</code>
    */
   public Check[] getChecks(final Method method, final int paramIndex) throws InvalidConfigurationException {
      Assert.argumentNotNull("method", method);

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());

      final Map<Integer, ParameterChecks> checks = cc.checksForMethodParameters.get(method);
      if (checks == null)
         return null;

      final ParameterChecks paramChecks = checks.get(paramIndex);
      return paramChecks == null ? null : paramChecks.checks.toArray(new Check[checks.size()]);
   }

   /**
    * Returns the registered post condition checks for the given method
    *
    * @throws IllegalArgumentException if <code>method == null</code>
    */
   public PostCheck[] getChecksPost(final Method method) throws IllegalArgumentException {
      Assert.argumentNotNull("method", method);

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());

      final Set<PostCheck> checks = cc.checksForMethodsPostExcecution.get(method);
      return checks == null ? null : checks.toArray(new PostCheck[checks.size()]);
   }

   /**
    * Returns the registered pre condition checks for the given method.
    *
    * @throws IllegalArgumentException if <code>method == null</code>
    */
   public PreCheck[] getChecksPre(final Method method) throws IllegalArgumentException {
      Assert.argumentNotNull("method", method);

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());

      final Set<PreCheck> checks = cc.checksForMethodsPreExecution.get(method);
      return checks == null ? null : checks.toArray(new PreCheck[checks.size()]);
   }

   public ParameterNameResolver getParameterNameResolver() {
      return parameterNameResolver;
   }

   /**
    * This method is provided for use by guard aspects.
    */
   protected void guardConstructorPost(final Object guardedObject, final Constructor<?> ctor, @SuppressWarnings("unused") final Object[] args)
      throws ConstraintsViolatedException, ValidationFailedException {
      if (!isActivated)
         return;

      final ClassChecks cc = getClassChecks(ctor.getDeclaringClass());

      // check invariants
      if (isInvariantsEnabled && cc.isCheckInvariants || cc.methodsWithCheckInvariantsPost.contains(ctor)) {
         final List<ConstraintViolation> violations = getCollectionFactory().createList();
         currentViolations.get().add(violations);
         try {
            validateInvariants(guardedObject, violations, null);
         } catch (final ValidationFailedException ex) {
            throw translateException(ex);
         } finally {
            currentViolations.get().removeLast();
         }

         if (violations.size() > 0) {
            final ConstraintsViolatedException violationException = new ConstraintsViolatedException(violations);
            if (isListenersFeatureUsed) {
               notifyListeners(guardedObject, violationException);
            }

            throw translateException(violationException);
         }
      }
   }

   /**
    * This method is provided for use by guard aspects.
    *
    * @throws ConstraintsViolatedException if anything precondition is not satisfied
    */
   protected void guardConstructorPre(final Object guardedObject, final Constructor<?> ctor, final Object[] args) throws ConstraintsViolatedException,
      ValidationFailedException {
      if (!isActivated)
         return;

      // constructor parameter validation
      if (isPreConditionsEnabled && args.length > 0) {
         final List<ConstraintViolation> violations;
         try {
            violations = validateConstructorParameters(guardedObject, ctor, args);
         } catch (final ValidationFailedException ex) {
            throw translateException(ex);
         }

         if (violations != null) {
            final ConstraintsViolatedException violationException = new ConstraintsViolatedException(violations);
            if (isListenersFeatureUsed) {
               notifyListeners(guardedObject, violationException);
            }

            throw translateException(violationException);
         }
      }
   }

   /**
    * This method is provided for use by guard aspects.
    *
    * @return The method return value or null if the guarded object is in probe mode.
    * @throws ConstraintsViolatedException if an constraint violation occurs and the validated object is not in probe mode.
    */
   protected Object guardMethod(Object guardedObject, final Method method, final Object[] args, final Invocable invocable) throws Throwable {
      if (!isActivated)
         return invocable.invoke();

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());

      final boolean checkInvariants = isInvariantsEnabled && cc.isCheckInvariants && !ReflectionUtils.isPrivate(method) && !ReflectionUtils.isProtected(method);

      // if static method use the declaring class as guardedObject
      if (guardedObject == null && ReflectionUtils.isStatic(method)) {
         guardedObject = method.getDeclaringClass();
      }

      final List<ConstraintViolation> violations = getCollectionFactory().createList();
      currentViolations.get().add(violations);

      try {
         // check invariants
         if (checkInvariants || cc.methodsWithCheckInvariantsPre.contains(method)) {
            validateInvariants(guardedObject, violations, null);
         }

         if (isPreConditionsEnabled) {
            // method parameter validation
            if (violations.size() == 0 && args.length > 0) {
               validateMethodParameters(guardedObject, method, args, violations);
            }

            // @Pre validation
            if (violations.size() == 0) {
               validateMethodPre(guardedObject, method, args, violations);
            }
         }
      } catch (final ValidationFailedException ex) {
         throw translateException(ex);
      } finally {
         currentViolations.get().removeLast();
      }

      final ProbeModeListener pml = isProbeModeFeatureUsed ? objectsInProbeMode.get().get(guardedObject) : null;
      if (pml != null) {
         pml.onMethodCall(method, args);
      }

      if (violations.size() > 0) {
         final ConstraintsViolatedException violationException = new ConstraintsViolatedException(violations);
         if (isListenersFeatureUsed) {
            notifyListeners(guardedObject, violationException);
         }

         // don't throw an exception if the method is a setter and suppressing for precondition is enabled
         if (pml != null) {
            pml.onConstraintsViolatedException(violationException);
            return null;
         }

         throw translateException(violationException);
      }

      // abort method execution if in probe mode
      if (pml != null)
         return null;

      final Map<PostCheck, Object> postCheckOldValues = calculateMethodPostOldValues(guardedObject, method, args);

      final Object returnValue = invocable.invoke();

      currentViolations.get().add(violations);

      try {
         // check invariants if executed method is not private
         if (checkInvariants || cc.methodsWithCheckInvariantsPost.contains(method)) {
            validateInvariants(guardedObject, violations, null);
         }

         if (isPostConditionsEnabled) {

            // method return value
            if (violations.size() == 0) {
               validateMethodReturnValue(guardedObject, method, returnValue, violations);
            }

            // @Post
            if (violations.size() == 0) {
               validateMethodPost(guardedObject, method, args, returnValue, postCheckOldValues, violations);
            }
         }
      } catch (final ValidationFailedException ex) {
         throw translateException(ex);
      } finally {
         currentViolations.get().removeLast();
      }

      if (violations.size() > 0) {
         final ConstraintsViolatedException violationException = new ConstraintsViolatedException(violations);
         if (isListenersFeatureUsed) {
            notifyListeners(guardedObject, violationException);
         }

         throw translateException(violationException);
      }

      return returnValue;
   }

   /**
    * <b>Note:</b> Only required until AspectJ allows throwing of checked exceptions,
    * then {@link #guardMethod(Object, Method, Object[], Invocable)} can be used instead
    *
    * This method is provided for use by guard aspects.
    *
    * @throws ConstraintsViolatedException if an constraint violation occurs and the validated object is not in probe mode.
    */
   protected void guardMethodPost(final Object returnValue, final GuardMethodPreResult preResult) throws ConstraintsViolatedException,
      ValidationFailedException {
      if (!isActivated)
         return;

      try {
         // check invariants if executed method is not private
         if (preResult.checkInvariants || preResult.cc.methodsWithCheckInvariantsPost.contains(preResult.method)) {
            validateInvariants(preResult.guardedObject, preResult.violations, null);
         }

         if (isPostConditionsEnabled) {

            // method return value
            if (preResult.violations.size() == 0) {
               validateMethodReturnValue(preResult.guardedObject, preResult.method, returnValue, preResult.violations);
            }

            // @Post
            if (preResult.violations.size() == 0) {
               validateMethodPost(preResult.guardedObject, preResult.method, preResult.args, returnValue, preResult.postCheckOldValues, preResult.violations);
            }
         }
      } catch (final ValidationFailedException ex) {
         throw translateException(ex);
      }

      if (preResult.violations.size() > 0) {
         final ConstraintsViolatedException violationException = new ConstraintsViolatedException(preResult.violations);
         if (isListenersFeatureUsed) {
            notifyListeners(preResult.guardedObject, violationException);
         }

         throw translateException(violationException);
      }
   }

   /**
    * <b>Note:</b> Only required until AspectJ allows throwing of checked exceptions, then {@link #guardMethod(Object, Method, Object[], Invocable)} can be
    * used instead
    *
    * This method is provided for use by guard aspects.
    *
    * @return Null if method guarding is deactivated or a result object that needs to be passed to {@link #guardMethodPost(Object, GuardMethodPreResult)}
    * @throws ConstraintsViolatedException if an constraint violation occurs and the validated object is not in probe mode.
    */
   protected GuardMethodPreResult guardMethodPre(Object guardedObject, final Method method, final Object[] args) throws ConstraintsViolatedException,
      ValidationFailedException {
      if (!isActivated)
         return null;

      final ClassChecks cc = getClassChecks(method.getDeclaringClass());

      final boolean checkInvariants = isInvariantsEnabled && cc.isCheckInvariants && !ReflectionUtils.isPrivate(method) && !ReflectionUtils.isProtected(method);

      // if static method use the declaring class as guardedObject
      if (guardedObject == null && ReflectionUtils.isStatic(method)) {
         guardedObject = method.getDeclaringClass();
      }

      final List<ConstraintViolation> violations = getCollectionFactory().createList();
      currentViolations.get().add(violations);

      try {
         // check invariants
         if (checkInvariants || cc.methodsWithCheckInvariantsPre.contains(method)) {
            validateInvariants(guardedObject, violations, null);
         }

         if (isPreConditionsEnabled) {
            // method parameter validation
            if (violations.size() == 0 && args.length > 0) {
               validateMethodParameters(guardedObject, method, args, violations);
            }

            // @Pre validation
            if (violations.size() == 0) {
               validateMethodPre(guardedObject, method, args, violations);
            }
         }
      } catch (final ValidationFailedException ex) {
         throw translateException(ex);
      } finally {
         currentViolations.get().removeLast();
      }

      final ProbeModeListener pml = isProbeModeFeatureUsed ? objectsInProbeMode.get().get(guardedObject) : null;
      if (pml != null) {
         pml.onMethodCall(method, args);
      }

      if (violations.size() > 0) {
         final ConstraintsViolatedException violationException = new ConstraintsViolatedException(violations);
         if (isListenersFeatureUsed) {
            notifyListeners(guardedObject, violationException);
         }

         // don't throw an exception if the method is a setter and suppressing for precondition is enabled
         if (pml != null) {
            pml.onConstraintsViolatedException(violationException);
            return DO_NOT_PROCEED;
         }

         throw translateException(violationException);
      }

      // abort method execution if in probe mode
      if (pml != null)
         return DO_NOT_PROCEED;

      final Map<PostCheck, Object> postCheckOldValues = calculateMethodPostOldValues(guardedObject, method, args);

      return new GuardMethodPreResult(guardedObject, method, args, cc, checkInvariants, postCheckOldValues, violations);
   }

   /**
    * @return <code>true</code> if the listener is registered
    * @throws IllegalArgumentException if <code>listener == null</code>
    */
   public boolean hasListener(final ConstraintsViolatedListener listener) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);

      return listeners.contains(listener);
   }

   /**
    * @param guardedClass guarded class or interface
    * @return <code>true</code> if the listener is registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code>
    */
   public boolean hasListener(final ConstraintsViolatedListener listener, final Class<?> guardedClass) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedClass", guardedClass);

      final Set<ConstraintsViolatedListener> classListeners = listenersByClass.get(guardedClass);

      if (classListeners == null)
         return false;

      return classListeners.contains(listener);
   }

   /**
    * @return <code>true</code> if the listener is registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code>
    */
   public boolean hasListener(final ConstraintsViolatedListener listener, final Object guardedObject) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedObject", guardedObject);

      final Set<ConstraintsViolatedListener> objectListeners = listenersByObject.get(guardedObject);

      if (objectListeners == null)
         return false;

      return objectListeners.contains(listener);
   }

   public boolean isActivated() {
      return isActivated;
   }

   /**
    * Determines if the probe mode is enabled for the given object in the current thread. In probe mode calls to
    * methods of an object are not actually executed. OVal only validates method pre-conditions and notifies
    * ConstraintViolationListeners but does not throw ConstraintViolationExceptions. Methods with return values will
    * return null.
    *
    * @return true if exceptions are suppressed
    */
   public boolean isInProbeMode(final Object guardedObject) {
      // guardedObject may be null if isInProbeMode is called when validating pre conditions of a static method
      if (guardedObject == null)
         return false;

      return objectsInProbeMode.get().containsKey(guardedObject);
   }

   /**
    * Determines if invariants are checked prior and after every call to a non-private method or constructor.
    */
   public boolean isInvariantsEnabled() {
      return isInvariantsEnabled;
   }

   /**
    * Determines if invariants are checked prior and after every call to a non-private method or constructor.
    *
    * @param guardedClass the guarded class
    */
   public boolean isInvariantsEnabled(final Class<?> guardedClass) {
      return getClassChecks(guardedClass).isCheckInvariants;
   }

   public boolean isPostConditionsEnabled() {
      return isPostConditionsEnabled;
   }

   public boolean isPreConditionsEnabled() {
      return isPreConditionsEnabled;
   }

   /**
    * notifies all registered validation listener about the occurred constraint violation exception
    */
   protected void notifyListeners(final Object guardedObject, final ConstraintsViolatedException ex) {
      // happens for static methods
      if (guardedObject == null)
         return;

      final LinkedHashSet<ConstraintsViolatedListener> listenersToNotify = new LinkedHashSet<ConstraintsViolatedListener>();

      // get the object listeners
      {
         final Set<ConstraintsViolatedListener> objectListeners = listenersByObject.get(guardedObject);
         if (objectListeners != null) {
            listenersToNotify.addAll(objectListeners);
         }
      }

      // get the class listeners
      {
         final Set<ConstraintsViolatedListener> classListeners = listenersByClass.get(guardedObject.getClass());
         if (classListeners != null) {
            listenersToNotify.addAll(classListeners);
         }
      }

      // get the interface listeners
      {
         for (final Class<?> interfaze : guardedObject.getClass().getInterfaces()) {
            final Set<ConstraintsViolatedListener> interfaceListeners = listenersByClass.get(interfaze);
            if (interfaceListeners != null) {
               listenersToNotify.addAll(interfaceListeners);
            }
         }
      }

      // get the global listeners
      listenersToNotify.addAll(listeners);

      // notify the listeners
      for (final ConstraintsViolatedListener listener : listenersToNotify) {
         try {
            listener.onConstraintsViolatedException(ex);
         } catch (final RuntimeException rex) {
            LOG.warn("Notifying listener '{1}' failed.", listener, rex);
         }
      }

   }

   /**
    * Removes constraint check exclusions from the given constructor parameter
    *
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void removeCheckExclusions(final Constructor<?> ctor, final int paramIndex, final CheckExclusion... exclusions) throws InvalidConfigurationException {
      Assert.argumentNotNull("ctor", ctor);
      Assert.argumentNotEmpty("exclusions", exclusions);

      getClassChecks(ctor.getDeclaringClass()).removeConstructorParameterCheckExclusions(ctor, paramIndex, exclusions);
   }

   /**
    * Removes constraint check exclusions from the given method parameter
    *
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void removeCheckExclusions(final Method method, final int paramIndex, final CheckExclusion... exclusions) throws InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("exclusions", exclusions);

      getClassChecks(method.getDeclaringClass()).removeMethodParameterCheckExclusions(method, paramIndex, exclusions);
   }

   /**
    * Removes constraint checks from the given constructor parameter
    *
    * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
    */
   public void removeChecks(final Constructor<?> ctor, final int paramIndex, final Check... checks) throws InvalidConfigurationException {
      Assert.argumentNotNull("ctor", ctor);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(ctor.getDeclaringClass()).removeConstructorParameterChecks(ctor, paramIndex, checks);
   }

   /**
    * Removes constraint checks for the given method parameter
    *
    * @throws IllegalArgumentException if <code>constructor == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the parameterIndex is out of range
    */
   public void removeChecks(final Method method, final int paramIndex, final Check... checks) throws InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).removeMethodParameterChecks(method, paramIndex, checks);
   }

   /**
    * Registers post condition checks to a method's return value
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded
    */
   public void removeChecks(final Method method, final PostCheck... checks) throws InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).removeMethodPostChecks(method, checks);
   }

   /**
    * Registers pre condition checks to a method's return value
    *
    * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
    * @throws InvalidConfigurationException if the declaring class is not guarded
    */
   public void removeChecks(final Method method, final PreCheck... checks) throws InvalidConfigurationException {
      Assert.argumentNotNull("method", method);
      Assert.argumentNotEmpty("checks", checks);

      getClassChecks(method.getDeclaringClass()).removeMethodPreChecks(method, checks);
   }

   /**
    * Removes the given listener
    *
    * @return <code>true</code> if the listener was registered
    * @throws IllegalArgumentException if <code>listener == null</code>
    */
   public boolean removeListener(final ConstraintsViolatedListener listener) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);

      return listeners.remove(listener);
   }

   /**
    * Removes the given listener
    *
    * @param guardedClass guarded class or interface
    * @return <code>true</code> if the listener was registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code>
    */
   public boolean removeListener(final ConstraintsViolatedListener listener, final Class<?> guardedClass) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedClass", guardedClass);

      final Set<ConstraintsViolatedListener> currentListeners = listenersByClass.get(guardedClass);

      return currentListeners == null ? false : currentListeners.remove(listener);
   }

   /**
    * Removes the given listener
    *
    * @return <code>true</code> if the listener was registered
    * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code>
    */
   public boolean removeListener(final ConstraintsViolatedListener listener, final Object guardedObject) throws IllegalArgumentException {
      Assert.argumentNotNull("listener", listener);
      Assert.argumentNotNull("guardedObject", guardedObject);

      final Set<ConstraintsViolatedListener> currentListeners = listenersByObject.get(guardedObject);

      return currentListeners == null ? false : currentListeners.remove(listener);
   }

   /**
    * If set to false OVal's programming by contract features are disabled and constraints are not checked
    * automatically during runtime.
    */
   public void setActivated(final boolean isActivated) {
      this.isActivated = isActivated;
   }

   /**
    * Specifies if invariants are checked prior and after calls to non-private methods and constructors.
    */
   public void setInvariantsEnabled(final boolean isEnabled) {
      isInvariantsEnabled = isEnabled;
   }

   /**
    * Specifies if invariants are checked prior and after calls to non-private methods and constructors.
    *
    * @param guardedClass the guarded class to turn on/off the invariant checking
    */
   public void setInvariantsEnabled(final Class<?> guardedClass, final boolean isEnabled) {
      getClassChecks(guardedClass).isCheckInvariants = isEnabled;
   }

   /**
    * @param parameterNameResolver the parameterNameResolver to set, cannot be null
    * @throws IllegalArgumentException if <code>parameterNameResolver == null</code>
    */
   public void setParameterNameResolver(final ParameterNameResolver parameterNameResolver) throws IllegalArgumentException {
      Assert.argumentNotNull("parameterNameResolver", parameterNameResolver);

      this.parameterNameResolver.setDelegate(parameterNameResolver);
   }

   public void setPostConditionsEnabled(final boolean isEnabled) {
      isPostConditionsEnabled = isEnabled;
   }

   public void setPreConditionsEnabled(final boolean isEnabled) {
      isPreConditionsEnabled = isEnabled;
   }

   /**
    * Validates the give arguments against the defined constructor parameter constraints.<br>
    *
    * @return null if no violation, otherwise a list
    */
   protected List<ConstraintViolation> validateConstructorParameters(final Object validatedObject, final Constructor<?> constructor,
      final Object[] argsToValidate) throws ValidationFailedException {
      // create required objects for this validation cycle
      final List<ConstraintViolation> violations = getCollectionFactory().createList();
      currentViolations.get().add(violations);
      currentlyValidatedObjects.get().add(new IdentitySet<Object>(4));

      try {
         final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
         final Map<Integer, ParameterChecks> parameterChecks = cc.checksForConstructorParameters.get(constructor);

         // if no parameter checks exist just return null
         if (parameterChecks == null)
            return null;

         final String[] parameterNames = parameterNameResolver.getParameterNames(constructor);

         for (int i = 0; i < argsToValidate.length; i++) {
            final ParameterChecks checks = parameterChecks.get(i);

            if (checks != null && checks.hasChecks()) {
               final Object valueToValidate = argsToValidate[i];
               final ConstructorParameterContext context = new ConstructorParameterContext(constructor, i, parameterNames[i]);

               _validateParameterChecks(checks, validatedObject, valueToValidate, context, violations);
            }
         }
         return violations.size() == 0 ? null : violations;
      } catch (final OValException ex) {
         throw new ValidationFailedException("Validation of constructor parameters failed. Constructor: " + constructor + " Validated object: "
            + validatedObject.getClass().getName() + "@" + Integer.toHexString(validatedObject.hashCode()), ex);
      } finally {
         // remove the validation cycle related objects
         currentViolations.get().removeLast();
         currentlyValidatedObjects.get().removeLast();
      }
   }

   @Override
   protected void validateInvariants(final Object guardedObject, final List<ConstraintViolation> violations, final String[] profiles)
      throws IllegalArgumentException, ValidationFailedException {
      if (currentlyValidatedObjects.get().size() > 0 && currentlyValidatedObjects.get().getLast().contains(guardedObject))
         // to prevent StackOverflowError
         return;

      // create a new set for this validation cycle
      currentlyValidatedObjects.get().add(new IdentitySet<Object>(4));
      try {
         super.validateInvariants(guardedObject, violations, profiles);
      } finally {
         // remove the set
         currentlyValidatedObjects.get().removeLast();
      }
   }

   /**
    * Validates the pre conditions for a method call.
    */
   protected void validateMethodParameters(final Object validatedObject, final Method method, final Object[] args, final List<ConstraintViolation> violations)
      throws ValidationFailedException {
      // create a new set for this validation cycle
      currentlyValidatedObjects.get().add(new IdentitySet<Object>(4));
      try {
         final ClassChecks cc = getClassChecks(method.getDeclaringClass());
         final Map<Integer, ParameterChecks> parameterChecks = cc.checksForMethodParameters.get(method);

         if (parameterChecks == null)
            return;

         final String[] parameterNames = parameterNameResolver.getParameterNames(method);

         /*
          * parameter constraints validation
          */
         if (parameterNames.length > 0) {
            for (int i = 0; i < args.length; i++) {
               final ParameterChecks checks = parameterChecks.get(i);

               if (checks != null && checks.checks.size() > 0) {
                  final Object valueToValidate = args[i];
                  final MethodParameterContext context = new MethodParameterContext(method, i, parameterNames[i]);

                  _validateParameterChecks(checks, validatedObject, valueToValidate, context, violations);
               }
            }
         }
      } catch (final OValException ex) {
         throw new ValidationFailedException("Method pre conditions validation failed. Method: " + method + " Validated object: " + validatedObject, ex);
      } finally {
         // remove the set
         currentlyValidatedObjects.get().removeLast();
      }
   }

   /**
    * Validates the post conditions for a method call.
    */
   protected void validateMethodPost(final Object validatedObject, final Method method, final Object[] args, final Object returnValue,
      final Map<PostCheck, Object> oldValues, final List<ConstraintViolation> violations) throws ValidationFailedException {
      final String key = System.identityHashCode(validatedObject) + " " + System.identityHashCode(method);

      /*
       *  avoid circular references
       */
      if (CURRENTLY_CHECKED_POST_CONDITIONS.get().contains(key))
         return;

      CURRENTLY_CHECKED_POST_CONDITIONS.get().add(key);
      try {
         final ClassChecks cc = getClassChecks(method.getDeclaringClass());
         final Set<PostCheck> postChecks = cc.checksForMethodsPostExcecution.get(method);

         if (postChecks == null)
            return;

         final String[] parameterNames = parameterNameResolver.getParameterNames(method);
         final boolean hasParameters = parameterNames.length > 0;

         final MethodExitContext context = ContextCache.getMethodExitContext(method);

         for (final PostCheck check : postChecks) {
            if (!isAnyProfileEnabled(check.getProfiles(), null)) {
               continue;
            }

            final ExpressionLanguage eng = expressionLanguageRegistry.getExpressionLanguage(check.getLanguage());
            final Map<String, Object> values = getCollectionFactory().createMap();
            values.put("_this", validatedObject);
            values.put("_returns", returnValue);
            values.put("_old", oldValues.get(check));
            if (hasParameters) {
               values.put("_args", args);
               for (int i = 0; i < args.length; i++) {
                  values.put(parameterNames[i], args[i]);
               }
            } else {
               values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);
            }

            if (!eng.evaluateAsBoolean(check.getExpression(), values)) {
               final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
               messageVariables.put("expression", check.getExpression());
               final String errorMessage = renderMessage(context, null, check.getMessage(), messageVariables);

               violations.add(new ConstraintViolation(check, errorMessage, validatedObject, null, context));
            }
         }
      } catch (final OValException ex) {
         throw new ValidationFailedException("Method post conditions validation failed. Method: " + method + " Validated object: " + validatedObject, ex);
      } finally {
         CURRENTLY_CHECKED_POST_CONDITIONS.get().remove(key);
      }
   }

   /**
    * Validates the @Pre conditions for a method call.
    */
   protected void validateMethodPre(final Object validatedObject, final Method method, final Object[] args, final List<ConstraintViolation> violations)
      throws ValidationFailedException {
      final String key = System.identityHashCode(validatedObject) + " " + System.identityHashCode(method);

      /*
       *  avoid circular references
       */
      if (CURRENTLY_CHECKED_PRE_CONDITIONS.get().contains(key))
         return;

      CURRENTLY_CHECKED_PRE_CONDITIONS.get().add(key);
      try {
         final ClassChecks cc = getClassChecks(method.getDeclaringClass());
         final Set<PreCheck> preChecks = cc.checksForMethodsPreExecution.get(method);

         if (preChecks == null)
            return;

         final String[] parameterNames = parameterNameResolver.getParameterNames(method);
         final boolean hasParameters = parameterNames.length > 0;

         final MethodEntryContext context = ContextCache.getMethodEntryContext(method);

         for (final PreCheck check : preChecks) {
            if (!isAnyProfileEnabled(check.getProfiles(), null)) {
               continue;
            }

            final ExpressionLanguage eng = expressionLanguageRegistry.getExpressionLanguage(check.getLanguage());
            final Map<String, Object> values = getCollectionFactory().createMap();
            values.put("_this", validatedObject);
            if (hasParameters) {
               values.put("_args", args);
               for (int i = 0; i < args.length; i++) {
                  values.put(parameterNames[i], args[i]);
               }
            } else {
               values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);
            }

            if (!eng.evaluateAsBoolean(check.getExpression(), values)) {
               final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
               messageVariables.put("expression", check.getExpression());
               final String errorMessage = renderMessage(context, null, check.getMessage(), messageVariables);

               violations.add(new ConstraintViolation(check, errorMessage, validatedObject, null, context));
            }
         }
      } catch (final OValException ex) {
         throw new ValidationFailedException("Method pre conditions validation failed. Method: " + method + " Validated object: " + validatedObject, ex);
      } finally {
         CURRENTLY_CHECKED_PRE_CONDITIONS.get().remove(key);
      }
   }

   /**
    * Validates the return value checks for a method call.
    */
   protected void validateMethodReturnValue(final Object validatedObject, final Method method, final Object returnValue,
      final List<ConstraintViolation> violations) throws ValidationFailedException {
      final String key = System.identityHashCode(validatedObject) + " " + System.identityHashCode(method);

      /*
       *  avoid circular references, e.g.
       *
       *  private String name;
       *
       *  @Assert("_this.name != null", lang="groovy")
       *  public String getName { return name; }
       *
       *  => Groovy will invoke the getter to return the value, invocations of the getter will trigger the validation of the method return values again,
       *  including the @Assert constraint
       */
      if (CURRENTLY_CHECKED_METHOD_RETURN_VALUES.get().contains(key))
         return;

      CURRENTLY_CHECKED_METHOD_RETURN_VALUES.get().add(key);
      // create a new set for this validation cycle
      currentlyValidatedObjects.get().add(new IdentitySet<Object>(4));
      try {
         final ClassChecks cc = getClassChecks(method.getDeclaringClass());
         final Collection<Check> returnValueChecks = cc.checksForMethodReturnValues.get(method);

         if (returnValueChecks == null || returnValueChecks.size() == 0)
            return;

         final MethodReturnValueContext context = ContextCache.getMethodReturnValueContext(method);

         for (final Check check : returnValueChecks) {
            checkConstraint(violations, check, validatedObject, returnValue, context, null, false);
         }
      } catch (final OValException ex) {
         throw new ValidationFailedException("Method post conditions validation failed. Method: " + method + " Validated object: " + validatedObject, ex);
      } finally {
         CURRENTLY_CHECKED_METHOD_RETURN_VALUES.get().remove(key);

         // remove the set
         currentlyValidatedObjects.get().removeLast();
      }
   }
}
