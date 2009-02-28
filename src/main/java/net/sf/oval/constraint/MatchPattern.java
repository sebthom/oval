/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
 * Check if the specified regular expression pattern is matched.
 * 
 * <br><br>
 * <b>Note:</b> This constraint is also satisfied when the value to validate is null, therefore you might also need to specified @NotNull
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(checkWith = MatchPatternCheck.class)
public @interface MatchPattern
{
	/**
	 * error code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraint.MatchPattern";

	/**
	 *  Match flags, a bit mask that may include
	 *         Pattern.CASE_INSENSITIVE, Pattern.MULTILINE, Pattern.DOTALL,
	 *         Pattern.UNICODE_CASE, Pattern.CANON_EQ
	 *
	 * @see java.util.regex.Pattern
	 */
	int[] flags() default 0;

	/**
	 * specifies if all of the declared patterns must match or if one is sufficient 
	 */
	boolean matchAll() default true;

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraint.MatchPattern.violated";

	/**
	 * The regular expression(s) to match against
	 * <br><br>
	 * Examples:<br>
	 * decimal number: "^-{0,1}(\\d*|(\\d{1,3}([,]\\d{3})*))[.]?\\d*$"<br>
	 * numbers only: "^\\d*$"<br>
	 * e-mail address: "^([a-z0-9]{1,}[\\.\\_\\-]?[a-z0-9]{1,})\\@([a-z0-9]{2,}\\.)([a-z]{2,2}|org|net|com|gov|edu|int|info|biz|museum)$"<br>
	 * 
	 * @see java.util.regex.Pattern
	 */
	String[] pattern();

	/**
	 * The associated constraint profiles.
	 */
	String[] profiles() default {};

	/**
	 * severity passed to the ConstraintViolation object
	 */
	int severity() default 0;
}
