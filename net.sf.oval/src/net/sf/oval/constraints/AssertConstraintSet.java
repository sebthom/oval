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

/**
 * check if the value satisfies the specified constraint set
 * 
 * <br><br>
 * <b>Note:</b> This constraint is also satisified when the value to validate is null, therefore you might also need to specified @NotNull
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = AssertConstraintSetCheck.class)
public @interface AssertConstraintSet
{
	/**
	 * The class in which the constraint set is defined.
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
