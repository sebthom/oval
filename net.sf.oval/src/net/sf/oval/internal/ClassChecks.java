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
package net.sf.oval.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.Check;
import net.sf.oval.collection.CollectionFactoryHolder;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.guard.IsGuarded;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * This class holds the instantiated checks for a single class
 * 
 * @author Sebastian Thomschke
 */
public class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * checks on constructors' parameter values
	 */
	public final Map<Constructor, Map<Integer, Collection<Check>>> checksForConstructorParameters = CollectionFactoryHolder
			.getFactory().createMap(4);

	/**
	 * checks on fields' value
	 */
	public final Map<Field, Set<Check>> checksForFields = CollectionFactoryHolder.getFactory()
			.createMap();

	/**
	 * checks on methods' return value
	 */
	public final Map<Method, Set<Check>> checksForMethodReturnValues = CollectionFactoryHolder
			.getFactory().createMap();

	public final Set<Method> methodsWithCheckInvariantsPre = CollectionFactoryHolder.getFactory()
			.createSet();
	
	public final Set<AccessibleObject> methodsWithCheckInvariantsPost = CollectionFactoryHolder.getFactory()
			.createSet();

	/**
	 * checks on methods' parameter values
	 */
	public final Map<Method, Map<Integer, Collection<Check>>> checksForMethodParameters = CollectionFactoryHolder
			.getFactory().createMap();

	public final Map<Method, Set<PostCheck>> checksForMethodsPostExcecution = CollectionFactoryHolder
			.getFactory().createMap();

	public final Map<Method, Set<PreCheck>> checksForMethodsPreExecution = CollectionFactoryHolder
			.getFactory().createMap();

	public final Class clazz;

	/**
	 * all non-static fields that have value constraints.
	 * Validator loops over this set during validation.
	 */
	public final Set<Field> constrainedStaticFields = CollectionFactoryHolder.getFactory()
			.createSet();

	/**
	 * all static non-void, non-parameterized methods marked as invariant that have return value constraints.
	 * Validator loops over this set during validation.
	 */
	public final Set<Method> constrainedStaticMethods = CollectionFactoryHolder.getFactory()
			.createSet();

	/**
	 * all non-static fields that have value constraints.
	 * Validator loops over this set during validation.
	 */
	public final Set<Field> constrainedFields = CollectionFactoryHolder.getFactory().createSet();

	/**
	 * all non-static non-void, non-parameterized methods marked as invariant that have return value constraints.
	 * Validator loops over this set during validation.
	 */
	public final Set<Method> constrainedMethods = CollectionFactoryHolder.getFactory().createSet();

	public final boolean isGuarded;

	public boolean isCheckInvariants;

	/**
	 * package constructor used by the Validator class
	 * 
	 * @param clazz
	 */
	@SuppressWarnings("unchecked")
	public ClassChecks(final Class clazz)
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
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect 
	 */
	public synchronized void addChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply constructor parameter constraints to constructor " + constructor
							+ ". Constraints guarding is not activated for this class.");

		if (parameterIndex < 0 || parameterIndex > constructor.getParameterTypes().length)
			throw new InvalidConfigurationException("ParameterIndex is out of range");

		// retrieve the currently registered checks for all parameters of the specified constructor
		Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameters
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = CollectionFactoryHolder.getFactory().createMap(8);
			checksForConstructorParameters.put(constructor, checksOfConstructorByParameter);
		}

		// retrieve the checks for the specified parameter
		Collection<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null)
		{
			checksOfConstructorParameter = CollectionFactoryHolder.getFactory().createSet(8);
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
			throws InvalidConfigurationException
	{
		Set<Check> checksOfField = checksForFields.get(field);
		if (checksOfField == null)
		{
			checksOfField = CollectionFactoryHolder.getFactory().createSet(8);
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
	 * @param isInvariant determines if the return value should be checked when the object is validated, can be null
	 * @param checks
	 */
	public synchronized void addChecks(final Method method, final Boolean isInvariant,
			final Check... checks) throws InvalidConfigurationException
	{
		// ensure the method has a return type
		if (method.getReturnType() == Void.TYPE)
		{
			throw new InvalidConfigurationException(
					"Adding return value constraints for method "
							+ method
							+ " is not possible. The method is declared as void and does not return any values.");
		}

		if (ReflectionUtils.isVoidMethod(method))
		{
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for void method " + method);
		}

		final boolean hasParameters = method.getParameterTypes().length > 0;

		if (!isGuarded && hasParameters)
		{
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for parameterized method "
							+ method + ". Constraints guarding is not activated for this class.");
		}

		final boolean isInvariant2 = isInvariant == null ? constrainedMethods.contains(method)
				: isInvariant;

		if (!isGuarded && !isInvariant2)
		{
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for method "
							+ method
							+ ". The method needs to be marked as being invariant (@IsInvariant) since constraints guarding is not activated for this class.");
		}

		if (!hasParameters && isInvariant2)
		{
			if (ReflectionUtils.isStatic(method))
				constrainedStaticMethods.add(method);
			else
				constrainedMethods.add(method);
		}
		else
		{
			if (ReflectionUtils.isStatic(method))
				constrainedStaticMethods.remove(method);
			else
				constrainedMethods.remove(method);
		}

		Set<Check> methodChecks = checksForMethodReturnValues.get(method);
		if (methodChecks == null)
		{
			methodChecks = CollectionFactoryHolder.getFactory().createSet(checks.length);
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
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public synchronized void addChecks(final Method method, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ". Constraints guarding is not activated for this class.");

		if (parameterIndex < 0 || parameterIndex > method.getParameterTypes().length)
			throw new InvalidConfigurationException("ParameterIndex is out of range");

		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Collection<Check>> checksOfMethodByParameter = checksForMethodParameters
				.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = CollectionFactoryHolder.getFactory().createMap(8);
			checksForMethodParameters.put(method, checksOfMethodByParameter);
		}

		// retrieve the checks for the specified parameter
		Collection<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null)
		{
			checksOfMethodParameter = CollectionFactoryHolder.getFactory().createSet(8);
			checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
		}

		for (final Check check : checks)
			checksOfMethodParameter.add(check);
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public synchronized void addChecks(final Method method, final PostCheck... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (!isGuarded)
		{
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");
		}

		Set<PostCheck> postChecks = checksForMethodsPostExcecution.get(method);
		if (postChecks == null)
		{
			postChecks = CollectionFactoryHolder.getFactory().createSet(checks.length);
			checksForMethodsPostExcecution.put(method, postChecks);
		}

		for (final PostCheck check : checks)
		{
			postChecks.add(check);
		}
	}

	/**
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public synchronized void addChecks(final Method method, final PreCheck... checks)
			throws InvalidConfigurationException
	{
		if (checks == null || checks.length == 0) return;

		if (!isGuarded)
		{
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");
		}

		Set<PreCheck> preChecks = checksForMethodsPreExecution.get(method);
		if (preChecks == null)
		{
			preChecks = CollectionFactoryHolder.getFactory().createSet(checks.length);
			checksForMethodsPreExecution.put(method, preChecks);
		}

		for (final PreCheck check : checks)
		{
			preChecks.add(check);
		}
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllCheck(final Field field)
	{
		checksForFields.remove(field);
		constrainedFields.remove(field);
		constrainedStaticFields.remove(field);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllChecks(final Constructor constructor)
	{
		checksForConstructorParameters.remove(constructor);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllChecks(final Constructor constructor, final int parameterIndex)
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameters
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
	public synchronized void removeAllChecks(final Method method)
	{
		removeAllParameterChecks(method);
		removeAllReturnValueChecks(method);
		removeAllPreChecks(method);
		removeAllPostChecks(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllChecks(final Method method, final int parameterIndex)
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
	public synchronized void removeAllParameterChecks(final Method method)
	{
		checksForMethodParameters.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllPostChecks(final Method method)
	{
		checksForMethodsPostExcecution.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllPreChecks(final Method method)
	{
		checksForMethodsPreExecution.remove(method);
	}

	/**
	 * Used by Validator during configuration
	 */
	public synchronized void removeAllReturnValueChecks(final Method method)
	{
		checksForMethodReturnValues.remove(method);
		constrainedMethods.remove(method);
		constrainedStaticMethods.remove(method);
	}

	public synchronized void removeChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfConstructorByParameter = checksForConstructorParameters
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null) return;

		for (final Check check : checks)
		{
			checksOfConstructorParameter.remove(check);
		}

		if (checksOfConstructorParameter.size() == 0)
		{
			checksOfConstructorByParameter.remove(parameterIndex);
		}
	}

	public synchronized void removeChecks(final Field field, final Check... checks)
	{
		final Set<Check> checksOfField = checksForFields.get(field);

		if (checksOfField == null) return;

		for (final Check check : checks)
		{
			checksOfField.remove(check);
		}

		if (checksOfField.size() == 0)
		{
			checksForFields.remove(field);
			constrainedFields.remove(field);
			constrainedStaticFields.remove(field);
		}
	}

	public synchronized void removeChecks(final Method method, final Check... checks)
	{
		final Set<Check> checksOfMethod = checksForMethodReturnValues.get(method);

		if (checksOfMethod == null) return;

		for (final Check check : checks)
		{
			checksOfMethod.remove(check);
		}

		if (checksOfMethod.size() == 0)
		{
			checksForMethodReturnValues.remove(method);
			constrainedMethods.remove(method);
			constrainedStaticMethods.remove(method);
		}
	}

	public synchronized void removeChecks(final Method method, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (parameterIndex < 0 || parameterIndex > method.getParameterTypes().length)
			throw new InvalidConfigurationException("ParameterIndex is out of range");

		// retrieve the currently registered checks for all parameters of the specified method
		final Map<Integer, Collection<Check>> checksOfMethodByParameter = checksForMethodParameters
				.get(method);
		if (checksOfMethodByParameter == null) return;

		// retrieve the checks for the specified parameter
		final Collection<Check> checksOfMethodParameter = checksOfMethodByParameter
				.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		for (final Check check : checks)
		{
			checksOfMethodParameter.remove(check);
		}

		if (checksOfMethodParameter.size() == 0)
		{
			checksOfMethodByParameter.remove(parameterIndex);
		}
	}

	public synchronized void removeChecks(final Method method, final PostCheck... checks)
			throws InvalidConfigurationException
	{
		final Set<PostCheck> checksforMethod = checksForMethodsPostExcecution.get(method);

		if (checks == null) return;

		for (final PostCheck check : checks)
		{
			checksforMethod.remove(check);
		}

		if (checksforMethod.size() == 0)
		{
			checksForMethodsPostExcecution.remove(method);
		}
	}

	public synchronized void removeChecks(final Method method, final PreCheck... checks)
			throws InvalidConfigurationException
	{
		final Set<PreCheck> checksforMethod = checksForMethodsPreExecution.get(method);

		if (checks == null) return;

		for (final PreCheck check : checks)
		{
			checksforMethod.remove(check);
		}

		if (checksforMethod.size() == 0)
		{
			checksForMethodsPreExecution.remove(method);
		}
	}

	public synchronized void reset()
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Clearing all checks for class " + clazz.getName());

		checksForMethodsPostExcecution.clear();
		checksForMethodsPreExecution.clear();
		checksForConstructorParameters.clear();
		checksForFields.clear();
		checksForMethodReturnValues.clear();
		checksForMethodParameters.clear();
		constrainedFields.clear();
		constrainedStaticFields.clear();
		constrainedMethods.clear();
		constrainedStaticMethods.clear();
	}
}
