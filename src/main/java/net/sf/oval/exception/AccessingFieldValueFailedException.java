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
public class AccessingFieldValueFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final OValContext context;
   private final Object validatedObject;

   public AccessingFieldValueFailedException(final String fieldName, final Object validatedObject, final OValContext context, final Throwable cause) {
      super(MessageRenderer.renderMessage("net.sf.oval.exception.AccessingFieldValueFailedException.message", "fieldName", fieldName), cause);
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
