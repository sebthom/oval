/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import net.sf.oval.Validator;
import net.sf.oval.context.ClassContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodExitContext;
import net.sf.oval.context.MethodReturnValueContext;

/**
 * @author Sebastian Thomschke
 */
public final class ContextCache {
   private static final Log LOG = Log.getLog(ContextCache.class);

   private static final ConcurrentMap<Class<?>, ClassContext> CLASS_CONTEXTS = Validator.getCollectionFactory().createConcurrentMap();
   private static final ConcurrentMap<Field, FieldContext> FIELD_CONTEXTS = Validator.getCollectionFactory().createConcurrentMap();
   private static final ConcurrentMap<Method, MethodEntryContext> METHOD_ENTRY_CONTEXTS = Validator.getCollectionFactory().createConcurrentMap();
   private static final ConcurrentMap<Method, MethodExitContext> METHOD_EXIT_CONTEXTS = Validator.getCollectionFactory().createConcurrentMap();
   private static final ConcurrentMap<Method, MethodReturnValueContext> METHOD_RETURN_VALUE_CONTEXTS = Validator.getCollectionFactory().createConcurrentMap();

   public static void clear() {
      LOG.debug("Clearing context cache...");
      CLASS_CONTEXTS.clear();
      FIELD_CONTEXTS.clear();
      METHOD_ENTRY_CONTEXTS.clear();
      METHOD_EXIT_CONTEXTS.clear();
      METHOD_RETURN_VALUE_CONTEXTS.clear();
   }

   public static ClassContext getClassContext(final Class<?> clazz) {
      ClassContext ctx = CLASS_CONTEXTS.get(clazz);
      if (ctx == null) {
         ctx = new ClassContext(clazz);
         CLASS_CONTEXTS.put(clazz, ctx);
      }
      return ctx;
   }

   public static FieldContext getFieldContext(final Field field) {
      FieldContext ctx = FIELD_CONTEXTS.get(field);
      if (ctx == null) {
         ctx = new FieldContext(field);
         FIELD_CONTEXTS.put(field, ctx);
      }
      return ctx;
   }

   public static MethodEntryContext getMethodEntryContext(final Method method) {
      MethodEntryContext ctx = METHOD_ENTRY_CONTEXTS.get(method);
      if (ctx == null) {
         ctx = new MethodEntryContext(method);
         METHOD_ENTRY_CONTEXTS.put(method, ctx);
      }
      return ctx;
   }

   public static MethodExitContext getMethodExitContext(final Method method) {
      MethodExitContext ctx = METHOD_EXIT_CONTEXTS.get(method);
      if (ctx == null) {
         ctx = new MethodExitContext(method);
         METHOD_EXIT_CONTEXTS.put(method, ctx);
      }
      return ctx;
   }

   public static MethodReturnValueContext getMethodReturnValueContext(final Method method) {
      MethodReturnValueContext ctx = METHOD_RETURN_VALUE_CONTEXTS.get(method);
      if (ctx == null) {
         ctx = new MethodReturnValueContext(method);
         METHOD_RETURN_VALUE_CONTEXTS.put(method, ctx);
      }
      return ctx;
   }

   private ContextCache() {
      super();
   }
}
