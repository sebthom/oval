/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

import net.sf.oval.internal.MessageRenderer;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetAlreadyDefinedException extends InvalidConfigurationException {
   private static final long serialVersionUID = 1L;

   public ConstraintSetAlreadyDefinedException(final String contraintSetId) {
      super(MessageRenderer.renderMessage("net.sf.oval.exception.ConstraintSetAlreadyDefinedException.message", "contraintSetId", contraintSetId));
   }
}
