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
 * check if the string representation has certain length
 * 
 * <br><br>
 * <b>Note:</b> This constraint is also satisified when the value to validate is null, therefore you might also need to specified @NotNull
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(check = LengthCheck.class)
public @interface Length
{
	int min() default 0;

	int max() default Integer.MAX_VALUE;

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintsViolatedException
	 */
	String message() default "net.sf.oval.constraints.Length.violated";
}
