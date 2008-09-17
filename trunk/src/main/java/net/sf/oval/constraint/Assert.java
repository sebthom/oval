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
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.annotation.Constraint;

/**
 * Check if evaluating the expression in the specified expression language returns true.
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Constraint(checkWith = AssertCheck.class)
public @interface Assert
{

	/**
	 * failure code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraint.Assert";

	/**
	 * Formula in the given expression language describing the constraint.
	 * The formula must return <code>true</code> if the constraint is satisfied.
	 * <br>
	 * available variables are:<br>
	 * <b>_this</b> -&gt; the validated bean<br>
	 * <b>_value</b> -&gt; the value to validate (e.g. the field value, parameter value, method return value,
	 *    or the validated bean for object level constraints)
	 */
	String expr();

	/**
	 * the expression language that is used, e.g. "bsh" / "beanshell", "groovy", or "js" / "javascript".
	 */
	String lang();

	/**
	 * message to be used for constructing the ConstraintViolation object
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraint.Assert.violated";

	/**
	 * The associated constraint profiles.
	 */
	String[] profiles() default {};

	/**
	 * severity passed to the ConstraintViolation object
	 */
	int severity() default 0;
}
