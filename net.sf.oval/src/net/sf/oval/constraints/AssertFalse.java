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
package net.sf.oval.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.Constraint;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.checks.AssertFalseCheck;

/**
 * Check if the value is false.
 * 
 * <br><br>
 * <b>Note:</b> This constraint is also satisified when the value to validate is null, therefore you might also need to specified @NotNull
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = AssertFalseCheck.class)
public @interface AssertFalse
{
	/**
	 * message to be used for constructing the ConstraintViolation object
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraints.AssertFalse.violated";
	
	/**
	 * The associated validation profiles.
	 */
	String[] profiles() default {};
}
