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
package net.sf.oval;

/**
 * interface for classes that can check/validate if a single constraint is satisfied
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public interface Check
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
	 * This method implements the validation logic
	 * 
	 * @param validatedObject the object to validate the value against
	 * @param value the property/parameter to validate
	 * @return true if the property/parameter meets the check
	 */
	boolean isSatisfied(Object validatedObject, Object value);

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
