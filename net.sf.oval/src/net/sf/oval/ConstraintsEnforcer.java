/*
 * Created on 06.04.2006
 */
package net.sf.oval;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 *
 */
public class ConstraintsEnforcer
{
	/**
	 * The mode how the constraints enforcer should report detected constraint violations.
	 * 
	 * @author Sebastian Thomschke
	 */
	public static enum ReportingMode
	{
		/**
		 * notify listeners about a detected constraint violation but do not throw
		 */
		NOTIFY_LISTENERS,

		/**
		 * notify listeners about a detected constraint violation and additionally throw a ContraintsViolatatedException 
		 */
		NOTIFY_LISTENERS_AND_THROW_EXCEPTION
	}

	private final Validator validator;

	private final Map<Class, Set<ConstraintsViolatedListener>> listenersByClass = new WeakHashMap<Class, Set<ConstraintsViolatedListener>>();
	private final Map<Object, Set<ConstraintsViolatedListener>> listenersByObject = new WeakHashMap<Object, Set<ConstraintsViolatedListener>>();

	private final Map<Class, ReportingMode> reportingModesByClass = new WeakHashMap<Class, ReportingMode>();
	private final Map<Object, ReportingMode> reportingModesByObject = new WeakHashMap<Object, ReportingMode>();

	/**
	 * default reporting mode
	 */
	private ReportingMode reportingMode = ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION;

	public ConstraintsEnforcer(final Validator validator)
	{
		this.validator = validator;
	}

	public void addListener(final ConstraintsViolatedListener listener, final Class clazz)
	{
		if (clazz == null || listener == null) return;

		Set<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null)
		{
			currentListeners = CollectionFactory.INSTANCE.createSet();
			listenersByClass.put(clazz, currentListeners);
		}
		currentListeners.add(listener);
	}

	public void addListener(final ConstraintsViolatedListener listener, final Object validatedObject)
	{
		if (validatedObject == null || listener == null) return;

		Set<ConstraintsViolatedListener> currentListeners = listenersByObject.get(validatedObject);

		if (currentListeners == null)
		{
			currentListeners = CollectionFactory.INSTANCE.createSet(2);
			listenersByObject.put(validatedObject, currentListeners);
		}
		currentListeners.add(listener);
	}

	/**
	 * Gets the default  mode.
	 * @return the mode
	 */
	public ReportingMode getReportingMode()
	{
		return reportingMode;
	}

	public ReportingMode getReportingMode(final Class clazz)
	{
		if (clazz == null) return reportingMode;

		final ReportingMode classMode = reportingModesByClass.get(clazz);
		return classMode == null ? reportingMode : classMode;
	}

	public ReportingMode getReportingMode(final Object validatedObject)
	{
		if (validatedObject == null) return reportingMode;

		final ReportingMode objectMode = reportingModesByObject.get(validatedObject);
		return objectMode == null ? getReportingMode(validatedObject.getClass()) : objectMode;
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
		final Set<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null) return false;

		return currentListeners.contains(listener);
	}

	public boolean hasListener(final ConstraintsViolatedListener listener,
			final Object validatedObject)
	{
		final Set<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(validatedObject);

		if (currentListeners == null) return false;

		return currentListeners.contains(listener);
	}

	/**
	 * notifies all registered validation listener about the occured constraint violation exception
	 */
	private void notifyListeners(final Object validatedObject, final ConstraintsViolatedException ex)
	{
		final List<ConstraintsViolatedListener> notifiedListeners = CollectionFactory.INSTANCE
				.createList();

		// notifiy object listeners
		{
			final Set<ConstraintsViolatedListener> currentListeners = listenersByObject
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
			final Set<ConstraintsViolatedListener> currentListeners = listenersByClass
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
		final Set<ConstraintsViolatedListener> currentListeners = listenersByClass.get(clazz);

		if (currentListeners == null) return;

		currentListeners.remove(listener);
	}

	public void removeListener(final ConstraintsViolatedListener listener,
			final Object validatedObject)
	{
		final Set<ConstraintsViolatedListener> currentListeners = listenersByObject
				.get(validatedObject);

		if (currentListeners == null) return;

		currentListeners.remove(listener);
	}

	/**
	 * Sets the default validation mode for all constrained objects
	 * accessed within the current thread.
	 * 
	 * @param newDefaultReportingMode the validation mode to set
	 */
	public void setReportingMode(final ReportingMode newDefaultReportingMode)
	{
		reportingMode = newDefaultReportingMode;
	}

	public void setReportingMode(final ReportingMode reportingMode, final Class clazz)
	{
		if (clazz == null || reportingMode == null) return;

		reportingModesByClass.put(clazz, reportingMode);
	}

	public void setReportingMode(final ReportingMode reportingMode, final Object validatedObject)
	{
		if (validatedObject == null || reportingMode == null) return;

		reportingModesByObject.put(validatedObject, reportingMode);
	}

	public void unsetReportingMode(final Class clazz)
	{
		reportingModesByClass.remove(clazz);
	}

	public void unsetReportingMode(final Object validatedObject)
	{
		reportingModesByObject.remove(validatedObject);
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
			if (getReportingMode(validatedObject) == ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION)
				throw violationException;

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

		if (getReportingMode(validatedObject) == ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION)
			throw violationException;

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

		if (getReportingMode(validatedObject) == ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION)
			throw violationException;

		return false;
	}
}
