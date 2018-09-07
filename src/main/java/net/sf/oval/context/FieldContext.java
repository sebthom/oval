/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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

   public Field getField() {
      return field.getField();
   }

   @Override
   public String toString() {
      return field.getDeclaringClass().getName() + '.' + field.getName();
   }
}
