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

import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.ReflectionException;

/**
 * This is the default check loader. It tries to instantiate a check having the same package and class name
 * as the constraint annotation appended with "Check". The check class is loaded via the same class loader as
 * the constraint annotation.
 *  
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public class AnnotationCheckLoaderImpl implements AnnotationCheckLoader
{
	public <Constraint extends Annotation> AnnotationCheck<Constraint> loadCheck(
			Constraint constraint, OValContext context)
	{
		final Class constraintClass = constraint.annotationType();

		/*
		 * try to load a corresponding check class that is in the same package as the
		 * constraint annotation and has the name "<ConstraintAnnotationName>Check"
		 */
		final String checkName = constraintClass.getName() + "Check";
		try
		{
			// use the same class loader to load the check class that loaded the annotation
			final Class checkClass = constraintClass.getClassLoader().loadClass(checkName);

			// instantiate the appropriate check for the found constraint
			@SuppressWarnings("unchecked")
			AnnotationCheck<Constraint> check = (AnnotationCheck<Constraint>) checkClass
					.newInstance();
			check.configure(constraint);
			return check;
		}
		catch (final ClassNotFoundException e)
		{
			throw new ReflectionException("Cannot load check " + checkName, e);
		}
		catch (final InstantiationException e)
		{
			throw new ReflectionException("Cannot load check " + checkName, e);
		}
		catch (final IllegalAccessException e)
		{
			throw new ReflectionException("Cannot load check " + checkName, e);
		}
		catch (final SecurityException e)
		{
			throw new ReflectionException("Cannot load check " + checkName, e);
		}
	}
}
