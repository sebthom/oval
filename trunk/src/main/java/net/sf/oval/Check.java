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
package net.sf.oval;

import java.io.Serializable;
import java.util.Map;

import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * interface for classes that can check/validate if a constraint is satisfied
 * 
 * @author Sebastian Thomschke
 */
public interface Check extends Serializable
{
	/**
	 * @return the error code that will be used in a corresponding ConstraintViolation object
	 */
	String getErrorCode();

	/** 
	 * gets the default message that is displayed if a corresponding message key
	 * is not found in the messages properties file
	 * <br>
	 * default processed place holders are:
	 * <ul>
	 * <li>{context} => specifies which getter, method parameter or field was validated
	 * <li>{invalidValue} => string representation of the validated value
	 * </ul>
	 */
	String getMessage();

	/**
	 * values that are used to fill place holders when rendering the error message.
	 * A key "min" with a value "4" will replace the place holder {min} in an error message
	 * like "Value cannot be smaller than {min}" with the string "4".
	 */
	Map<String, String> getMessageVariables();

	/**
	 * @return the profiles, may return null
	 */
	String[] getProfiles();

	/**
	 * @return the severity
	 */
	int getSeverity();

	/**
	 * This method implements the validation logic
	 * 
	 * @param validatedObject the object/bean to validate the value against, for static fields or methods this is the class
	 * @param valueToValidate the value to validate, may be null when validating pre conditions for static methods
	 * @param context the validation context (e.g. a field, a constructor parameter or a method parameter)
	 * @param validator the calling validator
	 * @return true if the value satisfies the checked constraint
	 */
	boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context,
			Validator validator) throws OValException;

	/**
	 * @param errorCode the error code to set
	 */
	void setErrorCode(String errorCode);

	/**
	 * sets the default message that is displayed if a corresponding message key
	 * is not found in the messages properties file
	 * 
	 * <br>
	 * default processed place holders are:
	 * <ul>
	 * <li>{context} => specifies which getter, method parameter or field was validated
	 * <li>{invalidValue} => string representation of the validated value
	 * </ul>
	 */
	void setMessage(String message);

	/**
	 * @param profiles the profiles to set
	 */
	void setProfiles(String... profiles);

	/**
	 * @param severity the severity to set
	 */
	void setSeverity(int severity);
}
