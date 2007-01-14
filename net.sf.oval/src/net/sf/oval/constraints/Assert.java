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
package net.sf.oval.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.annotations.Constraint;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * Check if evaluating the specified condition script returns true.<br>
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = AssertCheck.class)
public @interface Assert
{
	/**
	 * formula in the given expression language describing the constraint. the formula must return true if the constraint is satisfied.
	 * <br>
	 * available variables are:<br>
	 * <b>this</b> -&gt; the validated bean<br>
	 * <b>value</b> -&gt; the value to validate (e.g. the field value, parameter value, or method return value)
	 */
	String expression();

	/**
	 * the expression language that is used
	 */
	String language() default "javascript";
	
	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintsViolatedException
	 */
	String message() default "net.sf.oval.constraints.Assert.violated";
}
