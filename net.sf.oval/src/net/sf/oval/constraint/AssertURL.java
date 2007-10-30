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
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.constraint.AssertURLCheck.URIScheme;

/**
 * Check if the value passes a validation by Validator.validate()
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Constraint(checkWith = AssertURLCheck.class)
public @interface AssertURL
{
	/**
	 * Specifies if a connection to the URL should be attempted to verify its validity. 
	 */
	boolean connect() default false;

	/**
	 * error code passed to the ConstraintViolation object
	 */
	String errorCode() default "net.sf.oval.constraints.AssertURL";

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintViolation
	 */
	String message() default "net.sf.oval.constraints.AssertURL.violated";

	/**
	 * Specifies the allowed URL schemes.
	 */
	URIScheme[] permittedURISchemes() default {URIScheme.HTTP, URIScheme.HTTPS, URIScheme.FTP};

	/**
	 * priority passed to the ConstraintViolation object
	 */
	int priority() default 0;

	/**
	 * The associated validation profiles.
	 */
	String[] profiles() default {};
}
