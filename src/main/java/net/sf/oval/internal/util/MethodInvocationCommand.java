/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
