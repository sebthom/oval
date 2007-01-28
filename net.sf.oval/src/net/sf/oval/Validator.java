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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.configuration.AnnotationsConfigurer;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.constraints.AssertCheck;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.constraints.AssertValidCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodPostExecutionContext;
import net.sf.oval.contexts.MethodPreExecutionContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.ConstraintSetAlreadyDefinedException;
import net.sf.oval.exceptions.ExpressionLanguageException;
import net.sf.oval.exceptions.FieldNotFoundException;
import net.sf.oval.exceptions.InvalidConfigurationException;
import net.sf.oval.exceptions.MethodNotFoundException;
import net.sf.oval.exceptions.OValException;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.exceptions.UndefinedConstraintSetException;
import net.sf.oval.exceptions.ValidationFailedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.utils.ArrayUtils;
import net.sf.oval.utils.ListOrderedSet;
import net.sf.oval.utils.ReflectionUtils;
import net.sf.oval.utils.StringUtils;
import net.sf.oval.utils.ThreadLocalList;

/**
 * @author Sebastian Thomschke
 */
public class Validator
{
	private final static Logger LOG = Logger.getLogger(Validator.class.getName());

	private final Map<Class, ClassChecks> checksByClass = new WeakHashMap<Class, ClassChecks>();
	private final ListOrderedSet<Configurer> configurers = new ListOrderedSet<Configurer>();

	private final Map<String, ConstraintSet> constraintSetsById = CollectionFactory.INSTANCE
			.createMap();

	private final ThreadLocalList<Object> currentlyValidatedObjects = new ThreadLocalList<Object>();

	private MessageResolver messageResolver = new MessageResolverImpl();

	private ParameterNameResolver parameterNameResolver = new ParameterNameResolverEnumerationImpl();

	private Map<String, ExpressionLanguage> expressionLanguages = CollectionFactory.INSTANCE
			.createMap(2);

	/**
	 * Flag that indicates any configuration method related to profiles was called.
	 * Used for performance improvements.
	 */
	private boolean isProfilesFeatureUsed = false;

	private boolean isAllProfilesEnabledByDefault = true;

	private final Set<String> enabledProfiles = CollectionFactory.INSTANCE.createSet();
	private final Set<String> disabledProfiles = CollectionFactory.INSTANCE.createSet();

	/**
	 * Constructs a new validator object and uses a new isntance of
	 * AnnotationsConfigurer
	 */
	public Validator()
	{
		initializeDefaultELs();

		configurers.add(new AnnotationsConfigurer());
	}

	public Validator(final Collection<Configurer> configurers)
	{
		initializeDefaultELs();

		if (configurers != null)
		{
			this.configurers.addAll(configurers);
		}
	}

	public Validator(final Configurer... configurers)
	{
		initializeDefaultELs();

		if (configurers != null)
		{
			for (final Configurer configurer : configurers)
				this.configurers.add(configurer);
		}
	}

	private void addChecks(final ClassConfiguration classConfig) throws OValException
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
			 * apply method parameter and return value checks and pre/post conditions
			 */
			if (classConfig.methodConfigurations != null)
				for (final MethodConfiguration methodConfig : classConfig.methodConfigurations)
				{
					/*
					 * determine the method
					 */
					Method method = null;

					if (methodConfig.parameterConfigurations == null
							|| methodConfig.parameterConfigurations.size() == 0)
					{
						method = classConfig.type.getDeclaredMethod(methodConfig.name);
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
					}

					if (methodConfig.overwrite != null && methodConfig.overwrite)
					{
						cc.removeAllChecks(method);
					}

					/*
					 * configure parameter constraints
					 */
					if (methodConfig.parameterConfigurations != null
							&& methodConfig.parameterConfigurations.size() > 0)
					{
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

					/*
					 * configure return value constraints
					 */
					if (methodConfig.returnValueConfiguration != null)
					{
						if ((methodConfig.returnValueConfiguration.overwrite != null && methodConfig.returnValueConfiguration.overwrite))
						{
							cc.removeAllReturnValueChecks(method);
						}

						if (methodConfig.returnValueConfiguration.checks != null
								&& methodConfig.returnValueConfiguration.checks.size() > 0)
						{
							cc.addChecks(method, methodConfig.returnValueConfiguration.checks
									.toArray(new Check[methodConfig.returnValueConfiguration.checks
											.size()]));
						}
					}

					/*
					 * configure pre conditions
					 */
					if (methodConfig.preExecutionConfiguration != null)
					{
						if ((methodConfig.preExecutionConfiguration.overwrite != null && methodConfig.preExecutionConfiguration.overwrite))
						{
							cc.removeAllPreChecks(method);
						}

						if (methodConfig.preExecutionConfiguration.checks != null
								&& methodConfig.preExecutionConfiguration.checks.size() > 0)
						{
							cc.addChecks(method, methodConfig.preExecutionConfiguration.checks
									.toArray(//
									new PreCheck[methodConfig.preExecutionConfiguration.checks
											.size()]));
						}
					}

					/*
					 * configure post conditions
					 */
					if (methodConfig.postExecutionConfiguration != null)
					{
						if ((methodConfig.postExecutionConfiguration.overwrite != null && methodConfig.postExecutionConfiguration.overwrite))
						{
							cc.removeAllPostChecks(method);
						}

						if (methodConfig.postExecutionConfiguration.checks != null
								&& methodConfig.postExecutionConfiguration.checks.size() > 0)
						{
							cc.addChecks(method, methodConfig.postExecutionConfiguration.checks
									.toArray(//
									new PostCheck[methodConfig.postExecutionConfiguration.checks
											.size()]));
						}
					}
				}
		}
		catch (SecurityException ex)
		{
			throw new ReflectionException("SecurityException occured", ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new MethodNotFoundException("NoSuchMethodException occured.", ex);
		}
		catch (NoSuchFieldException ex)
		{
			throw new FieldNotFoundException("FieldNotFoundException occured.", ex);
		}
	}

	private void addConstraintSet(final ConstraintSet constraintSet)
			throws ConstraintSetAlreadyDefinedException
	{
		if (constraintSetsById.containsKey(constraintSet.id))
			throw new ConstraintSetAlreadyDefinedException(
					"Another constraint set with the same fully qualified id " + constraintSet.id
							+ " has already been defined.");

		constraintSetsById.put(constraintSet.id, constraintSet);
	}

	/**
	 * Registers a new constraint set.
	 * @param constraintSetConfigurations cannot be null
	 * @return
	 * @throws ConstraintSetAlreadyDefinedException
	 * @throws IllegalArgumentException if <code>constraintSetConfigurations == null</code>
	 */
	public ConstraintSet addConstraintSet(
			final ConstraintSetConfiguration constraintSetConfigurations)
			throws ConstraintSetAlreadyDefinedException, IllegalArgumentException
	{
		if (constraintSetConfigurations == null)
			throw new IllegalArgumentException("constraintSetConfigurations cannot be null");

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

	private void checkConstraint(final List<ConstraintViolation> violations, final Check check,
			final Object validatedObject, final Object valueToValidate, final OValContext context)
			throws OValException
	{
		if (!isAnyProfileEnabled(check.getProfiles())) return;

		/*
		 * special handling of the AssertValid constraint
		 */
		if (check instanceof AssertValidCheck)
		{
			checkConstraintAssertValid(violations, (AssertValidCheck) check, validatedObject,
					valueToValidate, context);
			return;
		}

		/*
		 * special handling of the FieldConstraints constraint
		 */
		if (check instanceof AssertConstraintSetCheck)
		{
			checkConstraintAssertConstraintSet(violations, (AssertConstraintSetCheck) check,
					validatedObject, valueToValidate, context);
			return;
		}

		/*
		 * special handling of the FieldConstraints constraint
		 */
		if (check instanceof AssertFieldConstraintsCheck)
		{
			checkConstraintAssertFieldConstraints(violations, (AssertFieldConstraintsCheck) check,
					validatedObject, valueToValidate, context);
			return;
		}

		/*
		 * special handling of the Condition constraint
		 */
		if (check instanceof AssertCheck)
		{
			checkConstraintAssert(violations, (AssertCheck) check, validatedObject,
					valueToValidate, context);
			return;
		}

		/*
		 * standard constraints handling
		 */
		if (!check.isSatisfied(validatedObject, valueToValidate, context))
		{
			final String errorMessage = renderMessage(context, valueToValidate, check.getMessage(),
					check.getMessageValues());
			violations.add(new ConstraintViolation(errorMessage, validatedObject, valueToValidate,
					context));
		}
	}

	private void checkConstraintAssert(final List<ConstraintViolation> violations,
			final AssertCheck check, final Object validatedObject, final Object valueToValidate,
			final OValContext context) throws ExpressionLanguageException
	{
		final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
		Map<String, Object> values = CollectionFactory.INSTANCE.createMap();
		values.put("value", valueToValidate);
		values.put("_this", validatedObject);
		if (!eng.evaluate(check.getExpression(), values))
		{
			final String errorMessage = renderMessage(context, valueToValidate, check.getMessage(),
					check.getMessageValues());

			violations.add(new ConstraintViolation(errorMessage, validatedObject, valueToValidate,
					context));
		}
	}

	private void checkConstraintAssertConstraintSet(final List<ConstraintViolation> violations,
			final AssertConstraintSetCheck check, final Object validatedObject,
			final Object valueToValidate, final OValContext context) throws OValException
	{
		Class targetClass = check.getSource();
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
				targetClass = ((MethodParameterContext) context).getMethod().getDeclaringClass();
			}
			else if (context instanceof MethodReturnValueContext)
			{
				targetClass = ((MethodReturnValueContext) context).getMethod().getDeclaringClass();
			}
		}

		final Class source = check.getSource();
		final String constraintSetId = check.getId();

		Collection<Check> referencedChecks = null;
		if (source == Object.class)
		{
			final ClassChecks cc = getClassChecks(targetClass);
			final ConstraintSet cs = cc.constraintSetsByLocalId.get(check.getId());
			if (cs != null) referencedChecks = cs.getChecks(this);
		}
		else
		{
			final ClassChecks cc = getClassChecks(source);
			final ConstraintSet cs = cc.constraintSetsByLocalId.get(check.getId());
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
			checkConstraint(violations, referencedCheck, validatedObject, valueToValidate, context);
		}
	}

	private void checkConstraintAssertFieldConstraints(final List<ConstraintViolation> violations,
			final AssertFieldConstraintsCheck check, final Object validatedObject,
			final Object valueToValidate, final OValContext context) throws OValException
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
		String fieldName = check.getFieldName();

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
		final Collection<Check> referencedChecks = cc.checksForFields.get(field);
		if (referencedChecks != null && referencedChecks.size() > 0)
		{
			for (final Check referencedCheck : referencedChecks)
			{
				checkConstraint(violations, referencedCheck, validatedObject, valueToValidate,
						context);
			}
		}
	}

	private void checkConstraintAssertValid(final List<ConstraintViolation> violations,
			final AssertValidCheck check, final Object validatedObject,
			final Object valueToValidate, final OValContext context) throws OValException
	{
		if (valueToValidate == null) return;

		// ignore circular dependencies
		if (isCurrentlyValidated(valueToValidate)) return;

		final List<ConstraintViolation> childViolations = validate(valueToValidate);

		if (childViolations.size() != 0)
		{
			final String errorMessage = renderMessage(context, valueToValidate, check.getMessage(),
					check.getMessageValues());

			violations.add(new ConstraintViolation(errorMessage, validatedObject, valueToValidate,
					context, childViolations
							.toArray(new ConstraintViolation[childViolations.size()])));
		}

		// if the value to validate is a collection also validate the collection items
		if (valueToValidate instanceof Collection && check.isRequireValidElements())
		{
			for (final Object item : (Collection) valueToValidate)
			{
				final List<ConstraintViolation> itemViolations = validate(item);

				if (itemViolations.size() != 0)
				{
					final String errorMessage = renderMessage(context, item, check.getMessage(),
							check.getMessageValues());

					violations.add(new ConstraintViolation(errorMessage, validatedObject, item,
							context, itemViolations.toArray(new ConstraintViolation[itemViolations
									.size()])));
				}
			}
		}

		// if the value to validate is a map also validate the map keys and values
		else if (valueToValidate instanceof Map && check.isRequireValidElements())
		{
			for (final Object item : ((Map) valueToValidate).keySet())
			{
				final List<ConstraintViolation> itemViolations = validate(item);

				if (itemViolations.size() != 0)
				{
					final String errorMessage = renderMessage(context, item, check.getMessage(),
							check.getMessageValues());

					violations.add(new ConstraintViolation(errorMessage, validatedObject, item,
							context, itemViolations.toArray(new ConstraintViolation[itemViolations
									.size()])));
				}
			}

			for (final Object item : ((Map) valueToValidate).values())
			{
				final List<ConstraintViolation> itemViolations = validate(item);

				if (itemViolations.size() != 0)
				{
					final String errorMessage = renderMessage(context, item, check.getMessage(),
							check.getMessageValues());

					violations.add(new ConstraintViolation(errorMessage, validatedObject, item,
							context, itemViolations.toArray(new ConstraintViolation[itemViolations
									.size()])));
				}
			}
		}
	}

	/**
	 * Enables all constraint profiles, i.e. all configured constraint will be validated.
	 */
	public synchronized void disableAllProfiles()
	{
		isProfilesFeatureUsed = true;
		isAllProfilesEnabledByDefault = false;

		enabledProfiles.clear();
		disabledProfiles.clear();
	}

	/**
	 * Disables a constraints profile.
	 * @param profile the id of the profile
	 * @param enabled
	 */
	public void disableProfile(final String profile)
	{
		isProfilesFeatureUsed = true;

		if (isAllProfilesEnabledByDefault)
			disabledProfiles.add(profile);
		else
			enabledProfiles.remove(profile);
	}

	/**
	 * Disables all constraint profiles, i.e. no configured constraint will be validated.
	 */
	public synchronized void enableAllProfiles()
	{
		isProfilesFeatureUsed = true;
		isAllProfilesEnabledByDefault = true;

		enabledProfiles.clear();
		disabledProfiles.clear();
	}

	/**
	 * Enables a constraints profile.
	 * @param profile the id of the profile
	 * @param enabled
	 */
	public void enableProfile(final String profile)
	{
		isProfilesFeatureUsed = true;

		if (isAllProfilesEnabledByDefault)
			disabledProfiles.remove(profile);
		else
			enabledProfiles.add(profile);
	}

	/**
	 * Returns the ClassChecks object for the particular class,
	 * allowing you to modify the checks
	 * 
	 * @param clazz cannot be null
	 * @return returns the ClassChecks for the given class
	 * @throws IllegalArgumentException if <code>clazz == null</code>
	 * @throws OValException
	 */
	public ClassChecks getClassChecks(final Class clazz) throws IllegalArgumentException,
			OValException
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");

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
					if (classConfig != null) addChecks(classConfig);
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

	private ConstraintSet getConstraintSet(final String constraintSetId) throws OValException
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

	private void initializeDefaultELs()
	{
		// JavaScript support
		if (ReflectionUtils.isClassPresent("org.mozilla.javascript.Context"))
		{
			registerExpressionLanguage("javascript", new ExpressionLanguageJavaScriptImpl());
			registerExpressionLanguage("js", new ExpressionLanguageJavaScriptImpl());
		}

		// Groovy support
		if (ReflectionUtils.isClassPresent("groovy.lang.Binding"))
		{
			registerExpressionLanguage("groovy", new ExpressionLanguageGroovyImpl());
		}
	}

	private boolean isAnyProfileEnabled(final String[] profileIds)
	{
		if (profileIds == null || profileIds.length == 0) return isAllProfilesEnabledByDefault;

		for (final String profile : profileIds)
		{
			if (isProfileEnabled(profile))
			{
				return true;
			}
		}
		return false;
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
	 * 
	 * @param languageId the id of the language, cannot be null
	 * @return
	 * @throws IllegalArgumentException if <code>languageName == null</code>
	 */
	public boolean isExpressionLanguageAvailable(final String languageId)
			throws IllegalArgumentException
	{
		if (languageId == null) throw new IllegalArgumentException("languageName cannot be null");

		return expressionLanguages.get(languageId) != null;
	}

	/**
	 * 
	 * @param profile
	 * @return
	 */
	public boolean isProfileEnabled(final String profile)
	{
		if (isProfilesFeatureUsed)
		{
			if (isAllProfilesEnabledByDefault) return !disabledProfiles.contains(profile);

			return enabledProfiles.contains(profile);
		}
		return true;
	}

	/**
	 * 
	 * @param languageId
	 * @param expressionLanguage
	 * @throws IllegalArgumentException if <code>languageId == null || expressionLanguage == null</code>
	 */
	public void registerExpressionLanguage(final String languageId,
			final ExpressionLanguage expressionLanguage) throws IllegalArgumentException
	{
		if (languageId == null) throw new IllegalArgumentException("languageId cannot be null");
		if (expressionLanguage == null)
			throw new IllegalArgumentException("expressionLanguage cannot be null");

		LOG.info("Expression language '" + languageId + "' registered: " + expressionLanguage);
		expressionLanguages.put(languageId, expressionLanguage);
	}

	/**
	 * Removes the constraint set with the given id
	 * @param id the id of the constraint set to remove, cannot be null
	 * @return the removed constraint set
	 * @throws IllegalArgumentException if <code>id == null</code>
	 */
	public ConstraintSet removeConstraintSet(final String id) throws IllegalArgumentException
	{
		if (id == null) throw new IllegalArgumentException("id cannot be null");

		return constraintSetsById.remove(id);
	}

	private String renderMessage(final OValContext context, final Object value,
			final String messageKey, final String... messageValues)
	{
		String message = messageResolver.getMessage(messageKey);
		if (message == null) message = messageKey;

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		final int messageValuesCount = messageValues == null ? 0 : messageValues.length;

		message = StringUtils.replaceAll(message, "{0}", context.toString());
		message = StringUtils.replaceAll(message, "{1}", value == null ? "null" : value.toString());

		for (int i = 0; i < messageValuesCount; i++)
		{
			message = StringUtils.replaceAll(message, "{" + Integer.toString(i + 2) + "}",
					messageValues[i]);
		}
		return message;
	}

	/**
	 * clears the checks and constraint sets => a reconfiguration using the
	 * currently registered configurers will automatically happen
	 */
	public void reset()
	{
		checksByClass.clear();
		constraintSetsById.clear();
	}

	/**
	 * @param messageResolver the messageResolver to set
	 * @throws IllegalArgumentException if <code>messageResolver == null</code>
	 */
	public void setMessageResolver(final MessageResolver messageResolver)
			throws IllegalArgumentException
	{
		if (messageResolver == null)
			throw new IllegalArgumentException("messageResolver cannot be null");

		this.messageResolver = messageResolver;
	}

	/**
	 * @param parameterNameResolver the parameterNameResolver to set, cannot be null
	 * @throws IllegalArgumentException if <code>parameterNameResolver == null</code>
	 */
	public void setParameterNameResolver(final ParameterNameResolver parameterNameResolver)
			throws IllegalArgumentException
	{
		if (parameterNameResolver == null)
			throw new IllegalArgumentException("parameterNameResolver cannot be null");

		this.parameterNameResolver = parameterNameResolver;
	}

	/**
	 * validates the field and getter constrains of the given object
	 * 
	 * @param validatedObject the object to validate, cannot be null
	 * @return  a list with the detected constraint violations. if no violations are detected an empty list is returned
	 * @throws ValidationFailedException
	 * @throws IllegalArgumentException if <code>validatedObject == null</code>
	 */
	public List<ConstraintViolation> validate(final Object validatedObject)
			throws IllegalArgumentException, ValidationFailedException
	{
		if (validatedObject == null)
			throw new IllegalArgumentException("validatedObject cannot be null");

		if (validatedObject instanceof Class) return validate((Class) validatedObject);

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
	 * Validates the static field and getter constrains of the given class.
	 * Constraints specified for super classes are not taken in account.
	 */
	private List<ConstraintViolation> validate(final Class validatedClass)
			throws IllegalArgumentException, ValidationFailedException
	{
		final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();

		final ClassChecks cc = getClassChecks(validatedClass);

		// validate static field constraints
		for (final Field field : cc.constrainedStaticFields)
		{
			final Collection<Check> checks = cc.checksForFields.get(field);

			if (checks != null && checks.size() > 0)
			{
				final Object valueToValidate = ReflectionUtils.getFieldValue(field, null);
				final FieldContext context = new FieldContext(field);

				for (final Check check : checks)
				{
					checkConstraint(violations, check, validatedClass, valueToValidate, context);
				}
			}
		}

		// validate constraints on getter methods
		for (final Method getter : cc.constrainedStaticGetters)
		{
			final Collection<Check> checks = cc.checksForMethodReturnValues.get(getter);

			if (checks != null && checks.size() > 0)
			{
				final Object valueToValidate = ReflectionUtils.invokeMethod(getter, null);
				final MethodReturnValueContext context = new MethodReturnValueContext(getter);

				for (final Check check : checks)
				{
					checkConstraint(violations, check, validatedClass, valueToValidate, context);
				}
			}
		}

		return violations;
	}

	/**
	 * Validates the give arguments against the defined constructor parameter constraints.<br>
	 * <br>  
	 * This method is primarily provided for use by the Guard class.<br>
	 * 
	 * @return null if no violation, otherwise a list
	 * @throws ValidationFailedException
	 * @see Guard
	 */
	public List<ConstraintViolation> validateConstructorParameters(final Object validatedObject,
			final Constructor constructor, final Object[] argsToValidate)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(constructor.getDeclaringClass());
			final Map<Integer, Collection<Check>> parameterChecks = cc.checksForConstructorParameter
					.get(constructor);

			// if no parameter checks exist just return null
			if (parameterChecks == null) return null;

			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();

			final String[] parameterNames = parameterNameResolver.getParameterNames(constructor);

			for (int i = 0; i < argsToValidate.length; i++)
			{
				final Collection<Check> checks = parameterChecks.get(i);

				if (checks != null && checks.size() > 0)
				{
					final Object valueToValidate = argsToValidate[i];
					final ConstructorParameterContext context = new ConstructorParameterContext(
							constructor, i, parameterNames[i]);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate,
								context);
					}
				}
			}
			return violations.size() == 0 ? null : violations;
		}
		catch (OValException ex)
		{
			throw new ValidationFailedException(
					"Validation of constructor parameters failed. Constructor: " + constructor
							+ " Validated object:" + validatedObject, ex);
		}

	}

	/**
	 * Validates the give value against the defined field constraints.<br>
	 * 
	 * @return null if no violation, otherwise a list
	 * @throws ValidationFailedException 
	 */
	public List<ConstraintViolation> validateField(final Object validatedObject, final Field field,
			final Object fieldValueToValidate) throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(field.getDeclaringClass());
			final Collection<Check> checks = cc.checksForFields.get(field);

			if (checks == null || checks.size() == 0) return null;

			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList();

			final FieldContext context = new FieldContext(field);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, fieldValueToValidate, context);
			}
			return violations.size() == 0 ? null : violations;
		}
		catch (OValException ex)
		{
			throw new ValidationFailedException("Field validation failed. Field: " + field
					+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the post conditions for a method call.<br>
	 * <br>  
	 * This method is primarily provided for use by the Guard class.<br>
	 * 
	 * @return null if no violation, otherwise a list
	 * @throws ValidationFailedException  
	 * @see Guard
	 */
	public List<ConstraintViolation> validateMethodPost(final Object validatedObject,
			final Method method, final Object[] args, final Object returnValue)
			throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Collection<Check> returnValueChecks = cc.checksForMethodReturnValues.get(method);
			final Set<PostCheck> postChecks = cc.checksForMethodsPostExcecution.get(method);

			// shortcut: check if any post checks for this method exist
			if (postChecks == null && returnValueChecks == null) return null;

			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList(2);

			// return value 
			if (returnValueChecks != null && returnValueChecks.size() > 0)
			{
				final MethodReturnValueContext context = new MethodReturnValueContext(method);

				for (final Check check : returnValueChecks)
				{
					checkConstraint(violations, check, validatedObject, returnValue, context);
				}
			}

			// @Post
			if (postChecks != null)
			{
				final String[] parameterNames = parameterNameResolver.getParameterNames(method);
				final boolean hasParameters = parameterNames.length > 0;

				final MethodPostExecutionContext context = new MethodPostExecutionContext(method);

				for (final PostCheck check : postChecks)
				{
					final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
					final Map<String, Object> values = CollectionFactory.INSTANCE.createMap();
					values.put("_this", validatedObject);
					values.put("_result", returnValue);
					if (hasParameters)
					{
						values.put("_args", args);
						for (int i = 0; i < args.length; i++)
						{
							values.put(parameterNames[i], args[i]);
						}
					}
					else
						values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);

					if (!eng.evaluate(check.getExpression(), values))
					{
						final String errorMessage = renderMessage(context, null,
								check.getMessage(), check.getExpression());

						violations.add(new ConstraintViolation(errorMessage, validatedObject, null,
								context));
					}
				}
			}

			return violations.size() == 0 ? null : violations;
		}
		catch (OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the pre conditions for a method call.<br>
	 * <br>  
	 * This method is primarily provided for use by the Guard class.<br>
	 * 
	 * @return null if no violation, otherwise a list 
	 * @throws ValidationFailedException 
	 * @see Guard
	 */
	public List<ConstraintViolation> validateMethodPre(final Object validatedObject,
			final Method method, final Object[] args) throws ValidationFailedException
	{
		try
		{
			final ClassChecks cc = getClassChecks(method.getDeclaringClass());
			final Map<Integer, Collection<Check>> parameterChecks = cc.checksForMethodParameters
					.get(method);
			final Set<PreCheck> preChecks = cc.checksForMethodsPreExecution.get(method);

			// shortcut: check if any pre checks for this method exist
			if (preChecks == null && parameterChecks == null) return null;

			final List<ConstraintViolation> violations = CollectionFactory.INSTANCE.createList(2);

			final String[] parameterNames = parameterNameResolver.getParameterNames(method);

			final boolean hasParameters = parameterNames.length > 0;

			/*
			 * parameter constraints validation
			 */
			if (parameterChecks != null && hasParameters)
			{
				for (int i = 0; i < args.length; i++)
				{
					final Collection<Check> checks = parameterChecks.get(i);

					if (checks != null && checks.size() > 0)
					{
						final Object valueToValidate = args[i];
						final MethodParameterContext context = new MethodParameterContext(method,
								i, parameterNames[i]);

						for (final Check check : checks)
						{
							checkConstraint(violations, check, validatedObject, valueToValidate,
									context);
						}
					}
				}
			}

			/*
			 * @Pre
			 */
			if (preChecks != null)
			{
				final MethodPreExecutionContext context = new MethodPreExecutionContext(method);

				for (final PreCheck check : preChecks)
				{
					final ExpressionLanguage eng = expressionLanguages.get(check.getLanguage());
					final Map<String, Object> values = CollectionFactory.INSTANCE.createMap();
					values.put("_this", validatedObject);
					if (hasParameters)
					{
						values.put("_args", args);
						for (int i = 0; i < args.length; i++)
						{
							values.put(parameterNames[i], args[i]);
						}
					}
					else
						values.put("_args", ArrayUtils.EMPTY_OBJECT_ARRAY);

					if (!eng.evaluate(check.getExpression(), values))
					{
						final String errorMessage = renderMessage(context, null,
								check.getMessage(), check.getExpression());

						violations.add(new ConstraintViolation(errorMessage, validatedObject, null,
								context));
					}
				}
			}
			return violations.size() == 0 ? null : violations;
		}
		catch (OValException ex)
		{
			throw new ValidationFailedException(
					"Method post conditions validation failed. Method: " + method
							+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * validate validatedObject based on the constraints of the given clazz 
	 */
	private void validateObject(final Object validatedObject, final Class< ? > clazz,
			final List<ConstraintViolation> violations) throws ValidationFailedException
	{
		// abort if the root class has been reached
		if (clazz == Object.class) return;

		try
		{
			final ClassChecks cc = getClassChecks(clazz);

			// validate field constraints
			for (final Field field : cc.constrainedFields)
			{
				final Collection<Check> checks = cc.checksForFields.get(field);

				if (checks != null && checks.size() > 0)
				{
					final Object valueToValidate = ReflectionUtils.getFieldValue(field,
							validatedObject);
					final FieldContext context = new FieldContext(field);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate,
								context);
					}
				}
			}

			// validate constraints on getter methods
			for (final Method getter : cc.constrainedGetters)
			{
				final Collection<Check> checks = cc.checksForMethodReturnValues.get(getter);

				if (checks != null && checks.size() > 0)
				{
					final Object valueToValidate = ReflectionUtils.invokeMethod(getter,
							validatedObject);
					final MethodReturnValueContext context = new MethodReturnValueContext(getter);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate,
								context);
					}
				}
			}

			// if the super class is annotated to be validatable also validate it against the object
			validateObject(validatedObject, clazz.getSuperclass(), violations);
		}
		catch (OValException ex)
		{
			throw new ValidationFailedException("Object validation failed. Class: " + clazz
					+ " Validated object: " + validatedObject, ex);
		}
	}
}
