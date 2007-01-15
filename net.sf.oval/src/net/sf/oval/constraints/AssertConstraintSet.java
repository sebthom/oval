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

/**
 * Check if the value satisfies the all constraints of pecified constraint set
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = AssertConstraintSetCheck.class)
public @interface AssertConstraintSet
{
	/**
	 * The class in which the constraint set is defined.<br>
	 * The default value Object.class means in the current class.
	 */
	Class source() default Object.class;

	/**
	 * The id of the constraint set to apply here:<br>
	 * <ul>
	 * <li>If the constraint set was defined in the same class, then the local id can be used.<br>
	 * <li>If the constraint set was defined in another class and this class is specified in the source parameter, then the local id can be used too.<br>
	 * <li>In any other case the fully qualified id of the constraint set needs to be used.
	 * </ul>
	 */
	String id();
}
