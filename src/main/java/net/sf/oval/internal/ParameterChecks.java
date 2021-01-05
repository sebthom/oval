/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public final class ParameterChecks {
   public final Set<Check> checks = new LinkedHashSet<>(2);
   public final Set<CheckExclusion> checkExclusions = new LinkedHashSet<>(2);

   public final int parameterIndex;

   public final OValContext context;

   public ParameterChecks(final Constructor<?> ctor, final int paramIndex, final String paramName) {
      context = new ConstructorParameterContext(ctor, paramIndex, paramName);
      parameterIndex = paramIndex;
   }

   public ParameterChecks(final Method method, final int paramIndex, final String paramName) {
      context = new MethodParameterContext(method, paramIndex, paramName);
      parameterIndex = paramIndex;
   }

   public boolean hasChecks() {
      return !checks.isEmpty();
   }

   public boolean hasExclusions() {
      return !checkExclusions.isEmpty();
   }

   public boolean isEmpty() {
      return checks.isEmpty() && checkExclusions.isEmpty();
   }
}
