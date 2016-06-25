/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval.configuration.annotation;

import static net.sf.oval.Validator.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

import net.sf.oval.Check;
import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.constraint.AssertFalseCheck;
import net.sf.oval.constraint.AssertNullCheck;
import net.sf.oval.constraint.AssertTrueCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.DigitsCheck;
import net.sf.oval.constraint.FutureCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.MaxCheck;
import net.sf.oval.constraint.MinCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.PastCheck;
import net.sf.oval.constraint.SizeCheck;
import net.sf.oval.guard.Guarded;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Constraints configurer that interprets the JSR303 built-in Java Bean Validation annotations:
 * <ul>
 * <li>javax.validation.constraints.AssertFalse => net.sf.oval.constraint.AssertFalseCheck
 * <li>javax.validation.constraints.AssertTrue => net.sf.oval.constraint.AssertTrueCheck
 * <li>javax.validation.constraints.DecimalMax => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.DecimalMin => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.Digits => net.sf.oval.constraint.DigitsCheck
 * <li>javax.validation.constraints.Future => net.sf.oval.constraint.FutureCheck
 * <li>javax.validation.constraints.Max => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.Min => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.NotNull => net.sf.oval.constraint.NotNullCheck
 * <li>javax.validation.constraints.Null => net.sf.oval.constraint.AssertNullCheck
 * <li>javax.validation.constraints.Past => net.sf.oval.constraint.PastCheck
 * <li>javax.validation.constraints.Pattern => net.sf.oval.constraint.PatternCheck
 * <li>javax.validation.constraints.Size => net.sf.oval.constraint.SizeCheck
 * <li>javax.validation.Valid => net.sf.oval.constraint.AssertValidCheck
 * </ul>
 *
 * @author Sebastian Thomschke
 */
public class BeanValidationAnnotationsConfigurer implements Configurer {
    private static final Log LOG = Log.getLog(BeanValidationAnnotationsConfigurer.class);

    private List<ParameterConfiguration> _createParameterConfiguration(final Annotation[][] paramAnnotations, final Class<?>[] parameterTypes) {
        final CollectionFactory cf = getCollectionFactory();

        final List<ParameterConfiguration> paramCfg = cf.createList(paramAnnotations.length);

        List<Check> paramChecks = cf.createList(2);

        // loop over all parameters of the current constructor
        for (int i = 0; i < paramAnnotations.length; i++) {
            // loop over all annotations of the current constructor parameter
            for (final Annotation annotation : paramAnnotations[i]) {
                initializeChecks(annotation, paramChecks);
            }

            final ParameterConfiguration pc = new ParameterConfiguration();
            paramCfg.add(pc);
            pc.type = parameterTypes[i];
            if (paramChecks.size() > 0) {
                pc.checks = paramChecks;
                paramChecks = cf.createList(2); // create a new list for the next parameter having checks
            }
        }
        return paramCfg;
    }

    protected void configureConstructorParameterChecks(final ClassConfiguration classCfg) {
        final CollectionFactory cf = getCollectionFactory();

        for (final Constructor<?> ctor : classCfg.type.getDeclaredConstructors()) {
            final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(ctor.getParameterAnnotations(), ctor.getParameterTypes());

            if (paramCfg.size() > 0) {
                if (classCfg.constructorConfigurations == null) {
                    classCfg.constructorConfigurations = cf.createSet(2);
                }

                final ConstructorConfiguration cc = new ConstructorConfiguration();
                cc.parameterConfigurations = paramCfg;
                cc.postCheckInvariants = false;
                classCfg.constructorConfigurations.add(cc);
            }
        }
    }

    protected void configureFieldChecks(final ClassConfiguration classCfg) {
        final CollectionFactory cf = getCollectionFactory();

        List<Check> checks = cf.createList(2);

        for (final Field field : classCfg.type.getDeclaredFields()) {
            // loop over all annotations of the current field
            for (final Annotation annotation : field.getAnnotations()) {
                initializeChecks(annotation, checks);
            }

            if (checks.size() > 0) {
                if (classCfg.fieldConfigurations == null) {
                    classCfg.fieldConfigurations = cf.createSet(2);
                }

                final FieldConfiguration fc = new FieldConfiguration();
                fc.name = field.getName();
                fc.checks = checks;
                classCfg.fieldConfigurations.add(fc);
                checks = cf.createList(2); // create a new list for the next field with checks
            }
        }
    }

    /**
     * configure method return value and parameter checks
     */
    protected void configureMethodChecks(final ClassConfiguration classCfg) {
        final CollectionFactory cf = getCollectionFactory();

        List<Check> returnValueChecks = cf.createList(2);

        for (final Method method : classCfg.type.getDeclaredMethods()) {
            // loop over all annotations
            for (final Annotation annotation : ReflectionUtils.getAnnotations(method, Boolean.TRUE.equals(classCfg.inspectInterfaces))) {
                initializeChecks(annotation, returnValueChecks);
            }

            /*
             * determine parameter checks
             */
            final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(ReflectionUtils.getParameterAnnotations(method, Boolean.TRUE.equals(
                classCfg.inspectInterfaces)), method.getParameterTypes());

            // check if anything has been configured for this method at all
            if (paramCfg.size() > 0 || returnValueChecks.size() > 0) {
                if (classCfg.methodConfigurations == null) {
                    classCfg.methodConfigurations = cf.createSet(2);
                }

                final MethodConfiguration mc = new MethodConfiguration();
                mc.name = method.getName();
                mc.parameterConfigurations = paramCfg;
                mc.isInvariant = ReflectionUtils.isGetter(method);
                if (returnValueChecks.size() > 0) {
                    mc.returnValueConfiguration = new MethodReturnValueConfiguration();
                    mc.returnValueConfiguration.checks = returnValueChecks;
                    returnValueChecks = cf.createList(2); // create a new list for the next method having return value checks
                }
                classCfg.methodConfigurations.add(mc);
            }
        }
    }

    public ClassConfiguration getClassConfiguration(final Class<?> clazz) {
        final ClassConfiguration classCfg = new ClassConfiguration();
        classCfg.type = clazz;

        final Guarded guarded = clazz.getAnnotation(Guarded.class);

        if (guarded == null) {
            classCfg.applyFieldConstraintsToConstructors = false;
            classCfg.applyFieldConstraintsToSetters = false;
            classCfg.assertParametersNotNull = false;
            classCfg.checkInvariants = false;
            classCfg.inspectInterfaces = false;
        } else {
            classCfg.applyFieldConstraintsToConstructors = guarded.applyFieldConstraintsToConstructors();
            classCfg.applyFieldConstraintsToSetters = guarded.applyFieldConstraintsToSetters();
            classCfg.assertParametersNotNull = guarded.assertParametersNotNull();
            classCfg.checkInvariants = guarded.checkInvariants();
            classCfg.inspectInterfaces = guarded.inspectInterfaces();
        }

        configureFieldChecks(classCfg);
        configureConstructorParameterChecks(classCfg);
        configureMethodChecks(classCfg);

        return classCfg;
    }

    public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId) {
        return null;
    }

    protected void initializeChecks(final Annotation annotation, final Collection<Check> checks) {
        assert annotation != null;
        assert checks != null;

        // ignore non-bean validation annotations
        if (!(annotation instanceof Valid) && annotation.annotationType().getAnnotation(javax.validation.Constraint.class) == null)
            return;

        Class<?>[] groups = null;
        Check check = null;
        if (annotation instanceof NotNull) {
            groups = ((NotNull) annotation).groups();
            check = new NotNullCheck();
        } else if (annotation instanceof Null) {
            groups = ((Null) annotation).groups();
            check = new AssertNullCheck();
        } else if (annotation instanceof Valid) {
            check = new AssertValidCheck();
        } else if (annotation instanceof AssertTrue) {
            groups = ((AssertTrue) annotation).groups();
            check = new AssertTrueCheck();
        } else if (annotation instanceof AssertFalse) {
            groups = ((AssertFalse) annotation).groups();
            check = new AssertFalseCheck();
        } else if (annotation instanceof DecimalMax) {
            groups = ((DecimalMax) annotation).groups();
            final MaxCheck maxCheck = new MaxCheck();
            maxCheck.setMax(Double.parseDouble(((DecimalMax) annotation).value()));
            check = maxCheck;
        } else if (annotation instanceof DecimalMin) {
            groups = ((DecimalMin) annotation).groups();
            final MinCheck minCheck = new MinCheck();
            minCheck.setMin(Double.parseDouble(((DecimalMin) annotation).value()));
            check = minCheck;
        } else if (annotation instanceof Max) {
            groups = ((Max) annotation).groups();
            final MaxCheck maxCheck = new MaxCheck();
            maxCheck.setMax(((Max) annotation).value());
            check = maxCheck;
        } else if (annotation instanceof Min) {
            groups = ((Min) annotation).groups();
            final MinCheck minCheck = new MinCheck();
            minCheck.setMin(((Min) annotation).value());
            check = minCheck;
        } else if (annotation instanceof Future) {
            groups = ((Future) annotation).groups();
            check = new FutureCheck();
        } else if (annotation instanceof Past) {
            groups = ((Past) annotation).groups();
            check = new PastCheck();
        } else if (annotation instanceof Pattern) {
            groups = ((Pattern) annotation).groups();
            final MatchPatternCheck matchPatternCheck = new MatchPatternCheck();
            int iflag = 0;
            for (final Flag flag : ((Pattern) annotation).flags()) {
                iflag = iflag | flag.getValue();
            }
            matchPatternCheck.setPattern(((Pattern) annotation).regexp(), iflag);
            check = matchPatternCheck;
        } else if (annotation instanceof Digits) {
            groups = ((Digits) annotation).groups();
            final DigitsCheck digitsCheck = new DigitsCheck();
            digitsCheck.setMaxFraction(((Digits) annotation).fraction());
            digitsCheck.setMaxInteger(((Digits) annotation).integer());
            check = digitsCheck;
        } else if (annotation instanceof Size) {
            groups = ((Size) annotation).groups();
            final SizeCheck sizeCheck = new SizeCheck();
            sizeCheck.setMax(((Size) annotation).max());
            sizeCheck.setMin(((Size) annotation).min());
            check = sizeCheck;
        }

        if (check != null) {
            final Method getMessage = ReflectionUtils.getMethod(annotation.getClass(), "message", (Class<?>[]) null);
            if (getMessage != null) {
                final String message = ReflectionUtils.invokeMethod(getMessage, annotation, (Object[]) null);
                if (message != null && !message.startsWith("{javax.validation.constraints.")) {
                    check.setMessage(message);
                }
            }

            if (groups != null && groups.length > 0) {
                final String[] profiles = new String[groups.length];
                for (int i = 0, l = groups.length; i < l; i++) {
                    profiles[i] = groups[i].getName();
                }
                check.setProfiles(profiles);
            }
            checks.add(check);
            return;
        }

        Annotation[] list = null;
        if (annotation instanceof AssertFalse.List) {
            list = ((AssertFalse.List) annotation).value();
        } else if (annotation instanceof AssertTrue.List) {
            list = ((AssertTrue.List) annotation).value();
        } else if (annotation instanceof DecimalMax.List) {
            list = ((DecimalMax.List) annotation).value();
        } else if (annotation instanceof DecimalMin.List) {
            list = ((DecimalMin.List) annotation).value();
        } else if (annotation instanceof Digits.List) {
            list = ((Digits.List) annotation).value();
        } else if (annotation instanceof Future.List) {
            list = ((Future.List) annotation).value();
        } else if (annotation instanceof Max.List) {
            list = ((Max.List) annotation).value();
        } else if (annotation instanceof Min.List) {
            list = ((Min.List) annotation).value();
        } else if (annotation instanceof NotNull.List) {
            list = ((NotNull.List) annotation).value();
        } else if (annotation instanceof Null.List) {
            list = ((Null.List) annotation).value();
        } else if (annotation instanceof Past.List) {
            list = ((Past.List) annotation).value();
        } else if (annotation instanceof Pattern.List) {
            list = ((Pattern.List) annotation).value();
        } else if (annotation instanceof Size.List) {
            list = ((Size.List) annotation).value();
        }

        if (list != null) {
            for (final Annotation anno : list) {
                initializeChecks(anno, checks);
            }
        } else {
            LOG.warn("Ignoring unsupported bean validation constraint annotation {1}", annotation);
            return;
        }
    }
}
