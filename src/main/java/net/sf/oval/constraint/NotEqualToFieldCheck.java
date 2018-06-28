/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.FieldNotFoundException;
import net.sf.oval.exception.InvokingMethodFailedException;
import net.sf.oval.exception.MethodNotFoundException;
import net.sf.oval.internal.ContextCache;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class NotEqualToFieldCheck extends AbstractAnnotationCheck<NotEqualToField> {
   private static final long serialVersionUID = 1L;

   private boolean useGetter;

   private String fieldName;

   private Class<?> declaringClass;

   @Override
   public void configure(final NotEqualToField constraintAnnotation) {
      super.configure(constraintAnnotation);
      setFieldName(constraintAnnotation.value());
      setDeclaringClass(constraintAnnotation.declaringClass());
      setUseGetter(constraintAnnotation.useGetter());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("fieldName", fieldName);
      messageVariables.put("declaringClass", declaringClass == null || declaringClass == Void.class ? null : declaringClass.getName());
      messageVariables.put("useGetter", Boolean.toString(useGetter));
      return messageVariables;
   }

   public Class<?> getDeclaringClass() {
      return declaringClass;
   }

   public String getFieldName() {
      return fieldName;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      final Class<?> clazz = validatedObject.getClass();

      final Object valueToCompare;
      if (useGetter) {
         final Method getter = ReflectionUtils.getGetterRecursive(clazz, fieldName);
         if (getter == null)
            throw new MethodNotFoundException("Getter for field <" + fieldName + "> not found in class <" + clazz + "> or it's super classes.");

         try {
            valueToCompare = getter.invoke(validatedObject);
         } catch (final Exception ex) {
            throw new InvokingMethodFailedException(getter.getName(), validatedObject, ContextCache.getMethodReturnValueContext(getter), ex);
         }
      } else {
         final Field field = ReflectionUtils.getFieldRecursive(clazz, fieldName);

         if (field == null)
            throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <" + clazz + "> or it's super classes.");

         valueToCompare = ReflectionUtils.getFieldValue(field, validatedObject);
      }

      if (valueToCompare == null)
         return true;

      return !valueToValidate.equals(valueToCompare);
   }

   public boolean isUseGetter() {
      return useGetter;
   }

   public void setDeclaringClass(final Class<?> declaringClass) {
      this.declaringClass = declaringClass == Void.class ? null : declaringClass;
      requireMessageVariablesRecreation();
   }

   public void setFieldName(final String fieldName) {
      this.fieldName = fieldName;
      requireMessageVariablesRecreation();
   }

   public void setUseGetter(final boolean useGetter) {
      this.useGetter = useGetter;
      requireMessageVariablesRecreation();
   }
}
