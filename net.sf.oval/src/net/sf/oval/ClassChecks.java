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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.annotations.Constrained;
import net.sf.oval.aspectj.ConstraintsEnforcementIsEnabled;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.exceptions.InvalidConfigurationException;
import net.sf.oval.utils.ReflectionUtils;

/**
 * This class holds the instantiated checks for a single class
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
final class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * checks on constructors' parameter values
	 */
	final Map<Constructor, Map<Integer, Set<Check>>> checksByConstructorParameter = CollectionFactory.INSTANCE
			.createMap(4);

	/**
	 * checks on fields' value
	 */
	final Map<Field, Set<Check>> checksByField = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on getter methods' return value
	 */
	final Map<Method, Set<Check>> checksByGetter = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on parameterized methods' return value
	 */
	final Map<Method, Set<Check>> checksByMethod = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on methods' parameter values
	 */
	final Map<Method, Map<Integer, Set<Check>>> checksByMethodParameter = CollectionFactory.INSTANCE
			.createMap();

	final Class clazz;

	final Constrained constrainedAnnotation;

	final Set<Field> constrainedFields = CollectionFactory.INSTANCE.createSet();
	final Set<Method> constrainedGetters = CollectionFactory.INSTANCE.createSet();
	final Set<Method> constrainedMethods = CollectionFactory.INSTANCE.createSet();

	final Set<Constructor> constrainedParameterizedConstructors = CollectionFactory.INSTANCE
			.createSet(4);

	final Set<Method> constrainedParameterizedMethods = CollectionFactory.INSTANCE.createSet();

	final Map<String, ConstraintSet> constraintSetsByLocalId = CollectionFactory.INSTANCE
			.createMap();

	private final boolean isConstraintsEnforcementEnabled;

	/**
	 * package constructor used by the Validator class
	 * 
	 * @param clazz
	 * @param validator
	 */
	@SuppressWarnings("unchecked")
	ClassChecks(final Class clazz)
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Initializing constraints configuration for class " + clazz);

		this.clazz = clazz;
		isConstraintsEnforcementEnabled = ConstraintsEnforcementIsEnabled.class
				.isAssignableFrom(clazz);

		constrainedAnnotation = (Constrained) clazz.getAnnotation(Constrained.class);

		if (constrainedAnnotation == null)
		{
			if (LOG.isLoggable(Level.FINE))
			{
				LOG.log(Level.FINE, clazz.getName() + ": @" + Constrained.class.getName()
						+ " annotation not present.");
			}
		}
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	synchronized void addCheck(final Method method, final Check... checks)
			throws InvalidConfigurationException
	{
		// ensure the method has a return type
		if (method.getReturnType() == Void.TYPE)
		{
			throw new InvalidConfigurationException(
					"Adding return value constraints for method "
							+ method
							+ " is not possible. The method is declared as void and does not return any values.");
		}

		final boolean isGetter = ReflectionUtils.isGetter(method);
		if (!isGetter && !isConstraintsEnforcementEnabled)
		{
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for method "
							+ method
							+ " not following the JavaBean Getter method convention. Constraints enforcement is not activated for this class.");
		}

		constrainedMethods.add(method);
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

		if (isGetter)
		{
			if (!checksByGetter.containsKey(method))
			{
				constrainedGetters.add(method);
				// we are pointing to the same set as used in the checksByMethod map 
				checksByGetter.put(method, methodChecks);
			}
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
	synchronized void addCheck(final Method method, final int parameterIndex, final Check... checks)
			throws InvalidConfigurationException
	{
		if (!isConstraintsEnforcementEnabled)
			throw new InvalidConfigurationException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ". Constraints enforcement is not activated for this class.");

		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfMethodByParameter = checksByMethodParameter.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null)
		{
			checksOfMethodParameter = CollectionFactory.INSTANCE.createSet(8);
			checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
		}

		for (final Check check : checks)
			checksOfMethodParameter.add(check);
	}

	/**
	 * adds constraint checks to a constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	synchronized void addChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (!isConstraintsEnforcementEnabled)
			throw new InvalidConfigurationException(
					"Cannot apply constructor parameter constraints to class " + clazz.getName()
							+ ". Constraints enforcement is not activated for this class.");

		// retrieve the currently registered checks for all parameters of the specified constructor
		Map<Integer, Set<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfConstructorParameter = checksOfConstructorByParameter
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
	synchronized void addChecks(final Field field, final Check... checks)
	{
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

	synchronized void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check)
	{
		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		final Set<Check> checksOfConstructorParameter = checksOfConstructorByParameter
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

	synchronized void removeCheck(final Field field, final Check check)
	{
		final Set<Check> checksOfField = checksByField.get(field);

		if (checksOfField == null) return;

		checksOfField.remove(check);
		if (checksOfField.size() == 0)
		{
			checksByField.remove(field);
			constrainedFields.remove(field);
		}
	}

	synchronized void removeCheck(final Method method, final int parameterIndex, final Check check)
	{
		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfMethodByParameter = checksByMethodParameter.get(method);
		if (checksOfMethodByParameter == null) return;

		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfMethodParameter.remove(check);

		if (checksOfMethodParameter.size() == 0)
		{
			checksOfMethodByParameter.remove(parameterIndex);
			if (checksOfMethodByParameter.size() == 0)
				constrainedParameterizedMethods.remove(method);
		}
	}
}
