/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

import net.sf.oval.internal.MessageRenderer;

/**
 * @author Sebastian Thomschke
 */
public class UndefinedConstraintSetException extends InvalidConfigurationException {
   private static final long serialVersionUID = 1L;

   public UndefinedConstraintSetException(final String constraintSetId) {
      super(MessageRenderer.renderMessage("net.sf.oval.exception.UndefinedConstraintSetException.message", "constraintSetId", constraintSetId));
   }
}
