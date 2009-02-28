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
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.annotation.Constraint;

/**
 * Check that the value is not a reference to the validated object itself.<br>
 * This is e.g. useful to avoid circular references.<br>
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(checkWith = NoSelfReferenceCheck.class)
public @interface NoSelfReference
{
	/**
	 * error code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraint.NoSelfReference";

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraint.NotSelfRef.violated";

	/**
	 * severity passed to the ConstraintViolation object
	 */
	int severity() default 0;

	/**
	 * The associated constraint profiles.
	 */
	String[] profiles() default {};
}
