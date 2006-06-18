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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.annotations.Constraint;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * check the value by a method of the same class that takes the value as argument and returns true if valid
 * and false if invalid
 *
 * <br>
 * <b>Note:</b> Applies only to fields, method parameters and getter methods. The constraint will
 * be ignored when specified for methods with method parameters or returning void.
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.7 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = ValidateWithMethodCheck.class)
public @interface ValidateWithMethod
{
	/**
	 * name a the single parameter method to use for validation
	 */
	String methodName();

	/**
	 * type of the method parameter
	 */
	Class parameterType();

	/**
	 * message to be used for the ContraintsViolatatedException
	 * 
	 * @see ConstraintsViolatedException
	 */
	String message() default "net.sf.oval.constraints.ValidateWithMethod.violated";
}
