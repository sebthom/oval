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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sebastian Thomschke
 */
public abstract class AbstractAnnotationCheck<ConstraintAnnotation extends Annotation>
		implements
			AnnotationCheck<ConstraintAnnotation>
{
	private final static Logger LOG = Logger.getLogger(AbstractAnnotationCheck.class.getName());

	protected ConstraintAnnotation constraintAnnotation;
	protected String message;

	public void configure(final ConstraintAnnotation constraintAnnotation)
	{
		this.constraintAnnotation = constraintAnnotation;

		/*
		 * Retrieve the message value from the constraint annotation via reflection.
		 * Using reflection is required because annotations do not support inheritance and 
		 * therefore cannot implement an interface that could be used for a down cast here.
		 */
		final Class< ? > constraintClazz = constraintAnnotation.getClass();
		try
		{
			final Method getMessage = constraintClazz.getDeclaredMethod("message",
					(Class< ? >[]) null);
			message = (String) getMessage.invoke(constraintAnnotation, (Object[]) null);
		}
		catch (Exception e)
		{
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE,
						"Cannot determine constraint error message based on annotation "
								+ constraintClazz.getName(), e);
		}
		if (message == null) message = constraintClazz.getName() + ".violated";
	}

	public ConstraintAnnotation getConstraintAnnotation()
	{
		return constraintAnnotation;
	}

	public String getMessage()
	{
		/*
		 * if the message has not been initialized (which might be the case when using XML configuration),
		 * construct the string based on this class' name minus the appendix "Check" plus the appendix ".violated"
		 */
		if (message == null)
		{
			final String className = getClass().getName();
			if (className.endsWith("Check"))
				message = className.substring(0, getClass().getName().length() - 5) + ".violated";
			else
				message = className + ".violated";
		}
		return message;
	}

	public String[] getMessageValues()
	{
		return null;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}
}
