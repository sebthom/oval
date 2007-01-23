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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.exceptions.OValException;
import net.sf.oval.exceptions.ValidationFailedException;
import net.sf.oval.utils.ListOrderedSet;
import net.sf.oval.utils.ReflectionUtils;
import net.sf.oval.utils.ThreadLocalWeakHashSet;
import net.sf.oval.utils.WeakHashSet;

/**
 * @author Sebastian Thomschke
 */
public class Guard
{
	private final static Logger LOG = Logger.getLogger(Guard.class.getName());

	private boolean isActivated = true;

	/**
	 * Flag that indicates if any listeners were registered at any time.
	 * Used for performance improvements.
	 */
	private boolean isListenersFeatureUsed = false;

	/**
	 * Flag that indicates if exception suppressing was used at any time.
	 * Used for performance improvements.
	 */
	private boolean isSuppressFeatureUsed = false;

	private final Set<ConstraintsViolatedListener> listeners = new WeakHashSet<ConstraintsViolatedListener>();
	private final Map<Class, Set<ConstraintsViolatedListener>> listenersByClass = new WeakHashMap<Class, Set<ConstraintsViolatedListener>>();
	private final Map<Object, Set<ConstraintsViolatedListener>> listenersByObject = new WeakHashMap<Object, Set<ConstraintsViolatedListener>>();

	private ExceptionTranslator exceptionTranslator;

	/**
	 * Classes for OVal suppresses occuring ConstraintViolationExceptions 
	 * for pre condition violations on setter methods for the current thread.
	 */
	private final ThreadLocalWeakHashSet<Class> unsafeClasses = new ThreadLocalWeakHashSet<Class>();

	/**
	 * Objects for OVal suppresses occuring ConstraintViolationExceptions 
	 * for pre condition violations on setter methods for the current thread.
	 */
	private final ThreadLocalWeakHashSet<Object> unsafeObjects = new ThreadLocalWeakHashSet<Object>();

	private Validator validator;

	public Guard(final Validator validator)
	{
		this.validator = validator;
	}

	/**
	 * Registers the given listener for <b>all</b> thrown ConstraintViolationExceptions
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
				classListeners = CollectionFactory.INSTANCE.createSet();
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
				objectListeners = CollectionFactory.INSTANCE.createSet(2);
				listenersByObject.put(guardedObject, objectListeners);
			}
			return objectListeners.add(listener);
		}
	}

	/**
	 * @return the exceptionProcessor
	 */
	public ExceptionTranslator getExceptionTranslator()
	{
		return exceptionTranslator;
	}

	/**
	 * Returns the validator used by this guard
	 * @return the validator
	 */
	public Validator getValidator()
	{
		return validator;
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @throws ConstraintsViolatedException
	 * @throws ValidationFailedException
	 */
	void guardConstructorPost(final Object guardedObject, final Constructor constructor,
			final Object[] args) throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return;

		// @PostValidateThis
		if (constructor.isAnnotationPresent(PostValidateThis.class))
		{
			try
			{
				final List<ConstraintViolation> violations = validator.validate(guardedObject);
				if (violations.size() > 0)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations.toArray(new ConstraintViolation[violations.size()]));
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					throwException(violationException);
				}
			}
			catch (ValidationFailedException ex)
			{
				throwException(ex);
			}
		}
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @throws ConstraintsViolatedException if anything precondition is not satisfied
	 * @throws ValidationFailedException 
	 */
	void guardConstructorPre(final Object guardedObject, final Constructor constructor,
			final Object[] args) throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return;

		// constructor parameter validation
		if (args.length > 0)
		{
			try
			{
				final List<ConstraintViolation> violations = validator
						.validateConstructorParameters(guardedObject, constructor, args);

				if (violations != null)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations.toArray(new ConstraintViolation[violations.size()]));
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					throwException(violationException);
				}
			}
			catch (ValidationFailedException ex)
			{
				throwException(ex);
			}
		}
	}

	/**
	 * This method is provided for use by guard aspects.
	 *  
	 * @throws ConstraintsViolatedException
	 * @throws ValidationFailedException
	 */
	void guardMethodPost(final Object guardedObject, final Method method, final Object[] args,
			Object returnValue) throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return;

		try
		{
			// @PostValidateThis
			if (method.isAnnotationPresent(PostValidateThis.class))
			{
				final List<ConstraintViolation> violations = validator.validate(guardedObject);
				if (violations.size() > 0)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations.toArray(new ConstraintViolation[violations.size()]));
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					throwException(violationException);
				}
			}

			// @Post
			final List<ConstraintViolation> violations = validator.validateMethodPost(
					guardedObject, method, args, returnValue);

			if (violations != null)
			{
				final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
						violations.toArray(new ConstraintViolation[violations.size()]));
				if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

				throwException(violationException);
			}
		}
		catch (ValidationFailedException ex)
		{
			throwException(ex);
		}
	}

	/**
	 * This method is provided for use by guard aspects.
	 * 
	 * @return true if valid, false if invalid
	 * @throws ConstraintsViolatedException if ValidationMode is set to THROW_EXCEPTION or if parameter alwaysThrow is true
	 * @throws ValidationFailedException 
	 */
	boolean guardMethodPre(final Object guardedObject, final Method method, final Object[] args)
			throws ConstraintsViolatedException, ValidationFailedException
	{
		if (!isActivated) return true;

		try
		{
			// @PreValidateThis
			if (method.isAnnotationPresent(PreValidateThis.class))
			{
				final List<ConstraintViolation> violations = validator.validate(guardedObject);
				if (violations.size() > 0)
				{
					final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
							violations.toArray(new ConstraintViolation[violations.size()]));
					if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

					// don't throw an exception if the method is a setter and suppressingfor precondition is enabled 
					if (isSuppressFeatureUsed && ReflectionUtils.isSetter(method)
							&& !isSuppressSetterPreConditionExceptions(guardedObject))
						return false;

					throwException(violationException);
				}
			}

			// @Pre validation and method parameter validation
			final List<ConstraintViolation> violations = validator.validateMethodPre(guardedObject,
					method, args);

			if (violations != null)
			{
				final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
						violations.toArray(new ConstraintViolation[violations.size()]));
				if (isListenersFeatureUsed) notifyListeners(guardedObject, violationException);

				// don't throw an exception if the method is a setter and suppressingfor precondition is enabled 
				if (isSuppressFeatureUsed && ReflectionUtils.isSetter(method)
						&& isSuppressSetterPreConditionExceptions(guardedObject)) return false;

				throwException(violationException);
			}
		}
		catch (ValidationFailedException ex)
		{
			throwException(ex);
		}
		
		return true;
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
	 * Determines if ConstraintViolationExceptions for pre condition violations
	 * on setter methods are not thrown by OVal for the current thread.
	 *  
	 * @param guardedClass guarded class or interface
	 * @return true if exceptions are suppressed
	 * @throws IllegalArgumentException if <code>guardedClass == null</code>
	 */
	public boolean isSuppressSetterPreConditionExceptions(final Class guardedClass)
			throws IllegalArgumentException
	{
		if (guardedClass == null)
			throw new IllegalArgumentException("guardedClass cannot be null");

		final boolean isSuppress = unsafeClasses.get().contains(guardedClass);
		if (isSuppress) return true;

		// check the interfaces
		for (final Class clazz : guardedClass.getInterfaces())
		{
			boolean isSuppressI = unsafeClasses.get().contains(clazz);
			if (isSuppressI) return true;
		}
		return false;
	}

	/**
	 * Determines if ConstraintViolationExceptions for pre condition violations
	 * on setter methods are not thrown by OVal for the current thread.
	 *  
	 * @param guardedObject
	 * @return true if exceptions are suppressed
	 * @throws IllegalArgumentException if <code>guardedObject == null</code>
	 */
	public boolean isSuppressSetterPreConditionExceptions(final Object guardedObject)
			throws IllegalArgumentException
	{
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		return unsafeObjects.get().contains(guardedObject) ? true
				: isSuppressSetterPreConditionExceptions(guardedObject.getClass());
	}

	/**
	 * notifies all registered validation listener about the occured constraint violation exception
	 */
	private void notifyListeners(final Object guardedObject, final ConstraintsViolatedException ex)
	{
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
			catch (RuntimeException rex)
			{
				LOG.log(Level.WARNING, "Notifying listener '" + listener + "'failed.", rex);
			}
		}

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
	 * @param isEnabled the isEnabled to set
	 */
	public void setActivated(boolean isActivated)
	{
		this.isActivated = isActivated;
	}

	/**
	 * @param exceptionTranslator the exceptionTranslator to set
	 */
	public void setExceptionTranslator(final ExceptionTranslator exceptionTranslator)
	{
		this.exceptionTranslator = exceptionTranslator;
	}

	/**
	 * Specifies if ConstraintViolationExceptions for pre condition violations
	 * on setter methods should be suppressed by OVal for the current thread.
	 * 
	 * @param guardedObject
	 * @return true if exceptions are suppressed
	 * @throws IllegalArgumentException if <code>guardedClass == null</code>
	 */
	public void setSuppressPreConditionExceptions(final Class guardedClass, boolean doSuppress)
			throws IllegalArgumentException
	{
		if (guardedClass == null)
			throw new IllegalArgumentException("guardedClass cannot be null");

		isSuppressFeatureUsed = true;

		if (doSuppress)
			unsafeClasses.get().add(guardedClass);
		else
			unsafeClasses.get().remove(guardedClass);
	}

	/**
	 * Specifies if ConstraintViolationExceptions for pre condition violations
	 * on setter methods should be suppressed by OVal for the current thread.
	 *   
	 * @param guardedObject
	 * @return true if exceptions are suppressed
	 * @throws IllegalArgumentException if <code>guardedObject == null</code>
	 */
	public void setSuppressPreConditionExceptions(final Object guardedObject, boolean doSuppress)
			throws IllegalArgumentException
	{
		if (guardedObject == null)
			throw new IllegalArgumentException("guardedObject cannot be null");

		isSuppressFeatureUsed = true;

		if (doSuppress)
			unsafeObjects.get().add(guardedObject);
		else
			unsafeObjects.get().remove(guardedObject);
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(final Validator validator)
	{
		this.validator = validator;
	}

	/**
	 * This method will process the exception via the registered ExceptionProcessor
	 * and then throw it.
	 *  
	 * @param ex
	 * @throws RuntimeException
	 */
	public void throwException(final OValException ex) throws RuntimeException
	{
		if (exceptionTranslator != null)
		{
			final RuntimeException rex = exceptionTranslator.translateException(ex);
			if (rex == null) throw rex;
		}
		throw ex;
	}
}
