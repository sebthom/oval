/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.exceptions.InvalidConfigurationException;
import net.sf.oval.utils.ReflectionUtils;

/**
 * This class holds the instantiated checks for a single class
 * 
 * @author Sebastian Thomschke
 */
public final class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * checks on constructors' parameter values
	 */
	final Map<Constructor, Map<Integer, Collection<Check>>> checksByConstructorParameter = CollectionFactory.INSTANCE
			.createMap(4);

	/**
	 * checks on fields' value
	 */
	final Map<Field, Set<Check>> checksByField = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on methods' return value
	 */
	final Map<Method, Set<Check>> checksByMethod = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on methods' parameter values
	 */
	final Map<Method, Map<Integer, Collection<Check>>> checksByMethodParameter = CollectionFactory.INSTANCE
			.createMap();

	final Class clazz;

	final Set<Field> constrainedFields = CollectionFactory.INSTANCE.createSet();
	final Set<Method> constrainedGetters = CollectionFactory.INSTANCE.createSet();
	final Set<Method> constrainedMethods = CollectionFactory.INSTANCE.createSet();

	final Set<Constructor> constrainedParameterizedConstructors = CollectionFactory.INSTANCE
			.createSet(4);

	final Set<Method> constrainedParameterizedMethods = CollectionFactory.INSTANCE.createSet();

	final Map<String, ConstraintSet> constraintSetsByLocalId = CollectionFactory.INSTANCE
			.createMap();

	private final boolean isGuarded;

	/**
	 * package constructor used by the Validator class
	 * 
	 * @param clazz
	 */
	@SuppressWarnings("unchecked")
	ClassChecks(final Class clazz)
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Initializing constraints configuration for class " + clazz);

		this.clazz = clazz;
		isGuarded = IsGuarded.class.isAssignableFrom(clazz);
	}

	/**
	 * adds constraint checks to a constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public synchronized void addChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (constructor.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException(
					"Given constructor does not belong to this class" + clazz.getName());

		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply constructor parameter constraints to class " + clazz.getName()
							+ ". Constraints guarding is not activated for this class.");

		// retrieve the currently registered checks for all parameters of the specified constructor
		Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		Collection<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null)
		{
			checksOfConstructorParameter = CollectionFactory.INSTANCE.createSet(8);
			checksOfConstructorByParameter.put(parameterIndex, checksOfConstructorParameter);
		}

		for (final Check check : checks)
			checksOfConstructorParameter.add(check);
	}

	/**
	 * adds check constraints to a field 
	 *  
	 * @param field
	 * @param checks
	 */
	public synchronized void addChecks(final Field field, final Check... checks)
	{
		if (checks == null || checks.length == 0) return;

		if (field.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given field does not belong to class "
					+ clazz.getName());

		Set<Check> checksOfField = checksByField.get(field);
		if (checksOfField == null)
		{
			checksOfField = CollectionFactory.INSTANCE.createSet(8);
			checksByField.put(field, checksOfField);
			constrainedFields.add(field);
		}

		for (final Check check : checks)
			checksOfField.add(check);
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public synchronized void addChecks(final Method method, final Check... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		// ensure the method has a return type
		if (method.getReturnType() == Void.TYPE)
		{
			throw new InvalidConfigurationException(
					"Adding return value constraints for method "
							+ method
							+ " is not possible. The method is declared as void and does not return any values.");
		}

		final boolean isGetter = ReflectionUtils.isGetter(method);
		if (!isGetter && !isGuarded)
		{
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for method "
							+ method
							+ " not following the JavaBean Getter method convention. Constraints guarding is not activated for this class.");
		}

		constrainedMethods.add(method);
		if (isGetter) constrainedGetters.add(method);

		Set<Check> methodChecks = checksByMethod.get(method);
		if (methodChecks == null)
		{
			methodChecks = CollectionFactory.INSTANCE.createSet(checks.length);
			checksByMethod.put(method, methodChecks);
		}

		for (final Check check : checks)
		{
			methodChecks.add(check);
		}
	}

	/**
	 * adds constraint checks to a method parameter 
	 *  
	 * @param method
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public synchronized void addChecks(final Method method, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ". Constraints guarding is not activated for this class.");

		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Collection<Check>> checksOfMethodByParameter = checksByMethodParameter
				.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		Collection<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null)
		{
			checksOfMethodParameter = CollectionFactory.INSTANCE.createSet(8);
			checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
		}

		for (final Check check : checks)
			checksOfMethodParameter.add(check);
	}

	ConstraintSet addFieldConstraintSet(final Field field, final String localId)
	{
		if (field.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given field does not belong to this class"
					+ clazz.getName());

		final ConstraintSet cs = new ConstraintSet();
		cs.context = new FieldContext(field);
		cs.localId = localId;
		cs.id = field.getDeclaringClass().getName() + "." + localId;
		constraintSetsByLocalId.put(localId, cs);
		return cs;
	}

	synchronized void removeAllCheck(final Field field) throws InvalidConfigurationException
	{
		checksByField.remove(field);
		constrainedFields.remove(field);
	}

	synchronized void removeAllChecks(final Constructor constructor)
			throws InvalidConfigurationException
	{
		checksByConstructorParameter.remove(constructor);
		constrainedParameterizedConstructors.remove(constructor);
	}

	synchronized void removeAllChecks(final Constructor constructor, final int parameterIndex)
			throws InvalidConfigurationException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfConstructorByParameter.remove(parameterIndex);
		if (checksOfConstructorByParameter.size() == 0)
			constrainedParameterizedConstructors.remove(constructor);
	}

	synchronized void removeAllChecks(final Method method) throws InvalidConfigurationException
	{
		removeAllParameterChecks(method);
		removeAllReturnValueChecks(method);
	}

	synchronized void removeAllChecks(final Method method, final int parameterIndex)
			throws InvalidConfigurationException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfMethodByParameter = checksByMethodParameter
				.get(method);
		if (checksOfMethodByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfMethodByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfMethodByParameter.remove(parameterIndex);
		if (checksOfMethodByParameter.size() == 0) constrainedParameterizedMethods.remove(method);
	}

	synchronized void removeAllParameterChecks(final Method method)
			throws InvalidConfigurationException
	{
		checksByMethodParameter.remove(method);
		constrainedParameterizedMethods.remove(method);
	}

	synchronized void removeAllReturnValueChecks(final Method method)
			throws InvalidConfigurationException
	{
		checksByMethod.remove(method);
		constrainedGetters.remove(method);
		constrainedMethods.remove(method);
	}

	public synchronized void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws InvalidConfigurationException
	{
		if (constructor.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException(
					"Given constructor does not belong to this class" + clazz.getName());

		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null) return;

		checksOfConstructorParameter.remove(check);

		if (checksOfConstructorParameter.size() == 0)
		{
			checksOfConstructorByParameter.remove(parameterIndex);
			if (checksOfConstructorByParameter.size() == 0)
				constrainedParameterizedConstructors.remove(constructor);
		}
	}

	public synchronized void removeCheck(final Field field, final Check check)
			throws InvalidConfigurationException
	{
		if (field.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given field does not belong to class "
					+ clazz.getName());

		final Set<Check> checksOfField = checksByField.get(field);

		if (checksOfField == null) return;

		checksOfField.remove(check);
		if (checksOfField.size() == 0)
		{
			checksByField.remove(field);
			constrainedFields.remove(field);
		}
	}

	public synchronized void removeCheck(final Method method, final Check check)
			throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		final Set<Check> checks = checksByMethod.get(method);

		if (checks == null) return;

		checks.remove(check);
		if (checks.size() == 0)
		{
			checksByMethod.remove(method);
			constrainedGetters.remove(method);
			constrainedMethods.remove(method);
		}
	}

	public synchronized void removeCheck(final Method method, final int parameterIndex,
			final Check check) throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfMethodByParameter = checksByMethodParameter
				.get(method);
		if (checksOfMethodByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfMethodByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfMethodParameter.remove(check);

		if (checksOfMethodParameter.size() == 0)
		{
			checksOfMethodByParameter.remove(parameterIndex);
			if (checksOfMethodByParameter.size() == 0)
				constrainedParameterizedMethods.remove(method);
		}
	}

	synchronized void reset()
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Clearing all checks for class " + clazz.getName());

		checksByConstructorParameter.clear();
		checksByField.clear();
		checksByMethod.clear();
		checksByMethodParameter.clear();
		constrainedFields.clear();
		constrainedGetters.clear();
		constrainedMethods.clear();
		constrainedParameterizedConstructors.clear();
		constrainedParameterizedMethods.clear();
		constraintSetsByLocalId.clear();
	}
}
