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
package net.sf.oval.exception;

import net.sf.oval.internal.MessageRenderer;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageNotAvailableException extends InvalidConfigurationException
{
	private static final long serialVersionUID = 1L;

	public ExpressionLanguageNotAvailableException(final String languageId)
	{
		super(MessageRenderer.renderMessage("net.sf.oval.exception.ExpressionLanguageNotAvailableException.message",
				"languageId", languageId));
	}
}