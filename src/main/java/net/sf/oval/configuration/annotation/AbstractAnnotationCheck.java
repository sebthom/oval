/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.oval.AbstractCheck;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Partial implementation of check classes configurable via annotations.
 *
 * @author Sebastian Thomschke
 */
public abstract class AbstractAnnotationCheck<ConstraintAnnotation extends Annotation> extends AbstractCheck implements AnnotationCheck<ConstraintAnnotation> {
    private static final long serialVersionUID = 1L;

    private static final Log LOG = Log.getLog(AbstractAnnotationCheck.class);

    @Override
    public void configure(final ConstraintAnnotation constraintAnnotation) {
        final Class<?> constraintClazz = constraintAnnotation.getClass();

        /*
         * Retrieve the message value from the constraint annotation via reflection.
         * Using reflection is required because annotations do not support inheritance and
         * therefore cannot implement an interface that could be used for a down cast here.
         */
        final Method getMessage = ReflectionUtils.getMethod(constraintClazz, "message", (Class<?>[]) null);
        if (getMessage == null) {
            LOG.debug("Cannot determine constraint error message based on annotation {1} since attribtue message() is not defined.", constraintClazz.getName());
        } else {
            try {
                setMessage((String) getMessage.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint error message based on annotation {1}", constraintClazz.getName(), ex);

                try {
                    setMessage(constraintClazz.getName() + ".violated");
                } catch (final UnsupportedOperationException uex) {
                    // ignore
                }
            }
        }

        /*
         * Retrieve the appliesTo value from the constraint annotation via reflection.
         */
        final Method getAppliesTo = ReflectionUtils.getMethod(constraintClazz, "appliesTo", (Class<?>[]) null);
        if (getAppliesTo == null) {
            LOG.debug("Cannot determine constraint targets based on annotation {1} since attribtue appliesTo() is not defined.", constraintClazz.getName());
        } else {
            try {
                setAppliesTo((ConstraintTarget[]) getAppliesTo.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint targets based on annotation {1}", constraintClazz.getName(), ex);
            }
        }

        /*
         * Retrieve the error code value from the constraint annotation via reflection.
         */
        final Method getErrorCode = ReflectionUtils.getMethod(constraintClazz, "errorCode", (Class<?>[]) null);
        if (getErrorCode == null) {
            LOG.debug("Cannot determine constraint error code based on annotation {1} since attribtue errorCode() is not defined.", constraintClazz.getName());
        } else {
            try {
                setErrorCode((String) getErrorCode.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint error code based on annotation {1}", constraintClazz.getName(), ex);
                try {
                    setErrorCode(constraintClazz.getName());
                } catch (final UnsupportedOperationException uex) {
                    // ignore
                }
            }
        }

        /*
         * Retrieve the severity value from the constraint annotation via reflection.
         */
        final Method getSeverity = ReflectionUtils.getMethod(constraintClazz, "severity", (Class<?>[]) null);
        if (getSeverity == null) {
            LOG.debug("Cannot determine constraint severity based on annotation {1} since attribtue severity() is not defined.", constraintClazz.getName());
        } else {
            try {
                setSeverity(((Number) getSeverity.invoke(constraintAnnotation, (Object[]) null)).intValue());
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint severity based on annotation {1}", constraintClazz.getName(), ex);
            }
        }

        /*
         * Retrieve the profiles value from the constraint annotation via reflection.
         */
        final Method getProfiles = ReflectionUtils.getMethod(constraintClazz, "profiles", (Class<?>[]) null);
        if (getProfiles == null) {
            LOG.debug("Cannot determine constraint profiles based on annotation {1} since attribtue profiles() is not defined.", constraintClazz.getName());
        } else {
            try {
                setProfiles((String[]) getProfiles.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint profiles based on annotation {1}", constraintClazz.getName(), ex);
            }
        }

        /*
         * Retrieve the profiles value from the constraint annotation via reflection.
         */
        final Method getTarget = ReflectionUtils.getMethod(constraintClazz, "target", (Class<?>[]) null);
        if (getTarget == null) {
            LOG.debug("Cannot determine constraint target based on annotation {1} since attribtue target() is not defined.", constraintClazz.getName());
        } else {
            try {
                setTarget((String) getTarget.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint target based on annotation {1}", constraintClazz.getName(), ex);
            }
        }

        /*
         * Retrieve the when formula from the constraint annotation via reflection.
         */
        final Method getWhen = ReflectionUtils.getMethod(constraintClazz, "when", (Class<?>[]) null);
        if (getWhen == null) {
            LOG.debug("Cannot determine constraint when formula based on annotation {1} since attribtue when() is not defined.", constraintClazz.getName());
        } else {
            try {
                setWhen((String) getWhen.invoke(constraintAnnotation, (Object[]) null));
            } catch (final Exception ex) {
                LOG.warn("Cannot determine constraint when formula based on annotation {1}", constraintClazz.getName(), ex);
            }
        }
    }
}
