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
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.oval.Check;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodExitContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.ValidationFailedException;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.internal.ClassChecks;
import net.sf.oval.internal.CollectionFactoryHolder;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.IdentitySet;
import net.sf.oval.internal.util.Invocable;
import net.sf.oval.internal.util.ListOrderedSet;
import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.ThreadLocalIdentitySet;
import net.sf.oval.internal.util.ThreadLocalWeakHashSet;

/**
 * Extended version of the validator to realize programming by contract.
 * 
 * @author Sebastian Thomschke
 */
public class Guard extends Validator
{
	private final static Log LOG = Log.getLog(Guard.class);

	private final ThreadLocalIdentitySet<Object> currentlyInvariantCheckingFor = new ThreadLocalIdentitySet<Object>();

	private boolean isActivated = true;
	private boolean isInvariantsEnabled = true;
	private boolean isPreConditionsEnabled = true;
	private boolean isPostConditionsEnabled = true;

	/**
	 * Flag that indicates if any listeners were registered at any time.
	 * Used for performance improvements.
	 */
	private boolean isListenersFeatureUsed = false;

	/**
	 * Flag that indicates if exception suppressing was used at any time.
	 * Used for performance improvements.
	 */
	private boolean isProbeModeFeatureUsed = false;

	private final Set<ConstraintsViolatedListener> listeners = new IdentitySet<ConstraintsViolatedListener>(
			4);
	private final Map<Class, Set<ConstraintsViolatedListener>> listenersByClass = new WeakHashMap<Class, Set<ConstraintsViolatedListener>>(
			4);
	private final Map<Object, Set<ConstraintsViolatedListener>> listenersByObject = new WeakHashMap<Object, Set<ConstraintsViolatedListener>>(
			4);

	/**
	 * Objects for OVal suppresses occuring ConstraintViolationExceptions 
	 * for pre condition violations on setter methods for the current thread.
	 */
	private final ThreadLocalWeakHashSet<Object> objectsInProbeMode = new ThreadLocalWeakHashSet<Object>();

	/**
	 * Constructs a new guard object and uses a new isntance of
	 * AnnotationsConfigurer
	 */
	public Guard()
	{
		super();
	}

	public Guard(final Collection<Configurer> configurers)
	{
		super(configurers);
	}

	public Guard(final Configurer... configurers)
	{
		super(configurers);
	}

	/**
	 * Registers constraint checks for the given constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param checks
	 * @throws IllegalArgumentException if <code>constructor == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
	 */
	public void addChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws IllegalArgumentException, InvalidConfigurationException
	{
		if (constructor == null) throw new IllegalArgumentException("constructor cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
		cc.addConstructorParameterChecks(constructor, parameterIndex, checks);
	}

	/**
	 * Registers constraint checks for the given method's return value
	 * 
	 * @param method
	 * @param checks
	 * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if method does not declare a return type (void), or the declaring class is not guarded
	 */
	@Override
	public void addChecks(final Method method, final Check... checks)
			throws IllegalArgumentException, InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.addMethodReturnValueChecks(method, null, checks);
	}

	/**
	 * Registers constraint checks for the given method parameter 
	 *  
	 * @param method
	 * @param parameterIndex
	 * @param checks
	 * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
	 */
	public void addChecks(final Method method, final int parameterIndex, final Check... checks)
			throws IllegalArgumentException, InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.addMethodParameterChecks(method, parameterIndex, checks);
	}

	/**
	 * Registers post condition checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded
	 */
	public void addChecks(final Method method, final PostCheck... checks)
			throws IllegalArgumentException, InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.addMethodPostChecks(method, checks);
	}

	/**
	 * Registers pre condition checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded
	 */
	public void addChecks(final Method method, final PreCheck... checks)
			throws IllegalArgumentException, InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.addMethodPreChecks(method, checks);
	}

	/**
	 * Registers the given listener for <b>all</b> thrown ConstraintViolationExceptions
	 * 
	 * @param listener the listener to register
	 * @return <code>true</code> if the listener was not yet registered
	 * @throws IllegalArgumentException if <code>listener == null</code>
	 */
	public boolean addListener(final ConstraintsViolatedListener listener)
			throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");

		isListenersFeatureUsed = true;
		return listeners.add(listener);
	}

	/**
	 * Registers the given listener for all thrown ConstraintViolationExceptions on objects of the given class
	 * @param listener the listener to register
	 * @param guardedClass guarded class or interface
	 * @return <code>true</code> if the listener was not yet registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code> 
	 */
	public boolean addListener(final ConstraintsViolatedListener listener, final Class guardedClass)
			throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedClass == null)
			throw new IllegalArgumentException("guardedClass cannot be null");

		isListenersFeatureUsed = true;

		synchronized (listenersByClass)
		{
			Set<ConstraintsViolatedListener> classListeners = listenersByClass.get(guardedClass);

			if (classListeners == null)
			{
				classListeners = CollectionFactoryHolder.getFactory().createSet();
				listenersByClass.put(guardedClass, classListeners);
			}
			return classListeners.add(listener);
		}
	}

	/**
	 * Registers the given listener for all thrown ConstraintViolationExceptions on objects of the given object
	 * @param listener the listener to register
	 * @param guardedObject
	 * @return <code>true</code> if the listener was not yet registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code> 
	 */
	public boolean addListener(final ConstraintsViolatedListener listener,
			final Object guardedObject)
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		isListenersFeatureUsed = true;

		synchronized (listenersByObject)
		{
			Set<ConstraintsViolatedListener> objectListeners = listenersByObject.get(guardedObject);

			if (objectListeners == null)
			{
				objectListeners = CollectionFactoryHolder.getFactory().createSet(2);
				listenersByObject.put(guardedObject, objectListeners);
			}
			return objectListeners.add(listener);
		}
	}

	/**
	 * Evaluates the old expression
	 * 
	 * @return null if no violation, otherwise a list
	 * @throws ValidationFailedException  
	 */
	protected Map<PostCheck, Object> calculateMethodPostOldValues(final Object validatedObject,
			final Method method, final Object[] args) throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Set<PostCheck> postChecks = cc.checksForMethodsPostExcecution.get(method);

			// shortcut: check if any post checks for this method exist
			if (postChecks == null) return null;

			final String[] parameterNames = parameterNameResolver.getParameterNames(method);
			final boolean hasParameters = parameterNames.length > 0;

			final Map<PostCheck, Object> oldValues = CollectionFactoryHolder.getFactory()
					.createMap(postChecks.size());

			for (final PostCheck check : postChecks)
			{
				if (isAnyProfileEnabled(check.getProfiles()) && check.getOld() != null
						&& check.getOld().length() > 0)
				{
					final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
					final Map<String, Object> values = CollectionFactoryHolder.getFactory()
							.createMap();
					values.put("_this", validatedObject);
					if (hasParameters)
					{
						values.put("_args", args);
						for (int i = 0; i < args.length; i++)
						{
							values.put(parameterNames[i], args[i]);
						}
					}
					else
						values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);

					oldValues.put(check, eng.evaluate(check.getOld(), values));
				}
			}

			return oldValues;
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Returns the registers constraint pre condition checks for the given method parameter 
	 *  
	 * @param method
	 * @param parameterIndex
	 * @throws IllegalArgumentException if <code>method == null</code>
	 */
	public Check[] getChecks(final Method method, final int parameterIndex)
			throws InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final Map<Integer, Set<Check>> checks = cc.checksForMethodParameters.get(method);
		if (checks == null) return null;

		final Collection<Check> paramChecks = checks.get(parameterIndex);
		return paramChecks == null ? null : paramChecks.toArray(new Check[checks.size()]);
	}

	/**
	 * Returns the registered post condition checks for the given method
	 * 
	 * @param method
	 * @throws IllegalArgumentException if <code>method == null</code>
	 */
	public PostCheck[] getChecksPost(final Method method) throws IllegalArgumentException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final Set<PostCheck> checks = cc.checksForMethodsPostExcecution.get(method);
		return checks == null ? null : checks.toArray(new PostCheck[checks.size()]);
	}

	/**
	 * Returns the registered pre condition checks for the given method.
	 * 
	 * @param method
	 * @throws IllegalArgumentException if <code>method == null</code>
	 */
	public PreCheck[] getChecksPre(final Method method) throws IllegalArgumentException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final Set<PreCheck> checks = cc.checksForMethodsPreExecution.get(method);
		return checks == null ? null : checks.toArray(new PreCheck[checks.size()]);
	}

	/**
	 * @return the parameterNameResolver
	 */
	public ParameterNameResolver getParameterNameResolver()
	{
		return parameterNameResolver;
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @throws ConstraintsViolatedException
	 * @throws ValidationFailedException
	 */
	protected void guardConstructorPost(final Object guardedObject, final Constructor constructor,
			final Object[] args) throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return;

		final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());

		// check invariants
		if (isInvariantsEnabled && cc.isCheckInvariants
				|| cc.methodsWithCheckInvariantsPost.contains(constructor))
		{
			try
			{
				final List<ConstraintViolation> violations = CollectionFactoryHolder.getFactory()
						.createList();
				validateInvariants(guardedObject, violations);

				if (violations.size() > 0)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations);
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					throw translateException(violationException);
				}
			}
			catch (final ValidationFailedException ex)
			{
				throw translateException(ex);
			}
		}
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @throws ConstraintsViolatedException if anything precondition is not satisfied
	 * @throws ValidationFailedException 
	 */
	protected void guardConstructorPre(final Object guardedObject, final Constructor constructor,
			final Object[] args) throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return;

		// constructor parameter validation
		if (isPreConditionsEnabled && args.length > 0)
		{
			try
			{
				final List<ConstraintViolation> violations = validateConstructorParameters(
						guardedObject, constructor, args);

				if (violations != null)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations);
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					throw translateException(violationException);
				}
			}
			catch (final ValidationFailedException ex)
			{
				throw translateException(ex);
			}
		}
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @param guardedObject
	 * @param method
	 * @param args
	 * @param invocable
	 * @return The method return value or null if the guarded object is in probe mode.
	 * @throws ConstraintsViolatedException if an constriant violation occures and the validated object is not in probe mode.
	 */
	protected Object guardMethod(Object guardedObject, final Method method, final Object[] args,
			final Invocable invocable) throws ConstraintsViolatedException,
			ValidationFailedException
	{
		if (!isActivated) return invocable.invoke();

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final boolean checkInvariants = isInvariantsEnabled && cc.isCheckInvariants
				&& !ReflectionUtils.isPrivate(method) && !ReflectionUtils.isProtected(method);

		final List<ConstraintViolation> violations = CollectionFactoryHolder.getFactory()
				.createList();

		// if static method use the declaring class as guardedObject
		if (guardedObject == null && ReflectionUtils.isStatic(method))
			guardedObject = method.getDeclaringClass();

		try
		{
			// check invariants
			if (checkInvariants || cc.methodsWithCheckInvariantsPre.contains(method))
				validateInvariants(guardedObject, violations);

			if (isPreConditionsEnabled)
			{
				// method parameter validation
				if (violations.size() == 0 && args.length > 0)
					validateMethodParameters(guardedObject, method, args, violations);

				// @Pre validation
				if (violations.size() == 0)
					validateMethodPre(guardedObject, method, args, violations);
			}

			if (violations.size() > 0)
			{
				final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
						violations);
				if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

				// don't throw an exception if the method is a setter and suppressing for precondition is enabled
				if (isProbeModeFeatureUsed && isInProbeMode(guardedObject)) return null;

				throw translateException(violationException);
			}

			// abort method execution if in probe mode
			if (isProbeModeFeatureUsed && isInProbeMode(guardedObject)) return null;
		}
		catch (final ValidationFailedException ex)
		{
			throw translateException(ex);
		}

		final Map<PostCheck, Object> postCheckOldValues = calculateMethodPostOldValues(
				guardedObject, method, args);

		final Object returnValue = invocable.invoke();

		try
		{
			// chek invariants if executed method is not private
			if (checkInvariants || cc.methodsWithCheckInvariantsPost.contains(method))
			{
				validateInvariants(guardedObject, violations);
			}

			if (isPostConditionsEnabled)
			{

				// method return value
				if (violations.size() == 0)
					validateMethodReturnValue(guardedObject, method, returnValue, violations);

				// @Post
				if (violations.size() == 0)
					validateMethodPost(guardedObject, method, args, returnValue,
							postCheckOldValues, violations);
			}

			if (violations.size() > 0)
			{
				final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
						violations);
				if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

				throw translateException(violationException);
			}
		}
		catch (final ValidationFailedException ex)
		{
			throw translateException(ex);
		}
		return returnValue;
	}

	/**
	 * @param listener
	 * @return <code>true</code> if the listener is registered
	 * @throws IllegalArgumentException if <code>listener == null</code> 
	 */
	public boolean hasListener(final ConstraintsViolatedListener listener)
			throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");

		return listeners.contains(listener);
	}

	/**
	 * @param listener
	 * @param guardedClass guarded class or interface
	 * @return <code>true</code> if the listener is registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code> 
	 */
	public boolean hasListener(final ConstraintsViolatedListener listener, final Class guardedClass)
			throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedClass == null)
			throw new IllegalArgumentException("guardedClass cannot be null");

		final Set<ConstraintsViolatedListener> classListeners = listenersByClass.get(guardedClass);

		if (classListeners == null) return false;

		return classListeners.contains(listener);
	}

	/**
	 * @param listener
	 * @param guardedObject
	 * @return <code>true</code> if the listener is registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code> 
	 */
	public boolean hasListener(final ConstraintsViolatedListener listener,
			final Object guardedObject) throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		final Set<ConstraintsViolatedListener> objectListeners = listenersByObject
				.get(guardedObject);

		if (objectListeners == null) return false;

		return objectListeners.contains(listener);
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isActivated()
	{
		return isActivated;
	}

	/**
	 * Determines if the probe mode is enabled for the given object in the current thread.
	 * In probe mode calls to methods of an object are not actually executed. OVal only 
	 * validates method pre-conditions and notifies ConstraintViolationListeners but
	 * does not throw ConstraintViolationExceptions. Methods with return values will return null. 
	 * 
	 * @param guardedObject
	 * @return true if exceptions are suppressed
	 */
	public boolean isInProbeMode(final Object guardedObject)
	{
		// guardedObject may be null if isInProbeMode is called when validating pre conditions of a static method
		if (guardedObject == null) return false;

		return objectsInProbeMode.get().contains(guardedObject);
	}

	/**
	 * Determins if invariants are checked prior and after every
	 * call to a non-private method or constructor.
	 * 
	 * @return the isInvariantChecksActivated
	 */
	public boolean isInvariantsEnabled()
	{
		return isInvariantsEnabled;
	}

	/**
	 * Determins if invariants are checked prior and after every
	 * call to a non-private method or constructor.
	 * 
	 * @param guardedClass the guarded class
	 * @return the isInvariantChecksActivated
	 */
	public boolean isInvariantsEnabled(final Class guardedClass)
	{
		final ClassChecks cc = getClassChecks(guardedClass);
		return cc.isCheckInvariants;
	}

	/**
	 * @return the isPostChecksActivated
	 */
	public boolean isPostConditionsEnabled()
	{
		return isPostConditionsEnabled;
	}

	/**
	 * @return the isPreChecksActivated
	 */
	public boolean isPreConditionsEnabled()
	{
		return isPreConditionsEnabled;
	}

	/**
	 * notifies all registered validation listener about the occured constraint violation exception
	 */
	protected void notifyListeners(final Object guardedObject, final ConstraintsViolatedException ex)
	{
		// happens for static methods
		if (guardedObject == null) return;

		final ListOrderedSet<ConstraintsViolatedListener> listenersToNotify = new ListOrderedSet<ConstraintsViolatedListener>();

		// get the object listeners
		{
			final Set<ConstraintsViolatedListener> objectListeners = listenersByObject
					.get(guardedObject);
			if (objectListeners != null)
			{
				listenersToNotify.addAll(objectListeners);
			}
		}

		// get the class listeners
		{
			final Set<ConstraintsViolatedListener> classListeners = listenersByClass
					.get(guardedObject.getClass());
			if (classListeners != null)
			{
				listenersToNotify.addAll(classListeners);
			}
		}

		// get the interface listeners
		{
			for (final Class interfaze : guardedObject.getClass().getInterfaces())
			{
				final Set<ConstraintsViolatedListener> interfaceListeners = listenersByClass
						.get(interfaze);
				if (interfaceListeners != null)
				{
					listenersToNotify.addAll(interfaceListeners);
				}
			}
		}

		// get the global listeners
		listenersToNotify.addAll(listeners);

		// notify the listeners
		for (final ConstraintsViolatedListener listener : listenersToNotify)
		{
			try
			{
				listener.onConstraintsViolatedException(ex);
			}
			catch (final RuntimeException rex)
			{
				LOG.warn("Notifying listener '{}' failed.", listener, rex);
			}
		}

	}

	/**
	 * Removes constraint checks for the given constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded or the parameterIndex is out of range
	 */
	public void removeChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (constructor == null) throw new IllegalArgumentException("constructor cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
		cc.removeConstructorParameterChecks(constructor, parameterIndex, checks);
	}

	/**
	 * Removes constraint checks for the given method parameter 
	 *  
	 * @param method
	 * @param parameterIndex
	 * @param checks
	 * @throws IllegalArgumentException if <code>constructor == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the parameterIndex is out of range
	 */
	public void removeChecks(final Method method, final int parameterIndex, final Check... checks)
			throws InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.removeMethodParameterChecks(method, parameterIndex, checks);
	}

	/**
	 * Registers post condition checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded
	 */
	public void removeChecks(final Method method, final PostCheck... checks)
			throws InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.removeMethodPostChecks(method, checks);
	}

	/**
	 * Registers pre condition checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws IllegalArgumentException if <code>method == null</code> or <code>checks == null</code> or checks is empty
	 * @throws InvalidConfigurationException if the declaring class is not guarded
	 */
	public void removeChecks(final Method method, final PreCheck... checks)
			throws InvalidConfigurationException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		cc.removeMethodPreChecks(method, checks);
	}

	/**
	 * Removes the given listener
	 * @param listener
	 * @return <code>true</code> if the listener was registered
	 * @throws IllegalArgumentException if <code>listener == null</code> 
	 */
	public boolean removeListener(final ConstraintsViolatedListener listener)
			throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");

		return listeners.remove(listener);
	}

	/**
	 * Removes the given listener
	 * @param listener
	 * @param guardedClass guarded class or interface
	 * @return <code>true</code> if the listener was registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedClass == null</code> 
	 */
	public boolean removeListener(final ConstraintsViolatedListener listener,
			final Class guardedClass) throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedClass == null)
			throw new IllegalArgumentException("guardedClass cannot be null");

		final Set<ConstraintsViolatedListener> currentListeners = listenersByClass
				.get(guardedClass);

		return currentListeners == null ? false : currentListeners.remove(listener);
	}

	/**
	 * Removes the given listener
	 * @param listener
	 * @param guardedObject
	 * @return <code>true</code> if the listener was registered
	 * @throws IllegalArgumentException if <code>listener == null</code> or <code>guardedObject == null</code> 
	 */
	public boolean removeListener(final ConstraintsViolatedListener listener,
			final Object guardedObject) throws IllegalArgumentException
	{
		if (listener == null) throw new IllegalArgumentException("listener cannot be null");
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		final Set<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(guardedObject);

		return currentListeners == null ? false : currentListeners.remove(listener);
	}

	/**
	 * If set to false OVal's programming by contract features are disabled
	 * and constraints are not checked automatically during runtime.
	 * @param isActivated the isActivated to set
	 */
	public void setActivated(final boolean isActivated)
	{
		this.isActivated = isActivated;
	}

	/**
	 * Enable or disable the probe mode for the given object in the current thread.
	 * In probe mode calls to methods of an object are not actually executed. OVal only 
	 * validates method pre-conditions and notifies ConstraintViolationListeners but
	 * does not throw ConstraintViolationExceptions. Methods with return values will return null. 
	 * 
	 * @param guardedObject
	 * @param isInProbeMode
	 * @throws IllegalArgumentException if <code>guardedObject == null</code>
	 */
	public void setInProbeMode(final Object guardedObject, final boolean isInProbeMode)
			throws IllegalArgumentException
	{
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		if (guardedObject instanceof Class)
		{
			LOG.warn("Enabling probe mode for a class looks like a programming error. Class: {}",
					guardedObject);
		}
		isProbeModeFeatureUsed = true;

		if (isInProbeMode)
			objectsInProbeMode.get().add(guardedObject);
		else
			objectsInProbeMode.get().remove(guardedObject);
	}

	/**
	 * Specifies if invariants are checked prior and after
	 * calls to non-private methods and constructors.
	 * 
	 * @param isEnabled the isInvariantsEnabled to set
	 */
	public void setInvariantsEnabled(final boolean isEnabled)
	{
		isInvariantsEnabled = isEnabled;
	}

	/**
	 * Specifies if invariants are checked prior and after
	 * calls to non-private methods and constructors.
	 * 
	 * @param guardedClass the guarded class to turn on/off the invariant checking
	 * @param isEnabled the isEnabled to set
	 */
	public void setInvariantsEnabled(final Class< ? > guardedClass, final boolean isEnabled)
	{
		final ClassChecks cc = getClassChecks(guardedClass);
		cc.isCheckInvariants = isEnabled;
	}

	/**
	 * @param parameterNameResolver the parameterNameResolver to set, cannot be null
	 * @throws IllegalArgumentException if <code>parameterNameResolver == null</code>
	 */
	public void setParameterNameResolver(final ParameterNameResolver parameterNameResolver)
			throws IllegalArgumentException
	{
		if (parameterNameResolver == null)
			throw new IllegalArgumentException("parameterNameResolver cannot be null");

		this.parameterNameResolver = parameterNameResolver;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setPostConditionsEnabled(final boolean isEnabled)
	{
		isPostConditionsEnabled = isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setPreConditionsEnabled(final boolean isEnabled)
	{
		isPreConditionsEnabled = isEnabled;
	}

	/**
	 * Validates the give arguments against the defined constructor parameter constraints.<br>
	 * 
	 * @return null if no violation, otherwise a list
	 * @throws ValidationFailedException
	 */
	protected List<ConstraintViolation> validateConstructorParameters(final Object validatedObject,
			final Constructor constructor, final Object[] argsToValidate)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
			final Map<Integer, Set<Check>> parameterChecks = cc.checksForConstructorParameters
					.get(constructor);

			// if no parameter checks exist just return null
			if (parameterChecks == null) return null;

			final List<ConstraintViolation> violations = CollectionFactoryHolder.getFactory()
					.createList();

			final String[] parameterNames = parameterNameResolver.getParameterNames(constructor);

			for (int i = 0; i < argsToValidate.length; i++)
			{
				final Collection<Check> checks = parameterChecks.get(i);

				if (checks != null && checks.size() > 0)
				{
					final Object valueToValidate = argsToValidate[i];
					final ConstructorParameterContext context = new ConstructorParameterContext(
							constructor, i, parameterNames[i]);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate,
								context);
					}
				}
			}
			return violations.size() == 0 ? null : violations;
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException(
					"Validation of constructor parameters failed. Constructor: " + constructor
							+ " Validated object:" + validatedObject, ex);
		}
	}

	@Override
	protected void validateInvariants(final Object guardedObject,
			final List<ConstraintViolation> violations) throws IllegalArgumentException,
			ValidationFailedException
	{
		if (!currentlyInvariantCheckingFor.get().contains(guardedObject))
		{
			currentlyInvariantCheckingFor.get().add(guardedObject);
			try
			{
				super.validateInvariants(guardedObject, violations);
			}
			finally
			{
				currentlyInvariantCheckingFor.get().remove(guardedObject);
			}
		}
	}

	/**
	 * Validates the pre conditions for a method call.<br>
	 *  
	 * @throws ValidationFailedException 
	 */
	protected void validateMethodParameters(final Object validatedObject, final Method method,
			final Object[] args, final List<ConstraintViolation> violations)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Map<Integer, Set<Check>> parameterChecks = cc.checksForMethodParameters
					.get(method);

			if (parameterChecks == null) return;

			final String[] parameterNames = parameterNameResolver.getParameterNames(method);
			final boolean hasParameters = parameterNames.length > 0;

			/*
			 * parameter constraints validation
			 */
			if (parameterChecks != null && hasParameters)
			{
				for (int i = 0; i < args.length; i++)
				{
					final Collection<Check> checks = parameterChecks.get(i);

					if (checks != null && checks.size() > 0)
					{
						final Object valueToValidate = args[i];
						final MethodParameterContext context = new MethodParameterContext(method,
								i, parameterNames[i]);

						for (final Check check : checks)
						{
							checkConstraint(violations, check, validatedObject, valueToValidate,
									context);
						}
					}
				}
			}
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException("Method pre conditions validation failed. Method: "
					+ method + " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the post conditions for a method call.<br>
	 * 
	 * @throws ValidationFailedException  
	 */
	protected void validateMethodPost(final Object validatedObject, final Method method,
			final Object[] args, final Object returnValue, final Map<PostCheck, Object> oldValues,
			final List<ConstraintViolation> violations) throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Set<PostCheck> postChecks = cc.checksForMethodsPostExcecution.get(method);

			if (postChecks == null) return;

			final String[] parameterNames = parameterNameResolver.getParameterNames(method);
			final boolean hasParameters = parameterNames.length > 0;

			final MethodExitContext context = new MethodExitContext(method);

			for (final PostCheck check : postChecks)
			{
				if (!isAnyProfileEnabled(check.getProfiles())) continue;

				final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
				final Map<String, Object> values = CollectionFactoryHolder.getFactory().createMap();
				values.put("_this", validatedObject);
				values.put("_returns", returnValue);
				values.put("_old", oldValues.get(check));
				if (hasParameters)
				{
					values.put("_args", args);
					for (int i = 0; i < args.length; i++)
					{
						values.put(parameterNames[i], args[i]);
					}
				}
				else
					values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);

				if (!eng.evaluateAsBoolean(check.getExpression(), values))
				{
					final Map<String, String> messageVariables = CollectionFactoryHolder
							.getFactory().createMap(2);
					messageVariables.put("expression", check.getExpression());
					final String errorMessage = renderMessage(context, null, check.getMessage(),
							messageVariables);

					violations.add(new ConstraintViolation(check.getErrorCode(), errorMessage,
							check.getSeverity(), validatedObject, null, context));
				}
			}
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the @Pre conditions for a method call.<br>
	 *  
	 * @throws ValidationFailedException 
	 */
	protected void validateMethodPre(final Object validatedObject, final Method method,
			final Object[] args, final List<ConstraintViolation> violations)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Set<PreCheck> preChecks = cc.checksForMethodsPreExecution.get(method);

			if (preChecks == null) return;

			final String[] parameterNames = parameterNameResolver.getParameterNames(method);
			final boolean hasParameters = parameterNames.length > 0;

			final MethodEntryContext context = new MethodEntryContext(method);

			for (final PreCheck check : preChecks)
			{
				if (!isAnyProfileEnabled(check.getProfiles())) continue;

				final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
				final Map<String, Object> values = CollectionFactoryHolder.getFactory().createMap();
				values.put("_this", validatedObject);
				if (hasParameters)
				{
					values.put("_args", args);
					for (int i = 0; i < args.length; i++)
					{
						values.put(parameterNames[i], args[i]);
					}
				}
				else
					values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);

				if (!eng.evaluateAsBoolean(check.getExpression(), values))
				{
					final Map<String, String> messageVariables = CollectionFactoryHolder
							.getFactory().createMap(2);
					messageVariables.put("expression", check.getExpression());
					final String errorMessage = renderMessage(context, null, check.getMessage(),
							messageVariables);

					violations.add(new ConstraintViolation(check.getErrorCode(), errorMessage,
							check.getSeverity(), validatedObject, null, context));
				}
			}
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the return value checks for a method call.<br>
	 * 
	 * @throws ValidationFailedException  
	 */
	protected void validateMethodReturnValue(final Object validatedObject, final Method method,
			final Object returnValue, final List<ConstraintViolation> violations)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Collection<Check> returnValueChecks = cc.checksForMethodReturnValues.get(method);

			if (returnValueChecks == null || returnValueChecks.size() == 0) return;

			final MethodReturnValueContext context = new MethodReturnValueContext(method);

			for (final Check check : returnValueChecks)
			{
				checkConstraint(violations, check, validatedObject, returnValue, context);
			}
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}
}
