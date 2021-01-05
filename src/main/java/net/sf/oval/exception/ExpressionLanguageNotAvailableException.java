/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

import net.sf.oval.internal.MessageRenderer;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageNotAvailableException extends InvalidConfigurationException {
   private static final long serialVersionUID = 1L;

   public ExpressionLanguageNotAvailableException(final String languageId) {
      super(MessageRenderer.renderMessage("net.sf.oval.exception.ExpressionLanguageNotAvailableException.message", "languageId", languageId));
   }
}
