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
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.guard.IsGuarded;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * This class holds the instantiated checks for a single class.
 * 
 * <b>Note:</b> For performance reasons the collections are made public (intended for read-access only).
 * Modifications to the collections should be done through the appropriate methods addXXX, removeXXX, clearXXX methods.
 * 
 * @author Sebastian Thomschke
 */
public class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * object invariants
	 */
	public final Set<Check> checksForObject = CollectionFactoryHolder.getFactory().createSet(2);

	/**
	 * checks on constructors' parameter values
	 */
	public final Map<Constructor, Map<Integer, Set<Check>>> checksForConstructorParameters = CollectionFactoryHolder
			.getFactory().createMap(2);

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

	public final Set<AccessibleObject> methodsWithCheckInvariantsPost = CollectionFactoryHolder
			.getFactory().createSet();

	/**
	 * checks on methods' parameter values
	 */
	public final Map<Method, Map<Integer, Set<Check>>> checksForMethodParameters = CollectionFactoryHolder
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
		if (ClassChecks.LOG.isLoggable(Level.FINE))
			ClassChecks.LOG.fine("Initializing constraints configuration for class " + clazz);

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
	public void addConstructorParameterChecks(final Constructor constructor,
			final int parameterIndex, final Check... checks) throws InvalidConfigurationException
	{
		addConstructorParameterChecks(constructor, parameterIndex, (Object) checks);
	}

	/**
	 * adds constraint checks to a constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect 
	 */
	public void addConstructorParameterChecks(final Constructor constructor,
			final int parameterIndex, final Collection<Check> checks)
			throws InvalidConfigurationException
	{
		addConstructorParameterChecks(constructor, parameterIndex, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addConstructorParameterChecks(final Constructor constructor,
			final int parameterIndex, final Object checks) throws InvalidConfigurationException
	{
		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply constructor parameter constraints to constructor " + constructor
							+ ". Constraints guarding is not activated for this class.");

		final int paramCount = constructor.getParameterTypes().length;

		if (parameterIndex < 0 || parameterIndex >= paramCount)
			throw new InvalidConfigurationException("ParameterIndex " + parameterIndex
					+ " is out of range (0-" + (paramCount - 1) + ")");

		synchronized (checksForConstructorParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified constructor
			Map<Integer, Set<Check>> checksOfConstructorByParameter = checksForConstructorParameters
					.get(constructor);
			if (checksOfConstructorByParameter == null)
			{
				checksOfConstructorByParameter = CollectionFactoryHolder.getFactory().createMap(
						paramCount);
				checksForConstructorParameters.put(constructor, checksOfConstructorByParameter);
			}

			// retrieve the checks for the specified parameter
			Set<Check> checksOfConstructorParameter = checksOfConstructorByParameter
					.get(parameterIndex);
			if (checksOfConstructorParameter == null)
			{
				checksOfConstructorParameter = CollectionFactoryHolder.getFactory().createSet(2);
				checksOfConstructorByParameter.put(parameterIndex, checksOfConstructorParameter);
			}

			if (checks instanceof Collection)
			{
				checksOfConstructorParameter.addAll((Collection<Check>) checks);
			}
			else
			{
				ArrayUtils.addAll(checksOfConstructorParameter, (Check[]) checks);
			}
		}
	}

	/**
	 * adds check constraints to a field 
	 *  
	 * @param field
	 * @param checks 
	 */
	public void addFieldChecks(final Field field, final Check... checks)
			throws InvalidConfigurationException
	{
		addFieldChecks(field, (Object) checks);
	}

	/**
	 * adds check constraints to a field 
	 *  
	 * @param field
	 * @param checks 
	 */
	public void addFieldChecks(final Field field, final Collection<Check> checks)
			throws InvalidConfigurationException
	{
		addFieldChecks(field, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addFieldChecks(final Field field, final Object checks)
	{
		synchronized (checksForFields)
		{
			Set<Check> checksOfField = checksForFields.get(field);
			if (checksOfField == null)
			{
				checksOfField = CollectionFactoryHolder.getFactory().createSet(2);
				checksForFields.put(field, checksOfField);
				if (ReflectionUtils.isStatic(field))
					constrainedStaticFields.add(field);
				else
					constrainedFields.add(field);
			}

			if (checks instanceof Collection)
			{
				checksOfField.addAll((Collection<Check>) checks);
			}
			else
			{
				ArrayUtils.addAll(checksOfField, (Check[]) checks);
			}
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
	public void addMethodParameterChecks(final Method method, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		addMethodParameterChecks(method, parameterIndex, (Object) checks);
	}

	/**
	 * adds constraint checks to a method parameter 
	 *  
	 * @param method
	 * @param parameterIndex
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public void addMethodParameterChecks(final Method method, final int parameterIndex,
			final Collection<Check> checks) throws InvalidConfigurationException
	{
		addMethodParameterChecks(method, parameterIndex, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addMethodParameterChecks(final Method method, final int parameterIndex,
			final Object checks) throws InvalidConfigurationException
	{
		if (!isGuarded)
			throw new InvalidConfigurationException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ". Constraints guarding is not activated for this class.");

		final int paramCount = method.getParameterTypes().length;

		if (parameterIndex < 0 || parameterIndex >= paramCount)
			throw new InvalidConfigurationException("ParameterIndex " + parameterIndex
					+ " is out of range (0-" + (paramCount - 1) + ")");

		synchronized (checksForMethodParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified method
			Map<Integer, Set<Check>> checksOfMethodByParameter = checksForMethodParameters
					.get(method);
			if (checksOfMethodByParameter == null)
			{
				checksOfMethodByParameter = CollectionFactoryHolder.getFactory().createMap(
						paramCount);
				checksForMethodParameters.put(method, checksOfMethodByParameter);
			}

			// retrieve the checks for the specified parameter
			Set<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
			if (checksOfMethodParameter == null)
			{
				checksOfMethodParameter = CollectionFactoryHolder.getFactory().createSet(2);
				checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
			}

			if (checks instanceof Collection)
			{
				checksOfMethodParameter.addAll((Collection<Check>) checks);
			}
			else
			{
				ArrayUtils.addAll(checksOfMethodParameter, (Check[]) checks);
			}
		}
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public void addMethodPostChecks(final Method method, final Collection<PostCheck> checks)
			throws InvalidConfigurationException
	{
		addMethodPostChecks(method, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addMethodPostChecks(final Method method, final Object checks)
			throws InvalidConfigurationException
	{
		if (!isGuarded)
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");

		synchronized (checksForMethodsPostExcecution)
		{
			Set<PostCheck> postChecks = checksForMethodsPostExcecution.get(method);
			if (postChecks == null)
			{
				postChecks = CollectionFactoryHolder.getFactory().createSet(2);
				checksForMethodsPostExcecution.put(method, postChecks);
			}

			if (checks instanceof Collection)
			{
				postChecks.addAll((Collection<PostCheck>) checks);
			}
			else
			{
				ArrayUtils.addAll(postChecks, (PostCheck[]) checks);
			}
		}
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public void addMethodPostChecks(final Method method, final PostCheck... checks)
			throws InvalidConfigurationException
	{
		addMethodPostChecks(method, (Object) checks);
	}

	/**
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public void addMethodPreChecks(final Method method, final Collection<PreCheck> checks)
			throws InvalidConfigurationException
	{
		addMethodPreChecks(method, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addMethodPreChecks(final Method method, final Object checks)
			throws InvalidConfigurationException
	{
		if (!isGuarded)
			throw new InvalidConfigurationException("Cannot apply pre condition for method "
					+ method + ". Constraints guarding is not activated for this class.");

		synchronized (checksForMethodsPreExecution)
		{
			Set<PreCheck> preChecks = checksForMethodsPreExecution.get(method);
			if (preChecks == null)
			{
				preChecks = CollectionFactoryHolder.getFactory().createSet(2);
				checksForMethodsPreExecution.put(method, preChecks);
			}

			if (checks instanceof Collection)
			{
				preChecks.addAll((Collection<PreCheck>) checks);
			}
			else
			{
				ArrayUtils.addAll(preChecks, (PreCheck[]) checks);
			}
		}
	}

	/**
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException if the declaring class is not guarded by GuardAspect
	 */
	public void addMethodPreChecks(final Method method, final PreCheck... checks)
			throws InvalidConfigurationException
	{
		addMethodPreChecks(method, (Object) checks);
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param isInvariant determines if the return value should be checked when the object is validated, can be null
	 * @param checks
	 */
	public void addMethodReturnValueChecks(final Method method, final Boolean isInvariant,
			final Check... checks) throws InvalidConfigurationException
	{
		addMethodReturnValueChecks(method, isInvariant, (Object) checks);
	}

	/**
	 * adds constraint checks to a method's return value
	 * @param method
	 * @param isInvariant determines if the return value should be checked when the object is validated, can be null
	 * @param checks
	 */
	public void addMethodReturnValueChecks(final Method method, final Boolean isInvariant,
			final Collection<Check> checks) throws InvalidConfigurationException
	{
		addMethodReturnValueChecks(method, isInvariant, (Object) checks);
	}

	@SuppressWarnings("unchecked")
	private void addMethodReturnValueChecks(final Method method, final Boolean isInvariant,
			final Object checks) throws InvalidConfigurationException
	{
		// ensure the method has a return type
		if (method.getReturnType() == Void.TYPE)
			throw new InvalidConfigurationException(
					"Adding return value constraints for method "
							+ method
							+ " is not possible. The method is declared as void and does not return any values.");

		if (ReflectionUtils.isVoidMethod(method))
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for void method " + method);

		final boolean hasParameters = method.getParameterTypes().length > 0;

		if (!isGuarded && hasParameters)
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for parameterized method "
							+ method + ". Constraints guarding is not activated for this class.");

		final boolean isInvariant2 = isInvariant == null ? constrainedMethods.contains(method)
				: isInvariant;

		if (!isGuarded && !isInvariant2)
			throw new InvalidConfigurationException(
					"Cannot apply method return value constraints for method "
							+ method
							+ ". The method needs to be marked as being invariant (@IsInvariant) since constraints guarding is not activated for this class.");

		synchronized (checksForMethodReturnValues)
		{
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
				methodChecks = CollectionFactoryHolder.getFactory().createSet(2);
				checksForMethodReturnValues.put(method, methodChecks);
			}

			if (checks instanceof Collection)
			{
				methodChecks.addAll((Collection<Check>) checks);
			}
			else
			{
				ArrayUtils.addAll(methodChecks, (Check[]) checks);
			}
		}
	}

	/**
	 * adds check constraints on object level (invariants) 
	 *  
	 * @param checks 
	 */
	public void addObjectChecks(final Check... checks)
	{
		synchronized (checksForObject)
		{
			ArrayUtils.addAll(checksForObject, checks);
		}
	}

	/**
	 * adds check constraints on object level (invariants) 
	 *  
	 * @param checks 
	 */
	public void addObjectChecks(final Collection<Check> checks)
	{
		synchronized (checksForObject)
		{
			checksForObject.addAll(checks);
		}
	}

	public synchronized void clear()
	{
		if (ClassChecks.LOG.isLoggable(Level.FINE))
			ClassChecks.LOG.fine("Clearing all checks for class " + clazz.getName());

		checksForObject.clear();
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

	public void clearConstructorChecks(final Constructor constructor)
	{
		clearConstructorParameterChecks(constructor);
	}

	public void clearConstructorParameterChecks(final Constructor constructor)
	{
		synchronized (checksForConstructorParameters)
		{
			checksForConstructorParameters.remove(constructor);
		}
	}

	public void clearConstructorParameterChecks(final Constructor constructor,
			final int parameterIndex)
	{
		synchronized (checksForConstructorParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified method
			final Map<Integer, Set<Check>> checksOfConstructorByParameter = checksForConstructorParameters
					.get(constructor);
			if (checksOfConstructorByParameter == null) return;

			// retrieve the checks for the specified parameter
			final Collection<Check> checksOfMethodParameter = checksOfConstructorByParameter
					.get(parameterIndex);
			if (checksOfMethodParameter == null) return;

			checksOfConstructorByParameter.remove(parameterIndex);
		}
	}

	public void clearFieldChecks(final Field field)
	{
		synchronized (checksForFields)
		{
			checksForFields.remove(field);
			constrainedFields.remove(field);
			constrainedStaticFields.remove(field);
		}
	}

	public synchronized void clearMethodChecks(final Method method)
	{
		clearMethodParameterChecks(method);
		clearMethodReturnValueChecks(method);
		clearMethodPreChecks(method);
		clearMethodPostChecks(method);
	}

	public void clearMethodParameterChecks(final Method method)
	{
		synchronized (checksForMethodParameters)
		{
			checksForMethodParameters.remove(method);
		}
	}

	public void clearMethodParameterChecks(final Method method, final int parameterIndex)
	{
		synchronized (checksForMethodParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified method
			final Map<Integer, Set<Check>> checksOfMethodByParameter = checksForMethodParameters
					.get(method);
			if (checksOfMethodByParameter == null) return;

			// retrieve the checks for the specified parameter
			final Collection<Check> checksOfMethodParameter = checksOfMethodByParameter
					.get(parameterIndex);
			if (checksOfMethodParameter == null) return;

			checksOfMethodByParameter.remove(parameterIndex);
		}
	}

	public void clearMethodPostChecks(final Method method)
	{
		synchronized (checksForMethodsPostExcecution)
		{
			checksForMethodsPostExcecution.remove(method);
		}
	}

	public void clearMethodPreChecks(final Method method)
	{
		synchronized (checksForMethodsPreExecution)
		{
			checksForMethodsPreExecution.remove(method);
		}
	}

	public void clearMethodReturnValueChecks(final Method method)
	{
		synchronized (checksForMethodReturnValues)
		{
			checksForMethodReturnValues.remove(method);
			constrainedMethods.remove(method);
			constrainedStaticMethods.remove(method);
		}
	}

	public void clearObjectChecks()
	{
		synchronized (checksForObject)
		{
			checksForObject.clear();
		}
	}

	public void removeConstructorParameterChecks(final Constructor constructor,
			final int parameterIndex, final Check... checks)
	{
		synchronized (checksForConstructorParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified method
			final Map<Integer, Set<Check>> checksOfConstructorByParameter = checksForConstructorParameters
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
	}

	public void removeFieldChecks(final Field field, final Check... checks)
	{
		synchronized (checksForFields)
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
	}

	public void removeMethodChecks(final Method method, final Check... checks)
	{
		synchronized (checksForMethodReturnValues)
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
	}

	public void removeMethodParameterChecks(final Method method, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		if (parameterIndex < 0 || parameterIndex > method.getParameterTypes().length)
			throw new InvalidConfigurationException("ParameterIndex is out of range");

		synchronized (checksForMethodParameters)
		{
			// retrieve the currently registered checks for all parameters of the specified method
			final Map<Integer, Set<Check>> checksOfMethodByParameter = checksForMethodParameters
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
	}

	public void removeMethodPostChecks(final Method method, final PostCheck... checks)
	{
		synchronized (checksForMethodsPostExcecution)
		{
			final Set<PostCheck> checksforMethod = checksForMethodsPostExcecution.get(method);

			if (checksforMethod == null) return;

			for (final PostCheck check : checks)
			{
				checksforMethod.remove(check);
			}

			if (checksforMethod.size() == 0)
			{
				checksForMethodsPostExcecution.remove(method);
			}
		}
	}

	public void removeMethodPreChecks(final Method method, final PreCheck... checks)
	{
		synchronized (checksForMethodsPreExecution)
		{
			final Set<PreCheck> checksforMethod = checksForMethodsPreExecution.get(method);

			if (checksforMethod == null) return;

			for (final PreCheck check : checks)
			{
				checksforMethod.remove(check);
			}

			if (checksforMethod.size() == 0)
			{
				checksForMethodsPreExecution.remove(method);
			}
		}
	}

	public void removeObjectChecks(final Check... checks)
	{
		synchronized (checksForObject)
		{
			for (final Check check : checks)
			{
				checksForObject.remove(check);
			}
		}
	}
}
