/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.AssertFieldConstraintsCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.context.ClassContext;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ConstraintSetAlreadyDefinedException;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.ExceptionTranslator;
import net.sf.oval.exception.ExpressionLanguageNotAvailableException;
import net.sf.oval.exception.FieldNotFoundException;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.MethodNotFoundException;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.UndefinedConstraintSetException;
import net.sf.oval.exception.ValidationFailedException;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.expression.ExpressionLanguageBeanShellImpl;
import net.sf.oval.expression.ExpressionLanguageGroovyImpl;
import net.sf.oval.expression.ExpressionLanguageJEXLImpl;
import net.sf.oval.expression.ExpressionLanguageJRubyImpl;
import net.sf.oval.expression.ExpressionLanguageJavaScriptImpl;
import net.sf.oval.expression.ExpressionLanguageMVELImpl;
import net.sf.oval.expression.ExpressionLanguageOGNLImpl;
import net.sf.oval.guard.ParameterNameResolver;
import net.sf.oval.guard.ParameterNameResolverEnumerationImpl;
import net.sf.oval.internal.ClassChecks;
import net.sf.oval.internal.CollectionFactoryHolder;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.MessageRenderer;
import net.sf.oval.internal.MessageResolverHolder;
import net.sf.oval.internal.util.ListOrderedSet;
import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.internal.util.ThreadLocalList;
import net.sf.oval.localization.MessageResolver;
import net.sf.oval.logging.LoggerFactory;

/**
 * @author Sebastian Thomschke
 */
public class Validator
{
	private final static Log LOG = Log.getLog(Validator.class);

	/**
	 * Returns a shared instance of the CollectionFactory
	 */
	public static CollectionFactory getCollectionFactory()
	{
		return CollectionFactoryHolder.getFactory();
	}

	/**
	 * @return the loggerFactory
	 */
	public static LoggerFactory getLoggerFactory()
	{
		return Log.getLoggerFactory();
	}

	/**
	 * @return the messageResolver
	 */
	public static MessageResolver getMessageResolver()
	{
		return MessageResolverHolder.getMessageResolver();
	}

	/**
	 * 
	 * @param factory the new collection factory to be used by all validator instances
	 */
	public static void setCollectionFactory(final CollectionFactory factory)
			throws IllegalArgumentException
	{
		CollectionFactoryHolder.setFactory(factory);
	}

	/**
	 * @param loggerFactory the loggerFactory to set
	 */
	public static void setLoggerFactory(final LoggerFactory loggerFactory)
	{
		Log.setLoggerFactory(loggerFactory);
	}

	/**
	 * @param messageResolver the messageResolver to set
	 * @throws IllegalArgumentException if <code>messageResolver == null</code>
	 */
	public static void setMessageResolver(final MessageResolver messageResolver)
			throws IllegalArgumentException
	{
		MessageResolverHolder.setMessageResolver(messageResolver);
	}

	private final Map<Class< ? >, ClassChecks> checksByClass = new WeakHashMap<Class< ? >, ClassChecks>();

	private final ListOrderedSet<Configurer> configurers = new ListOrderedSet<Configurer>(4);

	private final Map<String, ConstraintSet> constraintSetsById = CollectionFactoryHolder
			.getFactory().createMap(4);

	private final ThreadLocalList<Object> currentlyValidatedObjects = new ThreadLocalList<Object>();

	protected Map<String, ExpressionLanguage> expressionLanguages = CollectionFactoryHolder
			.getFactory().createMap(4);

	/**
	 * Flag that indicates any configuration method related to profiles was called.
	 * Used for performance improvements.
	 */
	private boolean isProfilesFeatureUsed = false;

	private boolean isAllProfilesEnabledByDefault = true;

	private final Set<String> disabledProfiles = CollectionFactoryHolder.getFactory().createSet();

	private final Set<String> enabledProfiles = CollectionFactoryHolder.getFactory().createSet();

	private ExceptionTranslator exceptionTranslator;

	protected ParameterNameResolver parameterNameResolver = new ParameterNameResolverEnumerationImpl();

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

	/**
	 * Registers object-level constraint checks
	 *  
	 * @param clazz
	 * @param checks
	 * @throws IllegalArgumentException if <code>clazz == null</code> or <code>checks == null</code> or checks is empty 
	 */
	public void addChecks(final Class< ? > clazz, final Check... checks)
			throws IllegalArgumentException
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(clazz);

		cc.addObjectChecks(checks);
	}

	@SuppressWarnings("unchecked")
	protected void addChecks(final ClassChecks cc, final ClassConfiguration classConfig)
			throws OValException
	{
		if (Boolean.TRUE.equals(classConfig.overwrite))
		{
			cc.clear();
		}

		if (classConfig.checkInvariants != null)
			cc.isCheckInvariants = classConfig.checkInvariants;

		try
		{
			/*
			 * apply object level checks
			 */
			if (classConfig.objectConfiguration != null)
			{
				final ObjectConfiguration objectConfig = classConfig.objectConfiguration;
				if (Boolean.TRUE.equals(objectConfig.overwrite))
				{
					cc.clearObjectChecks();
				}

				cc.addObjectChecks(objectConfig.checks);
			}

			/*
			 * apply field checks
			 */
			if (classConfig.fieldConfigurations != null)
				for (final FieldConfiguration fieldConfig : classConfig.fieldConfigurations)
				{
					final Field field = classConfig.type.getDeclaredField(fieldConfig.name);

					if (Boolean.TRUE.equals(fieldConfig.overwrite))
					{
						cc.clearFieldChecks(field);
					}

					if (fieldConfig.checks != null && fieldConfig.checks.size() > 0)
					{
						cc.addFieldChecks(field, fieldConfig.checks);
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
						final Class[] parameterTypes = new Class[constructorConfig.parameterConfigurations
								.size()];

						for (int i = 0, l = constructorConfig.parameterConfigurations.size(); i < l; i++)
						{
							parameterTypes[i] = constructorConfig.parameterConfigurations.get(i).type;
						}

						final Constructor constructor = classConfig.type
								.getDeclaredConstructor(parameterTypes);

						final String[] parameterNames = parameterNameResolver
								.getParameterNames(constructor);

						if (Boolean.TRUE.equals(constructorConfig.overwrite))
						{
							cc.clearConstructorChecks(constructor);
						}

						if (constructorConfig.postCheckInvariants != null
								&& constructorConfig.postCheckInvariants)
							cc.methodsWithCheckInvariantsPost.add(constructor);

						for (int i = 0, l = constructorConfig.parameterConfigurations.size(); i < l; i++)
						{
							final ParameterConfiguration parameterConfig = constructorConfig.parameterConfigurations
									.get(i);

							if (Boolean.TRUE.equals(parameterConfig.overwrite))
							{
								cc.clearConstructorParameterChecks(constructor, i);
							}

							final List<Check> checks = parameterConfig.checks;

							if (checks != null && checks.size() > 0)
							{
								cc.addConstructorParameterChecks(constructor, i, checks);
							}

							/* *******************
							 * applying field constraints to the single parameter of setter methods 
							 * *******************/
							if (classConfig.applyFieldConstraintsToConstructors != null
									&& classConfig.applyFieldConstraintsToConstructors
											.booleanValue())
							{
								final Field field = ReflectionUtils.getField(cc.clazz,
										parameterNames[i]);

								// check if a corresponding field has been found
								if (field != null
										&& parameterTypes[i].isAssignableFrom(field.getType()))
								{
									final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
									check.setFieldName(field.getName());
									cc.addConstructorParameterChecks(constructor, i, check);
								}
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
						final Class[] parameterTypes = new Class[methodConfig.parameterConfigurations
								.size()];

						for (int i = 0, l = methodConfig.parameterConfigurations.size(); i < l; i++)
						{
							parameterTypes[i] = methodConfig.parameterConfigurations.get(i).type;
						}

						method = classConfig.type.getDeclaredMethod(methodConfig.name,
								parameterTypes);
					}

					if (Boolean.TRUE.equals(methodConfig.overwrite))
					{
						cc.clearMethodChecks(method);
					}

					/* *******************
					 * applying field constraints to the single parameter of setter methods 
					 * *******************/
					if (classConfig.applyFieldConstraintsToSetters != null
							&& classConfig.applyFieldConstraintsToSetters.booleanValue())
					{
						final Field field = ReflectionUtils.getFieldForSetter(method);

						// check if a corresponding field has been found
						if (field != null)
						{
							final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
							check.setFieldName(field.getName());
							cc.addMethodParameterChecks(method, 0, check);
						}
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

							if (Boolean.TRUE.equals(parameterConfig.overwrite))
							{
								cc.clearMethodParameterChecks(method, i);
							}

							final List<Check> checks = parameterConfig.checks;

							if (checks != null && checks.size() > 0)
							{
								cc.addMethodParameterChecks(method, i, checks);
							}
						}
					}

					/*
					 * configure return value constraints
					 */
					if (methodConfig.returnValueConfiguration != null)
					{
						if (Boolean.TRUE.equals(methodConfig.returnValueConfiguration.overwrite))
						{
							cc.clearMethodReturnValueChecks(method);
						}

						if (methodConfig.returnValueConfiguration.checks != null
								&& methodConfig.returnValueConfiguration.checks.size() > 0)
						{
							cc.addMethodReturnValueChecks(method, methodConfig.isInvariant,
									methodConfig.returnValueConfiguration.checks);
						}
					}

					if (methodConfig.preCheckInvariants != null && methodConfig.preCheckInvariants)
					{
						cc.methodsWithCheckInvariantsPre.add(method);
					}

					/*
					 * configure pre conditions
					 */
					if (methodConfig.preExecutionConfiguration != null)
					{
						if (Boolean.TRUE.equals(methodConfig.preExecutionConfiguration.overwrite))
						{
							cc.clearMethodPreChecks(method);
						}

						if (methodConfig.preExecutionConfiguration.checks != null
								&& methodConfig.preExecutionConfiguration.checks.size() > 0)
						{
							cc.addMethodPreChecks(method,
									methodConfig.preExecutionConfiguration.checks);
						}
					}

					if (methodConfig.postCheckInvariants != null
							&& methodConfig.postCheckInvariants)
					{
						cc.methodsWithCheckInvariantsPost.add(method);
					}

					/*
					 * configure post conditions
					 */
					if (methodConfig.postExecutionConfiguration != null)
					{
						if (Boolean.TRUE.equals(methodConfig.postExecutionConfiguration.overwrite))
						{
							cc.clearMethodPostChecks(method);
						}

						if (methodConfig.postExecutionConfiguration.checks != null
								&& methodConfig.postExecutionConfiguration.checks.size() > 0)
						{
							cc.addMethodPostChecks(method,
									methodConfig.postExecutionConfiguration.checks);
						}
					}
				}
		}
		catch (final NoSuchMethodException ex)
		{
			throw new MethodNotFoundException(ex);
		}
		catch (final NoSuchFieldException ex)
		{
			throw new FieldNotFoundException(ex);
		}
	}

	/**
	 * Registers constraint checks for the given field 
	 *  
	 * @param field
	 * @param checks
	 * @throws IllegalArgumentException if <code>field == null</code> or <code>checks == null</code> or checks is empty 
	 */
	public void addChecks(final Field field, final Check... checks) throws IllegalArgumentException
	{
		if (field == null) throw new IllegalArgumentException("field cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(field.getDeclaringClass());

		cc.addFieldChecks(field, checks);
	}

	/**
	 * Registers constraint checks for the given getter's return value
	 * 
	 * @param invariantMethod a non-void, non-parameterized method (usually a JavaBean Getter style method)
	 * @param checks
	 * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code>
	 * @throws InvalidConfigurationException if getter is not a getter method
	 */
	public void addChecks(final Method invariantMethod, final Check... checks)
			throws IllegalArgumentException, InvalidConfigurationException
	{
		if (invariantMethod == null)
			throw new IllegalArgumentException("inVariantMethod cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(invariantMethod.getDeclaringClass());
		cc.addMethodReturnValueChecks(invariantMethod, Boolean.TRUE, checks);
	}

	/**
	 * Registers a new constraint set.
	 * 
	 * @param constraintSet cannot be null
	 * @param overwrite
	 * @throws ConstraintSetAlreadyDefinedException if <code>overwrite == false</code> and a constraint set with the given id exists already 
	 * @throws IllegalArgumentException if <code>constraintSet == null</code> or <code>constraintSet.id == null</code> or <code>constraintSet.id.length == 0</code>
	 * @throws IllegalArgumentException if <code>constraintSet.id == null</code>
	 */
	public void addConstraintSet(final ConstraintSet constraintSet, boolean overwrite)
			throws ConstraintSetAlreadyDefinedException, IllegalArgumentException
	{
		if (constraintSet == null)
			throw new IllegalArgumentException("constraintSet cannot be null");

		if (constraintSet.getId() == null)
			throw new IllegalArgumentException("constraintSet.id cannot be null");

		if (constraintSet.getId().length() == 0)
			throw new IllegalArgumentException("constraintSet.id cannot be empty");

		synchronized (constraintSetsById)
		{
			if (!overwrite && constraintSetsById.containsKey(constraintSet.getId()))
				throw new ConstraintSetAlreadyDefinedException(constraintSet.getId());

			constraintSetsById.put(constraintSet.getId(), constraintSet);
		}
	}

	/**
	 * 
	 * @param languageId
	 * @param expressionLanguage
	 * @throws IllegalArgumentException if <code>languageId == null || expressionLanguage == null</code>
	 */
	public void addExpressionLanguage(final String languageId,
			final ExpressionLanguage expressionLanguage) throws IllegalArgumentException
	{
		if (languageId == null) throw new IllegalArgumentException("languageId cannot be null");
		if (expressionLanguage == null)
			throw new IllegalArgumentException("expressionLanguage cannot be null");

		if (LOG.isInfo())
			LOG.info("Expression language '" + languageId + "' registered: " + expressionLanguage);

		expressionLanguages.put(languageId, expressionLanguage);
	}

	/**
	 * validates the field and getter constrains of the given object
	 * and throws an ConstraintsViolatedException if any constraint
	 * violations are detected
	 * 
	 * @param validatedObject the object to validate, cannot be null
	 * @throws ConstraintsViolatedException
	 * @throws ValidationFailedException
	 * @throws IllegalArgumentException if <code>validatedObject == null</code>
	 */
	public void assertValid(final Object validatedObject) throws IllegalArgumentException,
			ValidationFailedException, ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validate(validatedObject);

		if (violations.size() > 0)
			throw translateException(new ConstraintsViolatedException(violations));
	}

	/**
	 * Validates the give value against the defined field constraints and throws 
	 * an ConstraintsViolatedException if any constraint violations are detected.<br>
	 * 
	 * @param validatedObject the object to validate, cannot be null
	 * @param validatedField the field to validate, cannot be null
	 * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>field == null</code>
	 * @throws ConstraintsViolatedException
	 * @throws ValidationFailedException 
	 */
	public void assertValidFieldValue(final Object validatedObject, final Field validatedField,
			final Object fieldValueToValidate) throws IllegalArgumentException,
			ValidationFailedException, ConstraintsViolatedException
	{
		final List<ConstraintViolation> violations = validateFieldValue(validatedObject,
				validatedField, fieldValueToValidate);

		if (violations.size() > 0)
			throw translateException(new ConstraintsViolatedException(violations));
	}

	protected void checkConstraint(final List<ConstraintViolation> violations, final Check check,
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
		 * standard constraints handling
		 */
		if (!check.isSatisfied(validatedObject, valueToValidate, context, this))
		{
			final String errorMessage = renderMessage(context, valueToValidate, check.getMessage(),
					check.getMessageVariables());
			violations.add(new ConstraintViolation(check.getErrorCode(), errorMessage, check
					.getSeverity(), validatedObject, valueToValidate, context));
		}
	}

	protected void checkConstraintAssertConstraintSet(final List<ConstraintViolation> violations,
			final AssertConstraintSetCheck check, final Object validatedObject,
			final Object valueToValidate, final OValContext context) throws OValException
	{
		final ConstraintSet cs = getConstraintSet(check.getId());

		if (cs == null) throw new UndefinedConstraintSetException(check.getId());

		final Collection<Check> referencedChecks = cs.getChecks();

		if (referencedChecks != null && referencedChecks.size() > 0)
			for (final Check referencedCheck : referencedChecks)
			{
				checkConstraint(violations, referencedCheck, validatedObject, valueToValidate,
						context);
			}
	}

	protected void checkConstraintAssertFieldConstraints(
			final List<ConstraintViolation> violations, final AssertFieldConstraintsCheck check,
			final Object validatedObject, final Object valueToValidate, final OValContext context)
			throws OValException
	{
		Class< ? > targetClass;

		/*
		 * set the targetClass based on the validation context
		 */
		if (context instanceof ConstructorParameterContext)
		{
			// the class declaring the field must either be the class declaring the constructor or one of its super
			// classes
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
		else if (check.getDeclaringClass() != null && check.getDeclaringClass() != Void.class)
		{
			targetClass = check.getDeclaringClass();
		}
		else
		{
			// the lowest class that is expected to declare the field (or one of its super classes)
			targetClass = validatedObject.getClass();

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
				fieldName = ReflectionUtils.guessFieldName(((MethodReturnValueContext) context)
						.getMethod());
			}
		}

		/*
		 * find the field based on fieldName and targetClass
		 */
		final Field field = ReflectionUtils.getFieldRecursive(targetClass, fieldName);

		if (field == null)
			throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <"
					+ targetClass + "> or its super classes.");

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

	protected void checkConstraintAssertValid(final List<ConstraintViolation> violations,
			final AssertValidCheck check, final Object validatedObject,
			final Object valueToValidate, final OValContext context) throws OValException
	{
		if (valueToValidate == null) return;

		// ignore circular dependencies
		if (isCurrentlyValidated(valueToValidate)) return;

		final List<ConstraintViolation> additionalViolations = validate(valueToValidate);

		if (additionalViolations.size() != 0)
		{
			final String errorMessage = renderMessage(context, valueToValidate, check.getMessage(),
					check.getMessageVariables());

			violations
					.add(new ConstraintViolation(check.getErrorCode(), errorMessage, check
							.getSeverity(), validatedObject, valueToValidate, context,
							additionalViolations));
		}

		// if the value to validate is a collection also validate the collection items
		if (valueToValidate instanceof Collection && check.isRequireValidElements())
		{
			for (final Object item : (Collection< ? >) valueToValidate)
			{
				checkConstraintAssertValid(violations, check, validatedObject, item, context);
			}
		}

		// if the value to validate is a map also validate the map keys and values
		else if (valueToValidate instanceof Map && check.isRequireValidElements())
		{
			for (final Object item : ((Map< ? , ? >) valueToValidate).keySet())
			{
				checkConstraintAssertValid(violations, check, validatedObject, item, context);
			}

			for (final Object item : ((Map< ? , ? >) valueToValidate).values())
			{
				checkConstraintAssertValid(violations, check, validatedObject, item, context);
			}
		}

		// if the value to validate is an array also validate the array elements
		else if (valueToValidate.getClass().isArray())
		{
			for (final Object item : (Object[]) valueToValidate)
			{
				checkConstraintAssertValid(violations, check, validatedObject, item, context);
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
	 * Gets the object-level constraint checks for the given class 
	 *  
	 * @param clazz
	 * @throws IllegalArgumentException if <code>clazz == null</code> 
	 */
	public Check[] getChecks(final Class< ? > clazz) throws IllegalArgumentException
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");

		final ClassChecks cc = getClassChecks(clazz);

		final Set<Check> checks = cc.checksForObject;
		return checks == null ? null : checks.toArray(new Check[checks.size()]);
	}

	/**
	 * Gets the constraint checks for the given field 
	 *  
	 * @param field
	 * @throws IllegalArgumentException if <code>field == null</code> 
	 */
	public Check[] getChecks(final Field field) throws IllegalArgumentException
	{
		if (field == null) throw new IllegalArgumentException("field cannot be null");

		final ClassChecks cc = getClassChecks(field.getDeclaringClass());

		final Set<Check> checks = cc.checksForFields.get(field);
		return checks == null ? null : checks.toArray(new Check[checks.size()]);
	}

	/**
	 * Gets the constraint checks for the given method's return value
	 *  
	 * @param method
	 * @throws IllegalArgumentException if <code>getter == null</code>
	 */
	public Check[] getChecks(final Method method) throws IllegalArgumentException
	{
		if (method == null) throw new IllegalArgumentException("method cannot be null");

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final Set<Check> checks = cc.checksForMethodReturnValues.get(method);
		return checks == null ? null : checks.toArray(new Check[checks.size()]);
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
	protected ClassChecks getClassChecks(final Class< ? > clazz) throws IllegalArgumentException,
			OValException
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");

		synchronized (checksByClass)
		{
			ClassChecks cc = checksByClass.get(clazz);

			if (cc == null)
			{
				cc = new ClassChecks(clazz);

				for (final Configurer configurer : configurers)
				{
					final ClassConfiguration classConfig = configurer.getClassConfiguration(clazz);
					if (classConfig != null) addChecks(cc, classConfig);
				}

				checksByClass.put(clazz, cc);
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

	public ConstraintSet getConstraintSet(final String constraintSetId) throws OValException
	{
		synchronized (constraintSetsById)
		{
			ConstraintSet cs = constraintSetsById.get(constraintSetId);

			if (cs == null)
			{
				for (final Configurer configurer : configurers)
				{
					final ConstraintSetConfiguration csc = configurer
							.getConstraintSetConfiguration(constraintSetId);
					if (csc != null)
					{
						cs = new ConstraintSet(csc.id);
						cs.setChecks(csc.checks);

						addConstraintSet(cs, csc.overwrite != null && csc.overwrite);
					}
				}
			}
			return cs;
		}
	}

	/**
	 * @return the exceptionProcessor
	 */
	public ExceptionTranslator getExceptionTranslator()
	{
		return exceptionTranslator;
	}

	/**
	 * 
	 * @param languageId the id of the language, cannot be null
	 * 
	 * @throws IllegalArgumentException if <code>languageName == null</code>
	 * @throws ExpressionLanguageNotAvailableException
	 */
	public ExpressionLanguage getExpressionLanguage(final String languageId)
			throws IllegalArgumentException, ExpressionLanguageNotAvailableException
	{
		if (languageId == null) throw new IllegalArgumentException("languageId cannot be null");

		final ExpressionLanguage el = expressionLanguages.get(languageId);

		if (el == null) throw new ExpressionLanguageNotAvailableException(languageId);

		return el;
	}

	private void initializeDefaultELs()
	{
		// JavaScript support
		if (ReflectionUtils.isClassPresent("org.mozilla.javascript.Context"))
		{
			addExpressionLanguage("javascript", new ExpressionLanguageJavaScriptImpl());
			addExpressionLanguage("js", getExpressionLanguage("javascript"));
		}

		// Groovy support
		if (ReflectionUtils.isClassPresent("groovy.lang.Binding"))
		{
			addExpressionLanguage("groovy", new ExpressionLanguageGroovyImpl());
		}

		// BeanShell support
		if (ReflectionUtils.isClassPresent("bsh.Interpreter"))
		{
			addExpressionLanguage("bsh", new ExpressionLanguageBeanShellImpl());
			addExpressionLanguage("beanshell", getExpressionLanguage("bsh"));
		}

		// OGNL support
		if (ReflectionUtils.isClassPresent("ognl.Ognl"))
		{
			addExpressionLanguage("ognl", new ExpressionLanguageOGNLImpl());
		}

		// MVEL support
		if (ReflectionUtils.isClassPresent("org.mvel.MVEL"))
		{
			addExpressionLanguage("mvel", new ExpressionLanguageMVELImpl());
		}

		// JRuby support
		if (ReflectionUtils.isClassPresent("org.jruby.Ruby"))
		{
			addExpressionLanguage("ruby", new ExpressionLanguageJRubyImpl());
			addExpressionLanguage("jruby", getExpressionLanguage("ruby"));
		}

		// JEXL support
		if (ReflectionUtils.isClassPresent("org.apache.commons.jexl.ExpressionFactory"))
		{
			addExpressionLanguage("jexl", new ExpressionLanguageJEXLImpl());
		}
	}

	/**
	 * Determines if at least one of the given profiles is enabled
	 * 
	 * @param profileIds
	 * @return Returns true if at least one of the given profiles is enabled. 
	 */
	protected boolean isAnyProfileEnabled(final String[] profileIds)
	{
		if (profileIds == null || profileIds.length == 0) return isAllProfilesEnabledByDefault;

		for (final String profile : profileIds)
		{
			if (isProfileEnabled(profile)) return true;
		}
		return false;
	}

	/**
	 * Determines if the given object is currently validated in the current thread
	 * 
	 * @param object
	 * @return Returns true if the given object is currently validated in the current thread.
	 */
	protected boolean isCurrentlyValidated(final Object object)
	{
		return currentlyValidatedObjects.get().contains(object);
	}

	/**
	 * Determines if the given profile is enabled.
	 * 
	 * @param profileId
	 * @return Returns true if the given profile is enabled.
	 */
	public boolean isProfileEnabled(final String profileId)
	{
		if (isProfilesFeatureUsed)
		{
			if (isAllProfilesEnabledByDefault) return !disabledProfiles.contains(profileId);

			return enabledProfiles.contains(profileId);
		}
		return true;
	}

	/**
	 * clears the checks and constraint sets => a reconfiguration using the
	 * currently registered configurers will automatically happen
	 */
	public void reconfigureChecks()
	{
		synchronized (checksByClass)
		{
			checksByClass.clear();
		}
		synchronized (constraintSetsById)
		{
			constraintSetsById.clear();
		}
	}

	/**
	 * Removes object-level constraint checks 
	 *  
	 * @param clazz
	 * @param checks
	 * @throws IllegalArgumentException if <code>clazz == null</code> or <code>checks == null</code> or checks is empty 
	 */
	public void removeChecks(final Class< ? > clazz, final Check... checks)
			throws IllegalArgumentException
	{
		if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(clazz);
		cc.removeObjectChecks(checks);
	}

	/**
	 * Removes constraint checks for the given field 
	 *  
	 * @param field
	 * @param checks
	 * @throws IllegalArgumentException if <code>field == null</code> or <code>checks == null</code> or checks is empty 
	 */
	public void removeChecks(final Field field, final Check... checks)
			throws IllegalArgumentException
	{
		if (field == null) throw new IllegalArgumentException("field cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(field.getDeclaringClass());
		cc.removeFieldChecks(field, checks);
	}

	/**
	 * Removes constraint checks for the given getter's return value
	 * 
	 * @param getter a JavaBean Getter style method
	 * @param checks
	 * @throws IllegalArgumentException if <code>getter == null</code> or <code>checks == null</code>
	 */
	public void removeChecks(final Method getter, final Check... checks)
			throws IllegalArgumentException
	{
		if (getter == null) throw new IllegalArgumentException("field cannot be null");
		if (checks == null) throw new IllegalArgumentException("checks cannot be null");
		if (checks.length == 0) throw new IllegalArgumentException("checks cannot empty");

		final ClassChecks cc = getClassChecks(getter.getDeclaringClass());
		cc.removeMethodChecks(getter, checks);
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
		synchronized (constraintSetsById)
		{
			return constraintSetsById.remove(id);
		}
	}

	protected String renderMessage(final OValContext context, final Object value,
			final String messageKey, final Map<String, String> messageValues)
	{
		String message = MessageRenderer.renderMessage(messageKey, messageValues);

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		message = StringUtils.replaceAll(message, "{context}", context.toString());
		message = StringUtils.replaceAll(message, "{invalidValue}", value == null ? "null" : value
				.toString());

		return message;
	}

	/**
	 * @param exceptionTranslator the exceptionTranslator to set
	 */
	public void setExceptionTranslator(final ExceptionTranslator exceptionTranslator)
	{
		this.exceptionTranslator = exceptionTranslator;
	}

	protected RuntimeException translateException(final OValException ex)
	{
		if (exceptionTranslator != null)
		{
			final RuntimeException rex = exceptionTranslator.translateException(ex);
			if (rex != null) return rex;
		}
		return ex;
	}

	/**
	 * validates the field and getter constrains of the given object
	 * 
	 * @param validatedObject the object to validate, cannot be null
	 * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
	 * @throws ValidationFailedException
	 * @throws IllegalArgumentException if <code>validatedObject == null</code>
	 */
	public List<ConstraintViolation> validate(final Object validatedObject)
			throws IllegalArgumentException, ValidationFailedException
	{
		if (validatedObject == null)
			throw new IllegalArgumentException("validatedObject cannot be null");

		final List<ConstraintViolation> violations = CollectionFactoryHolder.getFactory()
				.createList();

		validateInvariants(validatedObject, violations);
		return violations;
	}

	/**
	 * Validates the give value against the defined field constraints.<br>
	 * 
	 * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
	 * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>field == null</code>
	 * @throws ValidationFailedException 
	 */
	public List<ConstraintViolation> validateFieldValue(final Object validatedObject,
			final Field validatedField, final Object fieldValueToValidate)
			throws IllegalArgumentException, ValidationFailedException
	{
		if (validatedObject == null)
			throw new IllegalArgumentException("validatedObject cannot be null");

		if (validatedField == null) throw new IllegalArgumentException("field cannot be null");

		try
		{
			final ClassChecks cc = getClassChecks(validatedField.getDeclaringClass());
			final Collection<Check> checks = cc.checksForFields.get(validatedField);

			final List<ConstraintViolation> violations = CollectionFactoryHolder.getFactory()
					.createList();

			if (checks == null || checks.size() == 0) return violations;

			final FieldContext context = new FieldContext(validatedField);

			for (final Check check : checks)
			{
				checkConstraint(violations, check, validatedObject, fieldValueToValidate, context);
			}
			return violations;
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException("Field validation failed. Field: " + validatedField
					+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * validates the field and getter constrains of the given object.
	 * if the given object is a class the static fields and getters
	 * are validated.
	 * 
	 * @param validatedObject the object to validate, cannot be null
	 * @throws ValidationFailedException
	 * @throws IllegalArgumentException if <code>validatedObject == null</code>
	 */
	protected void validateInvariants(final Object validatedObject,
			final List<ConstraintViolation> violations) throws IllegalArgumentException,
			ValidationFailedException
	{
		if (validatedObject == null)
			throw new IllegalArgumentException("validatedObject cannot be null");

		currentlyValidatedObjects.get().add(validatedObject);
		try
		{
			if (validatedObject instanceof Class)
				validateStaticInvariants((Class< ? >) validatedObject, violations);
			else
				validateObjectInvariants(validatedObject, validatedObject.getClass(), violations);
		}
		finally
		{
			currentlyValidatedObjects.get().remove(validatedObject);
		}
	}

	/**
	 * validate validatedObject based on the constraints of the given clazz 
	 */
	private void validateObjectInvariants(final Object validatedObject, final Class< ? > clazz,
			final List<ConstraintViolation> violations) throws ValidationFailedException
	{
		assert validatedObject != null;
		assert clazz != null;
		assert violations != null;

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
					final FieldContext ctx = new FieldContext(field);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate, ctx);
					}
				}
			}

			// validate constraints on getter methods
			for (final Method getter : cc.constrainedMethods)
			{
				final Collection<Check> checks = cc.checksForMethodReturnValues.get(getter);

				if (checks != null && checks.size() > 0)
				{
					final Object valueToValidate = ReflectionUtils.invokeMethod(getter,
							validatedObject);
					final MethodReturnValueContext ctx = new MethodReturnValueContext(getter);

					for (final Check check : checks)
					{
						checkConstraint(violations, check, validatedObject, valueToValidate, ctx);
					}
				}
			}

			// validate object constraints
			if (cc.checksForObject.size() > 0)
			{
				final ClassContext ctx = new ClassContext(clazz);
				for (final Check check : cc.checksForObject)
				{
					checkConstraint(violations, check, validatedObject, validatedObject, ctx);
				}
			}

			// if the super class is annotated to be validatable also validate it against the object
			validateObjectInvariants(validatedObject, clazz.getSuperclass(), violations);
		}
		catch (final OValException ex)
		{
			throw new ValidationFailedException("Object validation failed. Class: " + clazz
					+ " Validated object: " + validatedObject, ex);
		}
	}

	/**
	 * Validates the static field and static getter constrains of the given class.
	 * Constraints specified for super classes are not taken in account.
	 */
	private void validateStaticInvariants(final Class< ? > validatedClass,
			final List<ConstraintViolation> violations) throws ValidationFailedException
	{
		assert validatedClass != null;
		assert violations != null;

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
		for (final Method getter : cc.constrainedStaticMethods)
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
	}
}
