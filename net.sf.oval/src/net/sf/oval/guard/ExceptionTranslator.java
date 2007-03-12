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
package net.sf.oval.guard;

import net.sf.oval.exception.OValException;

/**
 * An exception translater for all exception thrown by the guard
 * during runtime when performing constraint validations on guarded objects.
 * 
 * @author Sebastian Thomschke
 */
public interface ExceptionTranslator
{
	/**
	 * If this method returns null, the original exception is thrown.
	 * 
	 * @param ex
	 * @return the exception to throw
	 */
	RuntimeException translateException(OValException ex);
}
