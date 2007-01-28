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
import net.sf.oval.guard.IsGuarded;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.utils.ReflectionUtils;

/**
 * This class holds the instantiated checks for a single class
 * 
 * Internal implementation.
 * 
 * @author Sebastian Thomschke
 */
public final class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * checks on constructors' parameter values
	 */
	final Map<Constructor, Map<Integer, Collection<Check>>> checksForConstructorParameter = CollectionFactory.INSTANCE
			.createMap(4);

	/**
	 * checks on fields' value
	 */
	final Map<Field, Set<Check>> checksForFields = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on methods' return value
	 */
	final Map<Method, Set<Check>> checksForMethodReturnValues = CollectionFactory.INSTANCE
			.createMap();

	/**
	 * checks on methods' parameter values
	 */
	final Map<Method, Map<Integer, Collection<Check>>> checksForMethodParameters = CollectionFactory.INSTANCE
			.createMap();

	final Map<Method, Set<PostCheck>> checksForMethodsPostExcecution = CollectionFactory.INSTANCE
			.createMap();

	final Map<Method, Set<PreCheck>> checksForMethodsPreExecution = CollectionFactory.INSTANCE
			.createMap();

	final Class clazz;

	/**
	 * all non-static fields that have value constraints.
	 * Validator loops over this set during validation.
	 */
	final Set<Field> constrainedStaticFields = CollectionFactory.INSTANCE.createSet();

	/**
	 * all non-static getters that have return value constraints.
	 * Validator loops over this set during validation.
	 */
	final Set<Method> constrainedStaticGetters = CollectionFactory.INSTANCE.createSet();

	/**
	 * all non-static fields that have value constraints.
	 * Validator loops over this set during validation.
	 */
	final Set<Field> constrainedFields = CollectionFactory.INSTANCE.createSet();

	/**
	 * all non-static getters that have return value constraints.
	 * Validator loops over this set during validation.
	 */
	final Set<Method> constrainedGetters = CollectionFactory.INSTANCE.createSet();

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
		Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksForConstructorParameter.put(constructor, checksOfConstructorByParameter);
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
	 * @throws InvalidConfigurationException 
	 */
	public synchronized void addChecks(final Field field, final Check... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (field.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given field does not belong to class "
					+ clazz.getName());

		Set<Check> checksOfField = checksForFields.get(field);
		if (checksOfField == null)
		{
			checksOfField = CollectionFactory.INSTANCE.createSet(8);
			checksForFields.put(field, checksOfField);
			if (ReflectionUtils.isStatic(field))
				constrainedStaticFields.add(field);
			else
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

		if (isGetter)
		{
			if (ReflectionUtils.isStatic(method))
				constrainedStaticGetters.add(method);
			else
				constrainedGetters.add(method);
		}

		Set<Check> methodChecks = checksForMethodReturnValues.get(method);
		if (methodChecks == null)
		{
			methodChecks = CollectionFactory.INSTANCE.createSet(checks.length);
			checksForMethodReturnValues.put(method, methodChecks);
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
		Map<Integer, Collection<Check>> checksOfMethodByParameter = checksForMethodParameters
				.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksForMethodParameters.put(method, checksOfMethodByParameter);
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

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public synchronized void addChecks(final Method method, final PostCheck... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		if (!isGuarded)
		{
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");
		}

		Set<PostCheck> postChecks = checksForMethodsPostExcecution.get(method);
		if (postChecks == null)
		{
			postChecks = CollectionFactory.INSTANCE.createSet(checks.length);
			checksForMethodsPostExcecution.put(method, postChecks);
		}

		for (final PostCheck check : checks)
		{
			postChecks.add(check);
		}
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public synchronized void addChecks(final Method method, final PreCheck... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		if (!isGuarded)
		{
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");
		}

		Set<PreCheck> preChecks = checksForMethodsPreExecution.get(method);
		if (preChecks == null)
		{
			preChecks = CollectionFactory.INSTANCE.createSet(checks.length);
			checksForMethodsPreExecution.put(method, preChecks);
		}

		for (final PreCheck check : checks)
		{
			preChecks.add(check);
		}
	}

	ConstraintSet addFieldConstraintSet(final Field field, final String localId)
			throws InvalidConfigurationException
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

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllCheck(final Field field)
	{
		checksForFields.remove(field);
		constrainedFields.remove(field);
		constrainedStaticFields.remove(field);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllChecks(final Constructor constructor)
	{
		checksForConstructorParameter.remove(constructor);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllChecks(final Constructor constructor, final int parameterIndex)
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfConstructorByParameter.remove(parameterIndex);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllChecks(final Method method)
	{
		removeAllParameterChecks(method);
		removeAllReturnValueChecks(method);
		removeAllPreChecks(method);
		removeAllPostChecks(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllChecks(final Method method, final int parameterIndex)
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfMethodByParameter = checksForMethodParameters
				.get(method);
		if (checksOfMethodByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfMethodByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfMethodByParameter.remove(parameterIndex);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllParameterChecks(final Method method)
	{
		checksForMethodParameters.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllPostChecks(final Method method)
	{
		checksForMethodsPostExcecution.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllPreChecks(final Method method)
	{
		checksForMethodsPreExecution.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	synchronized void removeAllReturnValueChecks(final Method method)
	{
		checksForMethodReturnValues.remove(method);
		constrainedGetters.remove(method);
		constrainedStaticGetters.remove(method);
	}

	public synchronized void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws InvalidConfigurationException
	{
		if (constructor.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException(
					"Given constructor does not belong to this class" + clazz.getName());

		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameter
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
		}
	}

	public synchronized void removeCheck(final Field field, final Check check)
			throws InvalidConfigurationException
	{
		if (field.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given field does not belong to class "
					+ clazz.getName());

		final Set<Check> checksOfField = checksForFields.get(field);

		if (checksOfField == null) return;

		checksOfField.remove(check);
		if (checksOfField.size() == 0)
		{
			checksForFields.remove(field);
			constrainedFields.remove(field);
			constrainedStaticFields.remove(field);
		}
	}

	public synchronized void removeCheck(final Method method, final Check check)
			throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		final Set<Check> checks = checksForMethodReturnValues.get(method);

		if (checks == null) return;

		checks.remove(check);
		if (checks.size() == 0)
		{
			checksForMethodReturnValues.remove(method);
			constrainedGetters.remove(method);
			constrainedStaticGetters.remove(method);
		}
	}

	public synchronized void removeCheck(final Method method, final int parameterIndex,
			final Check check) throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfMethodByParameter = checksForMethodParameters
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
		}
	}

	public synchronized void removeCheck(final Method method, final PostCheck check)
			throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		final Set<PostCheck> checks = checksForMethodsPostExcecution.get(method);

		if (checks == null) return;

		checks.remove(check);
		if (checks.size() == 0)
		{
			checksForMethodsPostExcecution.remove(method);
		}
	}

	public synchronized void removeCheck(final Method method, final PreCheck check)
			throws InvalidConfigurationException
	{
		if (method.getDeclaringClass() != clazz)
			throw new InvalidConfigurationException("Given method does not belong to class "
					+ clazz.getName());

		final Set<PreCheck> checks = checksForMethodsPreExecution.get(method);

		if (checks == null) return;

		checks.remove(check);
		if (checks.size() == 0)
		{
			checksForMethodsPreExecution.remove(method);
		}
	}

	synchronized void reset()
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Clearing all checks for class " + clazz.getName());

		checksForMethodsPostExcecution.clear();
		checksForMethodsPreExecution.clear();
		checksForConstructorParameter.clear();
		checksForFields.clear();
		checksForMethodReturnValues.clear();
		checksForMethodParameters.clear();
		constrainedFields.clear();
		constrainedStaticFields.clear();
		constrainedGetters.clear();
		constrainedStaticGetters.clear();
		constraintSetsByLocalId.clear();
	}
}
