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
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.configuration.AnnotationsConfigurer;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.constraints.AssertValidCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.ConstraintSetAlreadyDefinedException;
import net.sf.oval.exceptions.FieldNotFoundException;
import net.sf.oval.exceptions.InvalidConfigurationException;
import net.sf.oval.exceptions.MethodNotFoundException;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.exceptions.UndefinedConstraintSetException;
import net.sf.oval.utils.ListOrderedSet;
import net.sf.oval.utils.ReflectionUtils;
import net.sf.oval.utils.ThreadLocalList;

/**
 * @author Sebastian Thomschke
 */
public class Validator
{
	protected final Map<Class, ClassChecks> checksByClass = new WeakHashMap<Class, ClassChecks>();
	protected final ListOrderedSet<Configurer> configurers = new ListOrderedSet<Configurer>();

	protected final Map<String, ConstraintSet> constraintSetsById = CollectionFactory.INSTANCE
			.createMap();

	protected final ThreadLocalList<Object> currentlyValidatedObjects = new ThreadLocalList<Object>();

	protected MessageResolver messageResolver = new MessageResolverImpl();

	protected ParameterNameResolver parameterNameResolver = new ParameterNameResolverEnumerationImpl();

	/**
	 * Constructs a new validator object and uses a new isntance of
	 * AnnotationsConfigurer
	 */
	public Validator()
	{
		configurers.add(new AnnotationsConfigurer());
	}

	public Validator(final Configurer... configurers)
	{
		for (final Configurer configurer : configurers)
			this.configurers.add(configurer);
	}

	protected void addChecks(final ClassConfiguration classConfig)
			throws InvalidConfigurationException, FieldNotFoundException, MethodNotFoundException,
			ReflectionException
	{
		if (classConfig.type == null)
			throw new InvalidConfigurationException("The property 'type' for " + classConfig
					+ " must be specified.");

		final ClassChecks cc = getClassChecks(classConfig.type);

		if (classConfig.overwrite != null && classConfig.overwrite)
		{
			cc.reset();
		}

		try
		{
			/*
			 * apply field checks
			 */
			if (classConfig.fieldConfigurations != null)
				for (final FieldConfiguration fieldConfig : classConfig.fieldConfigurations)
				{
					final Field field = classConfig.type.getDeclaredField(fieldConfig.name);

					if (fieldConfig.overwrite != null && fieldConfig.overwrite)
					{
						cc.removeAllCheck(field);
					}

					if (fieldConfig.checks != null && fieldConfig.checks.size() > 0)
					{
						cc.addChecks(field, fieldConfig.checks.toArray(new Check[fieldConfig.checks
								.size()]));
					}

					if (fieldConfig.defineConstraintSet != null)
					{
						final ConstraintSet cs = cc.addFieldConstraintSet(field,
								fieldConfig.defineConstraintSet);

						// add the constraint set to the global map
						addConstraintSet(cs);
					}
				}

			/*
			 * apply constructor parameter checks
			 */
			if (classConfig.constructorConfigurations != null)
				for (final ConstructorConfiguration constructorConfig : classConfig.constructorConfigurations)
				{
					if (constructorConfig.parameterConfigurations != null)
					{
						final Class< ? >[] parameterTypes = new Class[constructorConfig.parameterConfigurations
								.size()];

						for (int i = 0, l = constructorConfig.parameterConfigurations.size(); i < l; i++)
						{
							parameterTypes[i] = constructorConfig.parameterConfigurations.get(i).type;
						}

						final Constructor constructor = classConfig.type
								.getDeclaredConstructor(parameterTypes);

						if (constructorConfig.overwrite != null && constructorConfig.overwrite)
						{
							cc.removeAllChecks(constructor);
						}

						for (int i = 0, l = constructorConfig.parameterConfigurations.size(); i < l; i++)
						{
							final ParameterConfiguration parameterConfig = constructorConfig.parameterConfigurations
									.get(i);

							if (parameterConfig.overwrite != null && parameterConfig.overwrite)
							{
								cc.removeAllChecks(constructor, i);
							}

							final List<Check> checks = parameterConfig.checks;

							if (checks != null && checks.size() > 0)
							{
								cc.addChecks(constructor, i, checks
										.toArray(new Check[checks.size()]));
							}
						}
					}
				}

			/*
			 * apply method parameter and return value checks
			 */
			if (classConfig.methodConfigurations != null)
				for (final MethodConfiguration methodConfig : classConfig.methodConfigurations)
				{
					Method method = null;
					if (methodConfig.parameterConfigurations == null
							|| methodConfig.parameterConfigurations.size() == 0)
					{
						method = classConfig.type.getDeclaredMethod(methodConfig.name);

						if (methodConfig.overwrite != null && methodConfig.overwrite)
						{
							cc.removeAllChecks(method);
						}
					}
					else
					{
						final Class< ? >[] parameterTypes = new Class[methodConfig.parameterConfigurations
								.size()];

						for (int i = 0, l = methodConfig.parameterConfigurations.size(); i < l; i++)
						{
							parameterTypes[i] = methodConfig.parameterConfigurations.get(i).type;
						}

						method = classConfig.type.getDeclaredMethod(methodConfig.name,
								parameterTypes);

						if (methodConfig.overwrite != null && methodConfig.overwrite)
						{
							cc.removeAllChecks(method);
						}

						for (int i = 0, l = methodConfig.parameterConfigurations.size(); i < l; i++)
						{
							final ParameterConfiguration parameterConfig = methodConfig.parameterConfigurations
									.get(i);

							if (parameterConfig.overwrite != null && parameterConfig.overwrite)
							{
								cc.removeAllChecks(method, i);
							}

							final List<Check> checks = parameterConfig.checks;

							if (checks != null && checks.size() > 0)
							{
								cc.addChecks(method, i, checks.toArray(new Check[checks.size()]));
							}
						}
					}

					if (methodConfig.returnValueChecks != null
							&& methodConfig.returnValueChecks.size() > 0)
					{
						cc.addChecks(method, methodConfig.returnValueChecks
								.toArray(new Check[methodConfig.returnValueChecks.size()]));
					}
				}
		}
		catch (SecurityException e)
		{
			throw new ReflectionException("SecurityException occured", e);
		}
		catch (NoSuchMethodException e)
		{
			throw new MethodNotFoundException("NoSuchMethodException occured.", e);
		}
		catch (NoSuchFieldException e)
		{
			throw new FieldNotFoundException("FieldNotFoundException occured.", e);
		}
	}

	protected void addConstraintSet(final ConstraintSet constraintSet)
			throws ConstraintSetAlreadyDefinedException
	{
		if (constraintSetsById.containsKey(constraintSet.id))
			throw new ConstraintSetAlreadyDefinedException(
					"Another constraint set with the same fully qualified id " + constraintSet.id
							+ " has already been defined.");

		constraintSetsById.put(constraintSet.id, constraintSet);
	}

	public ConstraintSet addConstraintSet(
			final ConstraintSetConfiguration constraintSetConfigurations)
			throws ConstraintSetAlreadyDefinedException
	{
		final ConstraintSet cs = new ConstraintSet();
		cs.checks = CollectionFactory.INSTANCE.createSet(constraintSetConfigurations.checks == null
				? 2 : constraintSetConfigurations.checks.size());
		cs.checks.addAll(constraintSetConfigurations.checks);
		cs.id = constraintSetConfigurations.id;

		if (constraintSetConfigurations.overwrite != null && constraintSetConfigurations.overwrite)
			constraintSetsById.put(cs.id, cs);
		else
			addConstraintSet(cs);

		return cs;
	}

	protected void checkConstraint(final List<ConstraintViolation> violations, final Check check,
			final Object validatedObject, final Object valueToValidate, final OValContext context)
	{
		/*
		 * special handling of the AssertValid constraint
		 */
		if (check instanceof AssertValidCheck)
		{
			if (valueToValidate == null) return;

			// ignore circular dependencies
			if (isCurrentlyValidated(valueToValidate)) return;

			if (validate(valueToValidate).size() != 0)
			{
				final String errorMessage = renderMessage(context, valueToValidate, check);
				violations.add(new ConstraintViolation(errorMessage, validatedObject,
						valueToValidate, context));
			}
			return;
		}

		/*
		 * special handling of the FieldConstraints constraint
		 */
		if (check instanceof AssertConstraintSetCheck)
		{
			final AssertConstraintSetCheck assertConstraintSetCheck = (AssertConstraintSetCheck) check;
			Class targetClass = assertConstraintSetCheck.getSource();
			if (targetClass == Object.class)
			{
				if (context instanceof ConstructorParameterContext)
				{
					targetClass = ((ConstructorParameterContext) context).getConstructor()
							.getDeclaringClass();
				}
				else if (context instanceof FieldContext)
				{
					targetClass = ((FieldContext) context).getField().getDeclaringClass();
				}
				else if (context instanceof MethodParameterContext)
				{
					targetClass = ((MethodParameterContext) context).getMethod()
							.getDeclaringClass();
				}
				else if (context instanceof MethodReturnValueContext)
				{
					targetClass = ((MethodReturnValueContext) context).getMethod()
							.getDeclaringClass();
				}
			}

			final Class source = assertConstraintSetCheck.getSource();
			final String constraintSetId = assertConstraintSetCheck.getId();

			Collection<Check> referencedChecks = null;
			if (source == Object.class)
			{
				final ClassChecks cc = getClassChecks(targetClass);
				final ConstraintSet cs = cc.constraintSetsByLocalId.get(assertConstraintSetCheck
						.getId());
				if (cs != null) referencedChecks = cs.getChecks(this);
			}
			else
			{
				final ClassChecks cc = getClassChecks(source);
				final ConstraintSet cs = cc.constraintSetsByLocalId.get(assertConstraintSetCheck
						.getId());
				if (cs != null) referencedChecks = cs.getChecks(this);
			}
			if (referencedChecks == null)
			{
				final ConstraintSet cs = getConstraintSet(constraintSetId);
				if (cs != null) referencedChecks = cs.getChecks(this);
			}
			if (referencedChecks == null)
			{
				throw new UndefinedConstraintSetException("No constraint set with id "
						+ constraintSetId + " defined.");
			}

			for (final Check referencedCheck : referencedChecks)
			{
				checkConstraint(violations, referencedCheck, validatedObject, valueToValidate,
						context);
			}
			return;
		}

		/*
		 * special handling of the FieldConstraints constraint
		 */
		if (check instanceof AssertFieldConstraintsCheck)
		{
			// the lowest class that is expected to declare the field (or one of its super classes)
			Class targetClass = validatedObject.getClass();

			/*
			 * adjust the targetClass based on the validation context
			 */
			if (context instanceof ConstructorParameterContext)
			{
				// the class declaring the field must either be the class declaring the constructor or one of its super classes
				targetClass = ((ConstructorParameterContext) context).getConstructor()
						.getDeclaringClass();
			}
			else if (context instanceof MethodParameterContext)
			{
				// the class declaring the field must either be the class declaring the method or one of its super classes
				targetClass = ((MethodParameterContext) context).getMethod().getDeclaringClass();
			}
			else if (context instanceof MethodReturnValueContext)
			{
				// the class declaring the field must either be the class declaring the getter or one of its super classes
				targetClass = ((MethodReturnValueContext) context).getMethod().getDeclaringClass();
			}

			// the name of the field whose constraints shall be used
			String fieldName = ((AssertFieldConstraintsCheck) check).getFieldName();

			/*
			 * calculate the field name based on the validation context if the @FieldConstraints constraint didn't specify the field name
			 */
			if (fieldName == null || fieldName.length() == 0)
			{
				if (context instanceof ConstructorParameterContext)
				{
					fieldName = ((ConstructorParameterContext) context).getParameterName();
				}
				else if (context instanceof MethodParameterContext)
				{
					fieldName = ((MethodParameterContext) context).getParameterName();
				}
				else if (context instanceof MethodReturnValueContext)
				{
					/*
					 * calculate the fieldName based on the getXXX isXXX style getter method name
					 */
					fieldName = ((MethodReturnValueContext) context).getMethod().getName();

					if (fieldName.startsWith("get") && fieldName.length() > 3)
					{
						fieldName = fieldName.substring(3);
						if (fieldName.length() == 1)
							fieldName = fieldName.toLowerCase();
						else
							fieldName = Character.toLowerCase(fieldName.charAt(0))
									+ fieldName.substring(1);
					}
					else if (fieldName.startsWith("is") && fieldName.length() > 2)
					{
						fieldName = fieldName.substring(2);
						if (fieldName.length() == 1)
							fieldName = fieldName.toLowerCase();
						else
							fieldName = Character.toLowerCase(fieldName.charAt(0))
									+ fieldName.substring(1);
					}
				}
			}

			/*
			 * find the field based on fieldName and targetClass
			 */
			final Field field = ReflectionUtils.getFieldRecursive(targetClass, fieldName);

			if (field == null)
			{
				throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <"
						+ targetClass + "> or its super classes.");
			}

			final ClassChecks cc = getClassChecks(field.getDeclaringClass());
			final Collection<Check> referencedChecks = cc.checksByField.get(field);
			if (referencedChecks != null && referencedChecks.size() > 0)
			{
				for (final Check referencedCheck : referencedChecks)
				{
					checkConstraint(violations, referencedCheck, validatedObject, valueToValidate,
							context);
				}
			}
		}

		/*
		 * standard constraints handling
		 */
		if (!check.isSatisfied(validatedObject, valueToValidate, context))
		{
			final String errorMessage = renderMessage(context, valueToValidate, check);
			violations.add(new ConstraintViolation(errorMessage, validatedObject, valueToValidate,
					context));
		}
	}

	public ClassChecks getClassChecks(final Class clazz)
	{
		synchronized (checksByClass)
		{
			ClassChecks cc = checksByClass.get(clazz);
			if (cc == null)
			{
				cc = new ClassChecks(clazz);
				checksByClass.put(clazz, cc);

				for (final Configurer configurer : configurers)
				{
					final ClassConfiguration classConfig = configurer.getClassConfiguration(clazz);
					addChecks(classConfig);
				}
			}
			return cc;
		}
	}

	/**
	 * @return the internal list with the registered configurers
	 */
	public List<Configurer> getConfigurers()
	{
		return configurers;
	}

	protected ConstraintSet getConstraintSet(final String constraintSetId)
	{
		ConstraintSet cs = constraintSetsById.get(constraintSetId);

		if (cs == null)
		{
			for (final Configurer configurer : configurers)
			{
				final ConstraintSetConfiguration csc = configurer
						.getConstraintSetConfiguration(constraintSetId);
				if (csc != null) cs = addConstraintSet(csc);
			}
		}
		return cs;
	}

	/**
	 * @return the messageResolver
	 */
	public MessageResolver getMessageResolver()
	{
		return messageResolver;
	}

	/**
	 * @return the parameterNameResolver
	 */
	public ParameterNameResolver getParameterNameResolver()
	{
		return parameterNameResolver;
	}

	/**
	 * Determines if the given object is currently validated in the current thread
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isCurrentlyValidated(final Object object)
	{
		return currentlyValidatedObjects.get().contains(object);
	}

	/**
	 * Removes the constraint set with the given id
	 * @param id
	 * @return the removed constraint set
	 */
	public ConstraintSet removeConstraintSet(final String id)
	{
		return constraintSetsById.remove(id);
	}

	protected String renderMessage(final OValContext context, final Object value, final Check check)
	{
		final String messageKey = check.getMessage();

		String message = messageResolver.getMessage(messageKey);
		if (message == null) message = messageKey;

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		final String[] messageValues = check.getMessageValues();
		final int messageValuesCount = messageValues == null ? 0 : messageValues.length;
		final Object[] args = new Object[2 + messageValuesCount];
		args[0] = context;
		args[1] = value;
		if (messageValuesCount > 0)
		{
			System.arraycopy(messageValues, 0, args, 2, messageValuesCount);
		}
		return MessageFormat.format(message, args);
	}

	/**
	 * clears the checks and constraint sets => reconfiguration using the
	 * registered configurers takes place
	 */
	public void reset()
	{
		checksByClass.clear();
		constraintSetsById.clear();
	}

	/**
	 * @param messageResolver the messageResolver to set
	 */
	public void setMessageResolver(final MessageResolver messageResolver)
	{
		this.messageResolver = messageResolver;
	}

	/**
	 * @param parameterNameResolver the parameterNameResolver to set
	 */
	public void setParameterNameResolver(final ParameterNameResolver parameterNameResolver)
	{
		this.parameterNameResolver = parameterNameResolver;
	}

	/**
	 * validates the field and getter constrains of the given object
	 * 
	 * @param validatedObject
	 * @return  a list with the detected constraint violations. if no violations are detected an empty list is returned
	 */
	public List<ConstraintViolation> validate(final Object validatedObject)
	{
		currentlyValidatedObjects.get().add(validatedObject);
		try
		{
			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();
			validateObject(validatedObject, validatedObject.getClass(), violations);
			return violations;
		}
		finally
		{
			currentlyValidatedObjects.get().remove(validatedObject);
		}
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	List<ConstraintViolation> validateConstructorParameters(final Object validatedObject,
			final Constructor constructor, final Object[] args)
	{
		final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
		final Map<Integer, Collection<Check>> parameterChecks = cc.checksByConstructorParameter
				.get(constructor);

		if (parameterChecks == null) return null;

		final String[] parameterNames = parameterNameResolver.getParameterNames(constructor);
		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();
		for (int i = 0; i < args.length; i++)
		{
			final Collection<Check> checks = parameterChecks.get(i);

			if (checks != null && checks.size() > 0)
			{
				final Object valueToValidate = args[i];
				final ConstructorParameterContext context = new ConstructorParameterContext(
						constructor, i, parameterNames[i]);

				for (final Check check : checks)
				{
					checkConstraint(violations, check, validatedObject, valueToValidate, context);
				}
			}
		}
		return violations.size() == 0 ? null : violations;
	}

	protected void validateField(final Object validatedObject, final Field field,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(field.getDeclaringClass());
		final Collection<Check> checks = cc.checksByField.get(field);

		if (checks != null && checks.size() > 0)
		{
			final Object valueToValidate = ReflectionUtils.getFieldValue(field, validatedObject);
			final FieldContext context = new FieldContext(field);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, valueToValidate, context);
			}
		}
	}

	protected void validateGetter(final Object validatedObject, final Method getter,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(getter.getDeclaringClass());
		final Collection<Check> checks = cc.checksByMethod.get(getter);

		if (checks != null && checks.size() > 0)
		{
			final Object valueToValidate = ReflectionUtils.invokeMethod(getter, validatedObject);
			final MethodReturnValueContext context = new MethodReturnValueContext(getter);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, valueToValidate, context);
			}
		}
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	List<ConstraintViolation> validateMethodParameters(final Object validatedObject,
			final Method method, final Object[] args)
	{
		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		final Map<Integer, Collection<Check>> parameterChecks = cc.checksByMethodParameter
				.get(method);

		// check if the method has any parameter checks at all
		if (parameterChecks == null) return null;

		final String[] parameterNames = parameterNameResolver.getParameterNames(method);

		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();

		for (int i = 0; i < args.length; i++)
		{
			final Collection<Check> checks = parameterChecks.get(i);

			if (checks != null && checks.size() > 0)
			{
				final Object valueToValidate = args[i];
				final MethodParameterContext context = new MethodParameterContext(method, i,
						parameterNames[i]);

				for (final Check check : checks)
				{
					checkConstraint(violations, check, validatedObject, valueToValidate, context);
				}
			}
		}
		return violations.size() == 0 ? null : violations;
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	List<ConstraintViolation> validateMethodReturnValue(final Object validatedObject,
			final Method method, final Object methodReturnValue)
	{
		final ClassChecks cc = getClassChecks(method.getDeclaringClass());
		final Collection<Check> checks = cc.checksByMethod.get(method);

		if (checks != null && checks.size() > 0)
		{
			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList(8);

			final MethodReturnValueContext context = new MethodReturnValueContext(method);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, methodReturnValue, context);
			}
			return violations.size() == 0 ? null : violations;
		}
		return null;
	}

	/**
	 * validate validatedObject based on the constraints of the given clazz
	 */
	protected void validateObject(final Object validatedObject, final Class< ? > clazz,
			final List<ConstraintViolation> violations)
	{
		// abort if the root class has been reached
		if (clazz == Object.class) return;

		final ClassChecks cc = getClassChecks(clazz);

		// validate field constraints
		for (final Field field : cc.constrainedFields)
		{
			validateField(validatedObject, field, violations);
		}

		// validate constraints on getter methods
		for (final Method getter : cc.constrainedGetters)
		{
			validateGetter(validatedObject, getter, violations);
		}

		// if the super class is annotated to be validatable also validate it against the object
		validateObject(validatedObject, clazz.getSuperclass(), violations);
	}
}
