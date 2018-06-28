/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.Validator;
import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.localization.value.MessageValueFormatter;

/**
 * @author Sebastian Thomschke
 *
 */
public final class MessageRenderer {
   public static String renderMessage(final String messageKey, final Map<String, ?> messageValues) {
      String message = Validator.getMessageResolver().getMessage(messageKey);
      if (message == null) {
         message = messageKey;
      }

      final MessageValueFormatter formatter = Validator.getMessageValueFormatter();

      // if there are no place holders in the message simply return it
      if (message.indexOf('{') == -1)
         return message;

      if (messageValues != null && messageValues.size() > 0) {
         for (final Entry<String, ?> entry : messageValues.entrySet()) {
            message = StringUtils.replaceAll(message, "{" + entry.getKey() + "}", formatter.format(entry.getValue()));
         }
      }
      return message;
   }

   public static String renderMessage(final String messageKey, final String messageValueName, final String messageValue) {
      String message = Validator.getMessageResolver().getMessage(messageKey);
      if (message == null) {
         message = messageKey;
      }

      // if there are no place holders in the message simply return it
      if (message.indexOf('{') == -1)
         return message;

      message = StringUtils.replaceAll(message, "{" + messageValueName + "}", messageValue);

      return message;
   }

   private MessageRenderer() {
      super();
   }
}
