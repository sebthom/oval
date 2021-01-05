/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.lang.reflect.Method;

import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvokingMethodFailedException;

/**
 * @author Sebastian Thomschke
 */
public final class MethodInvocationCommand {
   private final Object target;
   private final Method method;
   private final Object[] args;

   public MethodInvocationCommand(final Object target, final Method method, final Object[] args) {
      this.target = target;
      this.method = method;
      this.args = args;
   }

   public Object execute() throws InvokingMethodFailedException, ConstraintsViolatedException {
      return ReflectionUtils.invokeMethod(method, target, args);
   }
}
