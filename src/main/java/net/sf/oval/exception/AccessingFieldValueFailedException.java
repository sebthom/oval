/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
