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
 * Applies the constraints of the specified field to the annotated parameter or getter method.
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.3 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = FieldConstraintsCheck.class)
public @interface FieldConstraints
{
	/**
	 * message to be used for the ContraintsViolatatedException
	 * 
	 * @see ConstraintsViolatedException
	 */
	String message() default "not used";

	/**
	 * @return name of the field. If not specified, the constraints of the field with the same name as the annotated constructor/method parameter are applied.
	 */
	String value() default "";
}
