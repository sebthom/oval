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
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.configuration.AnnotationsConfigurer;
import net.sf.oval.configuration.ClassConfiguration;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.ConstraintSetConfiguration;
import net.sf.oval.configuration.ConstructorConfiguration;
import net.sf.oval.configuration.FieldConfiguration;
import net.sf.oval.configuration.MethodConfiguration;
import net.sf.oval.configuration.OValConfiguration;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.constraints.AssertValidCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.AccessingFieldValueFailedException;
import net.sf.oval.exceptions.ConstraintSetAlreadyDefinedException;
import net.sf.oval.exceptions.FieldNotFoundException;
import net.sf.oval.exceptions.InvalidConfigurationException;
import net.sf.oval.exceptions.InvokingGetterFailedException;
import net.sf.oval.exceptions.MethodNotFoundException;
import net.sf.oval.exceptions.OValException;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.exceptions.UndefinedConstraintSetException;
import net.sf.oval.utils.ThreadLocalList;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
public final class Validator
{
	private final Map<Class, ClassChecks> checksByClass = new WeakHashMap<Class, ClassChecks>();

	private final Configurer configurer;

	private final Map<String, ConstraintSet> constraintSetsById = CollectionFactory.INSTANCE
			.createMap();
	private final ThreadLocalList<Object> currentlyValidatedObjects = new ThreadLocalList<Object>();

	private final Map<ResourceBundle, List<String>> messageBundleKeys = CollectionFactory.INSTANCE
			.createMap(8);

	private final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();

	private ParameterNameResolver parameterNameResolver = new ParameterNameResolverEnumerationImpl();

	/**
	 * public constructor
	 */
	public Validator()
	{
		// add the message bundle for the pre-built constraints in the default locale
		addMessageBundle(ResourceBundle.getBundle("net/sf/oval/constraints/Messages"));

		this.configurer = new AnnotationsConfigurer();
	}

	public Validator(final Configurer configurer)
	{
		// add the message bundle for the pre-built constraints in the default locale
		addMessageBundle(ResourceBundle.getBundle("net/sf/oval/constraints/Messages"));

		this.configurer = configurer;
	}

	public void addChecks(final ClassConfiguration classConfig)
			throws InvalidConfigurationException, FieldNotFoundException, MethodNotFoundException,
			ReflectionException
	{
		if (classConfig.type == null)
			throw new InvalidConfigurationException("The property 'type' for " + classConfig
					+ " must be specified.");

		final ClassChecks classChecks = getClassChecks(classConfig.type);

		try
		{
			/*
			 * apply field checks
			 */
			if (classConfig.fieldsConfig != null)
				for (final FieldConfiguration fieldConfig : classConfig.fieldsConfig)
				{
					final Field field = classConfig.type.getDeclaredField(fieldConfig.name);

					if (fieldConfig.checks != null && fieldConfig.checks.size() > 0)
					{
						addChecks(field, fieldConfig.checks);
					}

					if (fieldConfig.defineConstraintSet != null)
					{
						final ConstraintSet cs = new ConstraintSet();
						cs.context = new FieldContext(field);
						cs.id = field.getDeclaringClass().getName() + "."
								+ fieldConfig.defineConstraintSet;

						classChecks.constraintSetsByLocalId
								.put(fieldConfig.defineConstraintSet, cs);

						// add the constraint set to the global map
						addConstraintSet(cs);
					}
				}

			/*
			 * apply constructor parameter checks
			 */
			if (classConfig.constructorsConfig != null)
				for (final ConstructorConfiguration constructorConfig : classConfig.constructorsConfig)
				{
					if (constructorConfig.parametersConfig != null)
					{
						final Class< ? >[] parameterTypes = new Class[constructorConfig.parametersConfig
								.size()];

						for (int i = 0, l = constructorConfig.parametersConfig.size(); i < l; i++)
						{
							parameterTypes[i] = constructorConfig.parametersConfig.get(i).type;
						}

						final Constructor constructor = classConfig.type
								.getDeclaredConstructor(parameterTypes);

						for (int i = 0, l = constructorConfig.parametersConfig.size(); i < l; i++)
						{
							final List<Check> checks = constructorConfig.parametersConfig.get(i).checks;

							if (checks != null && checks.size() > 0)
							{
								addChecks(constructor, i, checks);
							}
						}
					}
				}

			/*
			 * apply method parameter and return value checks
			 */
			if (classConfig.methodsConfig != null)
				for (final MethodConfiguration methodConfig : classConfig.methodsConfig)
				{
					if (methodConfig.parametersConfig != null)
					{
						final Class< ? >[] parameterTypes = new Class[methodConfig.parametersConfig
								.size()];

						for (int i = 0, l = methodConfig.parametersConfig.size(); i < l; i++)
						{
							parameterTypes[i] = methodConfig.parametersConfig.get(i).type;
						}

						final Method method = classConfig.type.getDeclaredMethod(methodConfig.name,
								parameterTypes);

						for (int i = 0, l = methodConfig.parametersConfig.size(); i < l; i++)
						{
							final List<Check> checks = methodConfig.parametersConfig.get(i).checks;

							if (checks != null && checks.size() > 0)
							{
								addChecks(method, i, checks);
							}
						}

						if (methodConfig.returnValueChecks != null
								&& methodConfig.returnValueChecks.size() > 0)
						{
							addChecks(method, methodConfig.returnValueChecks);
						}
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

	/**
	 * Introduces new constraint checks for the given constructor parameter
	 * 
	 * @param constructor
	 * @param parameterIndex 0 = first parameter
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Constructor constructor, final int parameterIndex,
			final Check... checks) throws InvalidConfigurationException
	{
		final Class clazz = constructor.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addChecks(constructor, parameterIndex, checks);
	}

	/**
	 * Introduces new constraint checks for the given constructor parameter
	 * 
	 * @param constructor
	 * @param parameterIndex 0 = first parameter
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Constructor constructor, final int parameterIndex,
			final Collection<Check> checks) throws InvalidConfigurationException
	{
		final Class clazz = constructor.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addChecks(constructor, parameterIndex, checks.toArray(new Check[checks.size()]));
	}

	/**
	 * Introduces new constraint checks for the given field
	 * 
	 * @param field
	 * @param checks
	 */
	public void addChecks(final Field field, final Check... checks)
	{
		final Class clazz = field.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addChecks(field, checks);
	}

	/**
	 * Introduces new constraint checks for the given field
	 * 
	 * @param field
	 * @param checks
	 */
	public void addChecks(final Field field, final Collection<Check> checks)
	{
		final Class clazz = field.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addChecks(field, checks.toArray(new Check[checks.size()]));
	}

	/**
	 * Introduces new constraint checks for the method's return value
	 * 
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Method method, final Check... checks)
			throws InvalidConfigurationException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addCheck(method, checks);
	}

	/**
	 * Introduces new constraint checks for the method's return value
	 * 
	 * @param method
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Method method, final Collection<Check> checks)
			throws InvalidConfigurationException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addCheck(method, checks.toArray(new Check[checks.size()]));
	}

	/**
	 * Introduces new constraint checks for the given method parameter
	 * 
	 * @param method
	 * @param parameterIndex 0 = first parameter
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Method method, final int parameterIndex, final Check... checks)
			throws InvalidConfigurationException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addCheck(method, parameterIndex, checks);
	}

	/**
	 * Introduces new constraint checks for the given method parameter
	 * 
	 * @param method
	 * @param parameterIndex 0 = first parameter
	 * @param checks
	 * @throws InvalidConfigurationException
	 */
	public void addChecks(final Method method, final int parameterIndex,
			final Collection<Check> checks) throws InvalidConfigurationException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.addCheck(method, parameterIndex, checks.toArray(new Check[checks.size()]));
	}

	public void addChecks(final OValConfiguration config) throws OValException
	{
		try
		{
			if (config.constraintSetsConfig != null)
			{
				for (final ConstraintSetConfiguration constraintSetConfiguration : config.constraintSetsConfig)
				{
					addConstraintSet(constraintSetConfiguration);
				}
			}

			if (config.classesConfig != null && config.classesConfig.size() > 0)
				for (final ClassConfiguration classConfig : config.classesConfig)
				{
					addChecks(classConfig);
				}
		}
		catch (SecurityException e)
		{
			throw new ReflectionException("SecurityException occured", e);
		}
	}

	public void addConstraintSet(final ConstraintSet constraintSet)
			throws ConstraintSetAlreadyDefinedException
	{
		if (constraintSetsById.containsKey(constraintSet.id))
			throw new ConstraintSetAlreadyDefinedException(
					"Another constraint set with the same fully qualified id " + constraintSet.id
							+ " has already been defined.");

		constraintSetsById.put(constraintSet.id, constraintSet);
	}

	public void addConstraintSet(final ConstraintSetConfiguration constraintSetConfigurations)
	{
		final ConstraintSet cs = new ConstraintSet();
		cs.checks = CollectionFactory.INSTANCE.createSet(constraintSetConfigurations.checks == null
				? 2 : constraintSetConfigurations.checks.size());
		cs.checks.addAll(constraintSetConfigurations.checks);
		cs.id = constraintSetConfigurations.id;
		addConstraintSet(cs);
	}

	/**
	 * Adds a message bundle
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it was already registered
	 */
	public boolean addMessageBundle(final ResourceBundle messageBundle)
	{
		if (messageBundles.contains(messageBundle)) return false;

		messageBundles.addFirst(messageBundle);
		final List<String> keys = CollectionFactory.INSTANCE.createList();

		for (final Enumeration<String> keysEnum = messageBundle.getKeys(); keysEnum
				.hasMoreElements();)
		{
			keys.add(keysEnum.nextElement());
		}

		messageBundleKeys.put(messageBundle, keys);

		return true;
	}

	private void checkConstraint(final List<ConstraintViolation> violations, final Check check,
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

			final ClassChecks cc = getClassChecks(targetClass);
			final String constraintSetId = assertConstraintSetCheck.getId();

			ConstraintSet cs = cc.constraintSetsByLocalId.get(constraintSetId);
			if (cs == null)
			{
				cs = constraintSetsById.get(constraintSetId);
			}
			if (cs == null)
			{
				throw new UndefinedConstraintSetException("No constraint set with id "
						+ constraintSetId + " defined.");
			}

			final Set<Check> referencedChecks = cs.getChecks(this);

			if (referencedChecks != null)
			{
				for (final Check referencedCheck : referencedChecks)
				{
					checkConstraint(violations, referencedCheck, validatedObject, valueToValidate,
							context);
				}
			}
			return;
		}

		/*
		 * special handling of the FieldConstraints constraint
		 */
		if (check instanceof AssertFieldConstraintsCheck)
		{
			// the name of the field whose constraints shall be used
			String fieldName = ((AssertFieldConstraintsCheck) check).getFieldName();

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
			Field field = null;
			for (Class fieldClass = targetClass; field == null
					&& fieldClass.getClass() != Object.class;)
			{
				try
				{
					field = fieldClass.getDeclaredField(fieldName);
				}
				catch (final NoSuchFieldException ex)
				{
					fieldClass = fieldClass.getSuperclass();
				}
			}

			if (field == null)
			{
				throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <"
						+ targetClass + "> or its super classes.");
			}

			final ClassChecks cc = getClassChecks(field.getDeclaringClass());
			final Set<Check> referencedChecks = cc.checksByField.get(field);
			if (referencedChecks != null)
			{
				for (final Check check2 : referencedChecks)
				{
					checkConstraint(violations, check2, validatedObject, valueToValidate, context);
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

	ClassChecks getClassChecks(final Class clazz)
	{
		synchronized (checksByClass)
		{
			ClassChecks checks = checksByClass.get(clazz);
			if (checks == null)
			{
				checks = new ClassChecks(clazz);
				checksByClass.put(clazz, checks);

				addChecks(configurer.getClassConfiguration(clazz));
			}
			return checks;
		}
	}

	private Object getFieldValue(final Object validatedObject, final Field field)
			throws AccessingFieldValueFailedException
	{
		try
		{
			if (!field.isAccessible()) field.setAccessible(true);
			return field.get(validatedObject);
		}
		catch (Exception ex)
		{
			throw new AccessingFieldValueFailedException("Accessing value of field "
					+ field.getName() + "failed.", validatedObject, new FieldContext(field), ex);
		}
	}

	private Object getGetterValue(final Object validatedObject, final Method getter)
			throws InvokingGetterFailedException
	{
		try
		{
			return getter.invoke(validatedObject, (Object[]) null);
		}
		catch (Exception ex)
		{
			throw new InvokingGetterFailedException("Executing getter method " + getter.getName()
					+ " failed.", validatedObject, new MethodReturnValueContext(getter), ex);
		}
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
	private boolean isCurrentlyValidated(final Object object)
	{
		return currentlyValidatedObjects.get().contains(object);
	}

	/**
	 * Removes a check from the given constructor parameter
	 * 
	 * @param constructor
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 */
	public void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check)
	{
		final Class clazz = constructor.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.removeCheck(constructor, parameterIndex, check);
	}

	/**
	 * Removes a check from the given field
	 * 
	 * @param field
	 * @param check
	 */
	public void removeCheck(final Field field, final Check check)
	{
		final Class clazz = field.getDeclaringClass();
		final ClassChecks cc = getClassChecks(clazz);
		cc.removeCheck(field, check);
	}

	/**
	 * Removes a check from the given method parameter
	 * 
	 * @param method
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 */
	public void removeCheck(final Method method, final int parameterIndex, final Check check)
	{
		final Class clazz = method.getDeclaringClass();
		final ClassChecks checks = getClassChecks(clazz);
		checks.removeCheck(method, parameterIndex, check);
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

	/**
	 * Removes the message bundle
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it wasn't registered
	 */
	public boolean removeMessageBundle(final ResourceBundle messageBundle)
	{
		if (!messageBundles.contains(messageBundle)) return false;

		messageBundles.remove(messageBundle);
		return true;
	}

	String renderMessage(final OValContext context, final Object value, final Check check)
	{
		String messageKey = check.getMessage();

		for (final ResourceBundle bundle : messageBundles)
		{
			final List<String> keys = messageBundleKeys.get(bundle);
			if (keys.contains(messageKey))
			{
				messageKey = bundle.getString(messageKey);
				break;
			}
		}

		// if no place holders are in the message return just return it
		if (messageKey.indexOf('{') == -1) return messageKey;

		final LinkedList<Object> args = new LinkedList<Object>();
		args.addLast(context);
		args.addLast(value);
		final String[] messageValues = check.getMessageValues();
		if (messageValues != null)
		{
			for (final String val : messageValues)
			{
				args.addLast(val);
			}
		}

		return MessageFormat.format(messageKey, args.toArray());
	}

	/**
	 * resets the constraint configuration
	 *
	 */
	public void resetChecks()
	{
		synchronized (this)
		{
			checksByClass.clear();
			constraintSetsById.clear();
		}
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
			final Constructor constructor, final Object[] parameters)
	{
		final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());

		final Map<Integer, Set<Check>> parameterChecks = cc.checksByConstructorParameter
				.get(constructor);

		if (parameterChecks == null) return null;

		final String[] parameterNames = parameterNameResolver.getParameterNames(constructor);
		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();
		for (int i = 0; i < parameters.length; i++)
		{
			final Set<Check> checks = parameterChecks.get(i);

			if (checks != null)
			{
				final Object valueToValidate = parameters[i];
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

	private void validateField(final Object validatedObject, final Field field,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(field.getDeclaringClass());

		final Set<Check> checks = cc.checksByField.get(field);

		if (checks != null)
		{
			final Object valueToValidate = getFieldValue(validatedObject, field);
			final FieldContext context = new FieldContext(field);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, valueToValidate, context);
			}
		}
	}

	private void validateGetter(final Object validatedObject, final Method getter,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(getter.getDeclaringClass());

		final Set<Check> checks = cc.checksByGetter.get(getter);

		if (checks != null)
		{
			final Object valueToValidate = getGetterValue(validatedObject, getter);
			final MethodReturnValueContext context = new MethodReturnValueContext(getter);

			for (final Check check : checks)
			{
				if (!check.isSatisfied(validatedObject, valueToValidate, context))
				{
					final String errorMessage = renderMessage(context, valueToValidate, check);
					violations.add(new ConstraintViolation(errorMessage, validatedObject,
							valueToValidate, context));
				}
			}
		}
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	List<ConstraintViolation> validateMethodParameters(final Object validatedObject,
			final Method method, final Object[] parameters)
	{
		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final Map<Integer, Set<Check>> parameterChecks = cc.checksByMethodParameter.get(method);

		// check if the method has any parameter checks at all
		if (parameterChecks == null) return null;

		final String[] parameterNames = parameterNameResolver.getParameterNames(method);
		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();

		for (int i = 0; i < parameters.length; i++)
		{
			final Set<Check> checks = parameterChecks.get(i);

			if (checks != null)
			{
				final Object valueToValidate = parameters[i];
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

		final Set<Check> checks = cc.checksByMethod.get(method);

		if (checks == null) return null;

		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList(8);

		final MethodReturnValueContext context = new MethodReturnValueContext(method);

		for (final Check check : checks)
		{
			checkConstraint(violations, check, validatedObject, methodReturnValue, context);
		}
		return violations.size() == 0 ? null : violations;
	}

	/**
	 * validate validatedObject based on the constraints of the given clazz
	 */
	private void validateObject(final Object validatedObject, final Class< ? > clazz,
			final List<ConstraintViolation> violations)
	{
		// abort if the root class is reached
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
