/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

import net.sf.oval.context.OValContext;
import net.sf.oval.internal.MessageRenderer;

/**
 * @author Sebastian Thomschke
 */
public class InvokingMethodFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final OValContext context;
   private final Object validatedObject;

   public InvokingMethodFailedException(final String methodName, final Object validatedObject, final OValContext context, final Throwable cause) {
      super(MessageRenderer.renderMessage("net.sf.oval.exception.InvokingMethodFailedException.message", "methodName", methodName), cause);
      this.context = context;
      this.validatedObject = validatedObject;
   }

   public OValContext getContext() {
      return context;
   }

   public Object getValidatedObject() {
      return validatedObject;
   }
}
