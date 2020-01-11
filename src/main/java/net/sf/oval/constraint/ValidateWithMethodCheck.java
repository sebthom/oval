/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodCheck extends AbstractAnnotationCheck<ValidateWithMethod> {
   private static final long serialVersionUID = 1L;

   private final ConcurrentMap<Class<?>, Method> validationMethodsByClass = Validator.getCollectionFactory().createConcurrentMap();

   private boolean ignoreIfNull;
   private String methodName;
   private Class<?> parameterType;

   @Override
   public void configure(final ValidateWithMethod constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMethodName(constraintAnnotation.methodName());
      setParameterType(constraintAnnotation.parameterType());
      setIgnoreIfNull(constraintAnnotation.ignoreIfNull());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(4);
      messageVariables.put("ignoreIfNull", Boolean.toString(ignoreIfNull));
      messageVariables.put("methodName", methodName);
      messageVariables.put("parameterType", parameterType.getName());
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public String getMethodName() {
      return methodName;
   }

   public Class<?> getParameterType() {
      return parameterType;
   }

   public boolean isIgnoreIfNull() {
      return ignoreIfNull;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
      throws ReflectionException {
      if (valueToValidate == null && ignoreIfNull)
         return true;

      final Class<?> clazz = validatedObject.getClass();
      Method method = validationMethodsByClass.get(clazz);
      if (method == null) {
         method = ReflectionUtils.getMethodRecursive(clazz, methodName, parameterType);
         validationMethodsByClass.put(clazz, method);
      }

      if (method == null)
         throw new InvalidConfigurationException("Method " + clazz.getName() + "." + methodName + "(" + parameterType + ") not found. Is [" + parameterType
            + "] the correct value for [@ValidateWithMethod.parameterType]?");
      // explicit cast to workaround:
      // "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds boolean,java.lang.Object"
      return (Boolean) ReflectionUtils.invokeMethod(method, validatedObject, valueToValidate);
   }

   public void setIgnoreIfNull(final boolean ignoreIfNull) {
      this.ignoreIfNull = ignoreIfNull;
      requireMessageVariablesRecreation();
   }

   public void setMethodName(final String methodName) {
      this.methodName = methodName;
      synchronized (validationMethodsByClass) {
         validationMethodsByClass.clear();
      }
      requireMessageVariablesRecreation();
   }

   public void setParameterType(final Class<?> parameterType) {
      this.parameterType = parameterType;
      synchronized (validationMethodsByClass) {
         validationMethodsByClass.clear();
      }
      requireMessageVariablesRecreation();
   }
}
