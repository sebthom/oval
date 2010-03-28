/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.annotation.Constraint;

/**
 * Check if the date is within the a date range.
 * 
 * <br><br>
 * <b>Note:</b> This constraint is also satisfied when the value to validate is null, therefore you might also need to specified @NotNull
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(checkWith = DateRangeCheck.class)
public @interface DateRange
{
	/**
	 * <p>In case the constraint is declared for an array, collection or map this controls how the constraint is applied to it and it's child objects.
	 * 
	 * <p><b>Default:</b> ConstraintTarget.VALUES
	 * 
	 * <p><b>Note:</b> This setting is ignored for object types other than array, map and collection.
	 */
	ConstraintTarget[] appliesTo() default ConstraintTarget.VALUES;
	
	/**
	 * error code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraint.DateRange";

	/**
	 * The format of the specified dates in a form understandable by the SimpleDateFormat class.
	 * Defaults to the default format style of the default locale.
	 */
	String format() default "";

	/**
	 * The upper date compared against in the format specified with the dateFormat parameter. 
	 * If not specified then no upper boundary check is performed.<br>
	 * Special values are:
	 * <ul>
	 * <li><code>now</code>
	 * <li><code>today</code>
	 * <li><code>yesterday</code>
	 * <li><code>tomorrow</code>
	 * </ul>
	 */
	String max() default "";

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraint.DateRange.violated";

	/**
	 * The lower date compared against in the format specified with the dateFormat parameter. 
	 * If not specified then no upper boundary check is performed.<br>
	 * Special values are:
	 * <ul>
	 * <li><code>now</code>
	 * <li><code>today</code>
	 * <li><code>yesterday</code>
	 * <li><code>tomorrow</code>
	 * </ul>
	 */
	String min() default "";

	/**
	 * The associated constraint profiles.
	 */
	String[] profiles() default {};

	/**
	 * severity passed to the ConstraintViolation object
	 */
	int severity() default 0;
	
	/**
	 * Tolerance in milliseconds the validated value can be beyond the min/max limits. 
	 * This is useful to compensate time differences in distributed environments where the clocks are not 100% in sync.
	 */
	int tolerance() default 0;
}
