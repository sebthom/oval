/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
package net.sf.oval.internal;

import net.sf.oval.localization.MessageResolver;
import net.sf.oval.localization.MessageResolverImpl;

/**
 * The held message resolver is used by OVal to resolve localized messages.
 *
 * @author Sebastian Thomschke
 */
public final class MessageResolverHolder
{
	private static MessageResolver messageResolver = MessageResolverImpl.INSTANCE;

	/**
	 * Returns a shared instance of the MessageResolver
	 */
	public static MessageResolver getMessageResolver()
	{
		return MessageResolverHolder.messageResolver;
	}

	/**
	 * 
	 * @param messageResolver the new messageResolver to use
	 */
	public static void setMessageResolver(final MessageResolver messageResolver)
			throws IllegalArgumentException
	{
		if (messageResolver == null)
			throw new IllegalArgumentException("messageResolver cannot be null");

		MessageResolverHolder.messageResolver = messageResolver;
	}
}
