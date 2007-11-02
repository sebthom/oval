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
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.constraint.CheckWithCheck.SimpleCheck;

/**
 * Check the value by a method of the same class that takes the value as argument and returns true if valid
 * and false if invalid<br>
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Constraint(checkWith = CheckWithCheck.class)
public @interface CheckWith
{
	/**
	 * error code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraints.CheckWith";

	/**
	 * this constraint will be ignored if the value to check is null
	 */
	boolean ignoreIfNull() default true;

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraints.CheckWith.violated";

	/**
	 * severity passed to the ConstraintViolation object
	 */
	int severity() default 0;

	/**
	 * The associated validation profiles.
	 */
	String[] profiles() default {};

	/**
	 * Check class to use for validation. If this class is an inner class
	 * it needs be declared as a <b>static</b> class. Otherwise
	 * check instantiation will fail.
	 * 
	 * @see net.sf.oval.constraint.CheckWithCheck.SimpleCheck
	 */
	Class< ? extends SimpleCheck> value();
}
