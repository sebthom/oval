/**
 * 
 */
package net.sf.oval.internal;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 *
 */
public final class MessageRenderer
{
	private MessageRenderer()
	{
	// do nothing
	}

	public static String renderMessage(final String messageKey,
			final Map<String, String> messageValues)
	{
		String message = MessageResolverHolder.getMessageResolver().getMessage(messageKey);
		if (message == null) message = messageKey;

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		if (messageValues != null && messageValues.size() > 0)
		{
			for (final Entry<String, String> entry : messageValues.entrySet())
			{
				message = StringUtils.replaceAll(message, "{" + entry.getKey() + "}", entry
						.getValue());
			}
		}
		return message;
	}

	public static String renderMessage(final String messageKey, final String[][] messageValues)
	{
		String message = MessageResolverHolder.getMessageResolver().getMessage(messageKey);
		if (message == null) message = messageKey;

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		if (messageValues != null && messageValues.length > 0)
		{
			for (final String[] entry : messageValues)
			{
				message = StringUtils.replaceAll(message, "{" + entry[0] + "}", entry[1]);
			}
		}
		return message;
	}
}
