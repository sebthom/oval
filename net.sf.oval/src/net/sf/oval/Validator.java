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
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.AssertValidCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.AccessingFieldValueFailedException;
import net.sf.oval.exceptions.ConstrainedAnnotationNotPresentException;
import net.sf.oval.exceptions.InvokingGetterFailedException;
import net.sf.oval.exceptions.UndefinedConstraintSetException;
import net.sf.oval.utils.ThreadLocalList;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
public final class Validator
{
	private final static Logger LOG = Logger.getLogger(Validator.class.getName());

	private final ThreadLocalList<Object> currentlyValidatedObjects = new ThreadLocalList<Object>();

	private final WeakHashMap<Class, ClassConfiguration> checksByClass = new WeakHashMap<Class, ClassConfiguration>();

	private final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();
	private final Map<ResourceBundle, List<String>> messageBundleKeys = CollectionFactory.INSTANCE
			.createMap(8);

	private ParameterNameResolver parameterNameResolver = new ParameterNameResolverDefaultImpl();

	private final Map<String, ConstraintSet> constraintSetsById = CollectionFactory.INSTANCE
			.createMap();

	/**
	 * public constructor
	 */
	public Validator()
	{
		// add the message bundle for the pre-built constraints in the default locale
		addMessageBundle(ResourceBundle.getBundle("net/sf/oval/constraints/Messages"));
	}

	/**
	 * Introduces a new constraints check for the specified constructor parameter
	 * 
	 * @param constructor
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	public void addCheck(final Constructor constructor, final int parameterIndex, final Check check)
			throws ConstrainedAnnotationNotPresentException
	{
		final Class clazz = constructor.getDeclaringClass();
		final ClassConfiguration checks = getClassConfig(clazz);
		checks.addCheck(constructor, parameterIndex, check);
	}

	/**
	 * Introduces a new constraints check for the specified field
	 * 
	 * @param field
	 * @param check
	 */
	public void addCheck(final Field field, final Check check)
	{
		final Class clazz = field.getDeclaringClass();
		final ClassConfiguration checks = getClassConfig(clazz);
		checks.addCheck(field, check);
	}

	/**
	 * Introduces a new constraints check for the specified method parameter
	 * 
	 * @param method
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	public void addCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstrainedAnnotationNotPresentException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassConfiguration checks = getClassConfig(clazz);
		checks.addCheck(method, parameterIndex, check);
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
						valueToValidate, context, check));
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
					targetClass = ((MethodReturnValueContext) context).getGetter()
							.getDeclaringClass();
				}
				else
				{
					//TODO
					return;
				}
			}

			final ClassConfiguration cc = getClassConfig(targetClass);
			final String constraintSetId = assertConstraintSetCheck.getId();

			ConstraintSet cs = cc.constraintSetsByShortId.get(constraintSetId);
			if (cs == null)
			{
				cs = constraintSetsById.get(constraintSetId);
			}
			if (cs == null)
			{
				throw new UndefinedConstraintSetException("No constraint set with id "
						+ constraintSetId + " defined.");
			}

			Set<Check> referencedChecks = cs.getChecks(this);

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
		 * standard constraints handling
		 */
		if (!check.isSatisfied(validatedObject, valueToValidate, context))
		{
			final String errorMessage = renderMessage(context, valueToValidate, check);
			violations.add(new ConstraintViolation(errorMessage, validatedObject, valueToValidate,
					context, check));
		}
	}

	ClassConfiguration getClassConfig(final Class clazz)
	{
		synchronized (checksByClass)
		{
			ClassConfiguration checks = checksByClass.get(clazz);
			if (checks == null)
			{
				checks = new ClassConfiguration(clazz, this);

				// register the constraint set definitions in a global map
				for (final ConstraintSet cs : checks.constraintSetsByShortId.values())
				{
					if (constraintSetsById.containsKey(cs.id))
					{
						LOG.warning("Another constraint set with the same fully qualified id "
								+ cs.id + " has already been defined.");
						//TODO what to do?
					}
					else
						constraintSetsById.put(cs.id, cs);
				}

			}
			checksByClass.put(clazz, checks);
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
	 * Removes a check from the specified constructor parameter
	 * 
	 * @param constructor
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	public void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstrainedAnnotationNotPresentException
	{
		final Class clazz = constructor.getDeclaringClass();
		final ClassConfiguration cc = getClassConfig(clazz);
		cc.removeCheck(constructor, parameterIndex, check);
	}

	/**
	 * Removes a check from the specified field
	 * 
	 * @param field
	 * @param check
	 */
	public void removeCheck(final Field field, final Check check)
	{
		final Class clazz = field.getDeclaringClass();
		final ClassConfiguration cc = getClassConfig(clazz);
		cc.removeCheck(field, check);
	}

	/**
	 * Removes a check from the specified method parameter
	 * 
	 * @param method
	 * @param parameterIndex 0 = first parameter
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	public void removeCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstrainedAnnotationNotPresentException
	{
		final Class clazz = method.getDeclaringClass();
		final ClassConfiguration checks = getClassConfig(clazz);
		checks.removeCheck(method, parameterIndex, check);
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
		final ClassConfiguration cc = getClassConfig(constructor.getDeclaringClass());

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
		final ClassConfiguration cc = getClassConfig(field.getDeclaringClass());

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
		final ClassConfiguration cc = getClassConfig(getter.getDeclaringClass());

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
							valueToValidate, context, check));
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
		final ClassConfiguration cc = getClassConfig(method.getDeclaringClass());

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
		final ClassConfiguration cc = getClassConfig(method.getDeclaringClass());

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

		final ClassConfiguration cc = getClassConfig(clazz);

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
