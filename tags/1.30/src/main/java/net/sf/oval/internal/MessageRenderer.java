/**
 * 
 */
package net.sf.oval.internal;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.Validator;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 *
 */
public final class MessageRenderer
{
	public static String renderMessage(final String messageKey, final Map<String, String> messageValues)
	{
		String message = Validator.getMessageResolver().getMessage(messageKey);
		if (message == null)
		{
			message = messageKey;
		}

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		if (messageValues != null && messageValues.size() > 0)
		{
			for (final Entry<String, String> entry : messageValues.entrySet())
			{
				message = StringUtils.replaceAll(message, "{" + entry.getKey() + "}", entry.getValue());
			}
		}
		return message;
	}

	public static String renderMessage(final String messageKey, final String messageValueName, final String messageValue)
	{
		String message = Validator.getMessageResolver().getMessage(messageKey);
		if (message == null)
		{
			message = messageKey;
		}

		// if there are no place holders in the message simply return it
		if (message.indexOf('{') == -1) return message;

		message = StringUtils.replaceAll(message, "{" + messageValueName + "}", messageValue);

		return message;
	}

	private MessageRenderer()
	{
	// do nothing
	}
}
