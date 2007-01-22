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
package net.sf.oval;

import java.io.Serializable;

import net.sf.oval.contexts.OValContext;

/**
 * interface for classes that can check/validate if a single constraint is satisfied
 * 
 * @author Sebastian Thomschke
 */
public interface Check extends Serializable
{
	/**
	 * gets the default message is displayed if a corresponding message key
	 * is not found in the messages properties file
	 * 
	 * default processed place holders are
	 * {0} => specifies which getter, method parameter or field was validated
	 * {1} => string representation of the validated value 
	 */
	String getMessage();

	/**
	 * values that are used as place holders when rendering the error message.
	 * the first value is used as placeholder {2}, the second value as placeholder {3} etc.
	 */
	String[] getMessageValues();

	/**
	 * @return the profiles, may return null
	 */
	String[] getProfiles();

	/**
	 * @param profiles the profiles to set
	 */
	void setProfiles(String[] profiles);

	/**
	 * This method implements the validation logic
	 * 
	 * @param validatedObject the object to validate the value against
	 * @param validatedValue the value to validate
	 * @param context the validation context (e.g. a field, a constructor parameter or a method parameter)
	 * @return true if the value satisfies the checked constraint
	 */
	boolean isSatisfied(Object validatedObject, Object validatedValue, OValContext context);

	/**
	 * sets the default message is displayed if a corresponding message key
	 * is not found in the messages properties file
	 * 
	 * default processed place holders are
	 * {0} => specifies which getter, method parameter or field was validated
	 * {1} => string representation of the validated value 
	 */
	void setMessage(String message);
}
