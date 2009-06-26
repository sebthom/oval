/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
public abstract class AbstractAnnotationCheck<ConstraintAnnotation extends Annotation> extends AbstractCheck
		implements
			AnnotationCheck<ConstraintAnnotation>
{
	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.getLog(AbstractAnnotationCheck.class);

	/**
	 * {@inheritDoc}
	 */
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
			final Method getMessage = constraintClazz.getDeclaredMethod("message", (Class< ? >[]) null);
			setMessage((String) getMessage.invoke(constraintAnnotation, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG
					.debug("Cannot determine constraint error message based on annotation {1}", constraintClazz
							.getName(), e);
			try
			{
				setMessage(constraintClazz.getName() + ".violated");
			}
			catch (final UnsupportedOperationException ex)
			{
				// ignore
			}
		}

		/*
		 * Retrieve the error code value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getErrorCode = constraintClazz.getDeclaredMethod("errorCode", (Class< ? >[]) null);
			setErrorCode((String) getErrorCode.invoke(constraintAnnotation, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint error code based on annotation {1}", constraintClazz.getName(), e);
			try
			{
				setErrorCode(constraintClazz.getName());
			}
			catch (final UnsupportedOperationException ex)
			{
				// ignore
			}
		}

		/*
		 * Retrieve the severity value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getSeverity = constraintClazz.getDeclaredMethod("severity", (Class< ? >[]) null);
			setSeverity(((Number) getSeverity.invoke(constraintAnnotation, (Object[]) null)).intValue());
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint severity based on annotation {1}", constraintClazz.getName(), e);
		}

		/*
		 * Retrieve the profiles value from the constraint annotation via reflection.
		 */
		try
		{
			final Method getProfiles = constraintClazz.getDeclaredMethod("profiles", (Class< ? >[]) null);
			setProfiles((String[]) getProfiles.invoke(constraintAnnotation, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint profiles based on annotation {1}", constraintClazz.getName(), e);
		}

		/*
		 * Retrieve the when formula from the constraint annotation via reflection.
		 */
		try
		{
			final Method getWhen = constraintClazz.getDeclaredMethod("when", (Class< ? >[]) null);
			setWhen((String) getWhen.invoke(constraintAnnotation, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint when formula based on annotation {1}", constraintClazz.getName(), e);
		}
	}
}
