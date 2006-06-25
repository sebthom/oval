/*
 * Created on 06.04.2006
 */
package net.sf.oval;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.WeakHashMap;

import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 *
 */
public class ConstraintsEnforcer
{
	/**
	 * The mode how oval should respond to detected constraint violations.
	 * 
	 * @author Sebastian Thomschke
	 */
	public static enum Mode
	{
		/**
		 * notify listeners about a detected constraint violation 
		 */
		NOTIFY_LISTENERS,

		/**
		 * notify listeners about a detected constraint violation and additionally throw a ContraintsViolatatedException 
		 */
		THROW_EXCEPTION
	}

	protected Validator validator;

	private final WeakHashMap<Class, HashSet<ConstraintsViolatedListener>> listenersByClass = new WeakHashMap<Class, HashSet<ConstraintsViolatedListener>>();
	private final WeakHashMap<Object, HashSet<ConstraintsViolatedListener>> listenersByObject = new WeakHashMap<Object, HashSet<ConstraintsViolatedListener>>();

	private final WeakHashMap<Class, Mode> modesByClass = new WeakHashMap<Class, Mode>();
	private final WeakHashMap<Object, Mode> modesByObject = new WeakHashMap<Object, Mode>();

	/**
	 * default reporting mode
	 */
	private Mode mode = Mode.THROW_EXCEPTION;

	public ConstraintsEnforcer(final Validator validator)
	{
		this.validator = validator;
	}

	public void addListener(final ConstraintsViolatedListener listener, final Class clazz)
	{
		if (clazz == null || listener == null) return;

		HashSet<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null)
		{
			currentListeners = new HashSet<ConstraintsViolatedListener>();
			listenersByClass.put(clazz, currentListeners);
		}
		currentListeners.add(listener);
	}

	//

	public void addListener(final ConstraintsViolatedListener listener, final Object validatedObject)
	{
		if (validatedObject == null || listener == null) return;

		HashSet<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(validatedObject);

		if (currentListeners == null)
		{
			currentListeners = new HashSet<ConstraintsViolatedListener>();
			listenersByObject.put(validatedObject, currentListeners);
		}
		currentListeners.add(listener);
	}

	/**
	 * Gets the default  mode.
	 * @return the mode
	 */
	public Mode getMode()
	{
		return mode;
	}

	public Mode getMode(final Class clazz)
	{
		if (clazz == null) return mode;

		final Mode classMode = modesByClass.get(clazz);
		return classMode == null ? mode : classMode;
	}

	public Mode getMode(final Object validatedObject)
	{
		if (validatedObject == null) return mode;

		final Mode objectMode = modesByObject.get(validatedObject);
		return objectMode == null ? getMode(validatedObject.getClass()) : objectMode;
	}

	/**
	 * @return the validator
	 */
	public Validator getValidator()
	{
		return validator;
	}

	public boolean hasListener(final ConstraintsViolatedListener listener, final Class clazz)
	{
		final HashSet<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null) return false;

		return currentListeners.contains(listener);
	}

	public boolean hasListener(final ConstraintsViolatedListener listener,
			final Object validatedObject)
	{
		final HashSet<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(validatedObject);

		if (currentListeners == null) return false;

		return currentListeners.contains(listener);
	}

	/**
	 * notifies all registered validation listener about the occured constraint violation exception
	 */
	private void notifyListeners(final Object validatedObject, final ConstraintsViolatedException ex)
	{
		final ArrayList<ConstraintsViolatedListener> notifiedListeners = new ArrayList<ConstraintsViolatedListener>();

		// notifiy object listeners
		{
			final HashSet<ConstraintsViolatedListener> currentListeners = listenersByObject
					.get(validatedObject);
			if (currentListeners != null)
			{
				for (final ConstraintsViolatedListener listener : currentListeners)
				{
					listener.onConstraintsViolatedException(ex);
					notifiedListeners.add(listener);
				}
			}
		}

		// notifiy class listeners
		{
			final HashSet<ConstraintsViolatedListener> currentListeners = listenersByClass
					.get(validatedObject);
			if (currentListeners != null)
			{
				for (final ConstraintsViolatedListener listener : currentListeners)
				{
					if (!notifiedListeners.contains(listener))
						listener.onConstraintsViolatedException(ex);
				}
			}
		}
	}

	public void removeListener(final ConstraintsViolatedListener listener, final Class clazz)
	{
		final HashSet<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null) return;

		currentListeners.remove(listener);
	}

	public void removeListener(final ConstraintsViolatedListener listener,
			final Object validatedObject)
	{
		final HashSet<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(validatedObject);

		if (currentListeners == null) return;

		currentListeners.remove(listener);
	}

	/**
	 * Sets the default validation mode.
	 * @param newDefaultMode the validation mode to set
	 */
	public void setMode(final Mode newDefaultMode)
	{
		mode = newDefaultMode;
	}

	public void setMode(final Mode mode, final Class clazz)
	{
		if (clazz == null || mode == null) return;

		modesByClass.put(clazz, mode);
	}

	public void setMode(final Mode mode, final Object validatedObject)
	{
		if (validatedObject == null || mode == null) return;

		modesByObject.put(validatedObject, mode);
	}

	public void unsetMode(final Class clazz)
	{
		modesByClass.remove(clazz);
	}

	public void unsetMode(final Object validatedObject)
	{
		modesByObject.remove(validatedObject);
	}

	/**
	 * used by ConstraintsEnforcerAspect
	 * 
	 * @return true if valid, false if invalid
	 * @throws ConstraintsViolatedException if ValidationMode is set to THROW_EXCEPTION or if parameter alwaysThrow is true
	 */
	public boolean validate(final Object validatedObject, final boolean alwaysThrow)
			throws ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validator.validate(validatedObject);
		if (violations.size() > 0)
		{
			final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
					violations.toArray(new ConstraintViolation[violations.size()]));
			notifyListeners(validatedObject, violationException);

			if (alwaysThrow) throw violationException;
			if (getMode(validatedObject) == Mode.THROW_EXCEPTION) throw violationException;
		}
		return violations.size() == 0;
	}

	/**
	 * used by ConstraintsEnforcerAspect
	 * 
	 * @throws ConstraintsViolatedException if any parameter is invalid
	 */
	public void validateConstructorParameters(final Object validatedObject,
			final Constructor constructor, final Object[] parameters)
			throws ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validator.validateConstructorParameters(
				validatedObject, constructor, parameters);

		if (violations == null) return;

		final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
				violations.toArray(new ConstraintViolation[violations.size()]));
		notifyListeners(validatedObject, violationException);

		throw violationException;
	}

	/**
	 * used by ConstraintsEnforcerAspect
	 * 
	 * @return true if valid, false if invalid
	 * @throws ConstraintsViolatedException if ValidationMode is set to THROW_EXCEPTION or if parameter alwaysThrow is true
	 */
	public boolean validateMethodParameters(final Object validatedObject, final Method method,
			final Object[] parameters) throws ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validator.validateMethodParameters(
				validatedObject, method, parameters);

		if (violations == null) return true;

		final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
				violations.toArray(new ConstraintViolation[violations.size()]));
		notifyListeners(validatedObject, violationException);

		if (getMode(validatedObject) == Mode.THROW_EXCEPTION) throw violationException;

		return false;
	}

	/**
	 * used by ConstraintsEnforcerAspect
	 * 
	 * @return true if valid, false if invalid
	 * @throws ConstraintsViolatedException if ValidationMode is set to THROW_EXCEPTION or if parameter alwaysThrow is true
	 */
	public boolean validateMethodReturnValue(final Object validatedObject, final Method method,
			final Object methodReturnValue) throws ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validator.validateMethodReturnValue(
				validatedObject, method, methodReturnValue);

		if (violations == null) return true;

		final ConstraintsViolatedException violationException = new ConstraintsViolatedException(
				violations.toArray(new ConstraintViolation[violations.size()]));
		notifyListeners(validatedObject, violationException);

		if (getMode(validatedObject) == Mode.THROW_EXCEPTION) throw violationException;

		return false;
	}
}
