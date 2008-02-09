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
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.oval.AbstractCheck;
import net.sf.oval.internal.Log;

/**
 * Partial implementation of check classes configurable via annotations.
 * 
 * @author Sebastian Thomschke
 */
public abstract class AbstractAnnotationCheck<ConstraintAnnotation extends Annotation>
		extends
			AbstractCheck implements AnnotationCheck<ConstraintAnnotation>
{
	private final static Log LOG = Log.getLog(AbstractAnnotationCheck.class);

	public void configure(final ConstraintAnnotation constraintAnnotation)
	{
		final Class< ? > constraintClazz = constraintAnnotation.getClass();

		/*
		 * Retrieve the message value from the constraint annotation via reflection.
		 * Using reflection is required because annotations do not support inheritance and 
		 * therefore cannot implement an interface that could be used for a down cast here.
		 */
		try
		{
			final Method getMessage = constraintClazz.getDeclaredMethod("message", (Class[]) null);
			message = (String) getMessage.invoke(constraintAnnotation, (Object[]) null);
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint error message based on annotation {}",
					constraintClazz.getName(), e);
			message = constraintClazz.getName() + ".violated";
		}

		/*
		 * Retrieve the error code value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getErrorCode = constraintClazz.getDeclaredMethod("errorCode",
					(Class[]) null);
			errorCode = (String) getErrorCode.invoke(constraintAnnotation, (Object[]) null);
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint error code based on annotation {}",
					constraintClazz.getName(), e);
			errorCode = constraintClazz.getName();
		}

		/*
		 * Retrieve the severity value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getSeverity = constraintClazz
					.getDeclaredMethod("severity", (Class[]) null);
			severity = ((Number) getSeverity.invoke(constraintAnnotation, (Object[]) null))
					.intValue();
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint severity based on annotation {}",
					constraintClazz.getName(), e);
		}

		/*
		 * Retrieve the profiles value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getProfiles = constraintClazz
					.getDeclaredMethod("profiles", (Class[]) null);
			profiles = (String[]) getProfiles.invoke(constraintAnnotation, (Object[]) null);
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint profiles based on annotation {}",
					constraintClazz.getName(), e);
		}
	}
}
