/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

import java.lang.reflect.Field;

import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.SerializableField;

/**
 * @author Sebastian Thomschke
 */
public class FieldContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final SerializableField field;

   public FieldContext(final Class<?> declaringClass, final String fieldName) {
      final Field field = ReflectionUtils.getField(declaringClass, fieldName);
      this.field = new SerializableField(field);
      compileTimeType = field.getType();
   }

   public FieldContext(final Field field) {
      this.field = new SerializableField(field);
      compileTimeType = field.getType();
   }

   @Override
   public Class<?> getDeclaringClass() {
      return field.getDeclaringClass();
   }

   public Field getField() {
      return field.getField();
   }

   @Override
   public String toString() {
      return field.getDeclaringClass().getName() + '.' + toStringUnqualified();
   }

   @Override
   public String toStringUnqualified() {
      return field.getName();
   }
}
