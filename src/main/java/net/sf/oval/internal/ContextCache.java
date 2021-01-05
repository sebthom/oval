/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
      return CLASS_CONTEXTS.computeIfAbsent(clazz, ClassContext::new);
   }

   public static FieldContext getFieldContext(final Field field) {
      return FIELD_CONTEXTS.computeIfAbsent(field, FieldContext::new);
   }

   public static MethodEntryContext getMethodEntryContext(final Method method) {
      return METHOD_ENTRY_CONTEXTS.computeIfAbsent(method, MethodEntryContext::new);
   }

   public static MethodExitContext getMethodExitContext(final Method method) {
      return METHOD_EXIT_CONTEXTS.computeIfAbsent(method, MethodExitContext::new);
   }

   public static MethodReturnValueContext getMethodReturnValueContext(final Method method) {
      return METHOD_RETURN_VALUE_CONTEXTS.computeIfAbsent(method, MethodReturnValueContext::new);
   }

   private ContextCache() {
   }
}
