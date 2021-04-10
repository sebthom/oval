/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.guard;

import java.lang.reflect.Method;
import java.util.LinkedList;

import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvokingMethodFailedException;
import net.sf.oval.internal.util.MethodInvocationCommand;

/**
 * @author Sebastian Thomschke
 */
public class ProbeModeListener extends ConstraintsViolatedAdapter {
   private final Object target;
   private final LinkedList<MethodInvocationCommand> commands = new LinkedList<>();

   /**
    * Creates a new instance for the given target object.
    */
   ProbeModeListener(final Object target) {
      this.target = target;
   }

   /**
    * Executes the collected method calls and clears the internal list holding them.
    */
   public synchronized void commit() throws InvokingMethodFailedException, ConstraintsViolatedException {
      for (final MethodInvocationCommand cmd : commands) {
         cmd.execute();
      }
      commands.clear();
   }

   /**
    * @return the object that is/was in probe mode
    */
   public Object getTarget() {
      return target;
   }

   /**
    * Adds the given method and method arguments to the method call stack.
    *
    * @param args the method arguments
    */
   void onMethodCall(final Method method, final Object[] args) {
      commands.add(new MethodInvocationCommand(target, method, args));
   }
}
