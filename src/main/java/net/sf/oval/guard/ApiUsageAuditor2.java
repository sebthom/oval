/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareWarning;

/**
 * This is an annotations based version of the {@link ApiUsageAuditor} aspect
 *
 * @author Sebastian Thomschke
 */
@Aspect
// CHECKSTYLE:IGNORE LineLength FOR NEXT 50 LINES
public abstract class ApiUsageAuditor2 {
   /*
    * Rule 1: Warn about return value constraints for void methods
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings @(@net.sf.oval.configuration.annotation.Constraint *) void (!@net.sf.oval.guard.SuppressOValWarnings *).*(..))")
   public static final String RULE1 = "OVal API usage violation 1: Method return value constraints are not allowed for methods without return values";

   /*
    * Rule 2: Warn about return value constraints for non-void, parameterized methods in classes that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings @(@net.sf.oval.configuration.annotation.Constraint *) !void (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).*(*,..))")
   public static final String RULE2 = "OVal API usage violation 2: Method return value constraints for parameterized methods are only allowed in guarded classes";

   /*
    * Rule 3: Warn about return value constraints for non-void, non-parameterized methods missing the @Invariant annotation in classes
    * that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings !@net.sf.oval.configuration.annotation.IsInvariant @(@net.sf.oval.configuration.annotation.Constraint *) !void (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).*())")
   public static final String RULE3 = "OVal API usage violation 3: Method return value constraints are only allowed if the method is annotated with @IsInvariant or the declaring class is guarded";

   /*
    * Rule 4: Warn about the @PreValidateThis annotation used on methods in classes that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings @net.sf.oval.guard.PreValidateThis * (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).*(..))")
   public static final String RULE4 = "OVal API usage violation 4: @PreValidateThis is only allowed in guarded classes";

   /*
    * Rule 5: Warn about the @PostValidateThis annotation used on methods and constructors in classes that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings @net.sf.oval.guard.PostValidateThis * (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).*(..)) ||"
      + "execution(!@net.sf.oval.guard.SuppressOValWarnings @PostValidateThis   (!@(Guarded || net.sf.oval.guard.SuppressOValWarnings) *).new(..))")
   public static final String RULE5 = "OVal API usage violation 5: @PostValidateThis is only allowed in guarded classes";

   /*
    * Rule 6: Warn about method parameter constraints in classes that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings * (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).*(.., @(@net.sf.oval.configuration.annotation.Constraint *) (*),..))")
   public static final String RULE6 = "OVal API usage violation 6: Method parameter constraints are only allowed in guarded classes";

   /*
    * Rule 7: Warn about constructor parameter constraints in classes that are not guarded
    */
   @DeclareWarning("execution(!@net.sf.oval.guard.SuppressOValWarnings (!@(net.sf.oval.guard.Guarded || net.sf.oval.guard.SuppressOValWarnings) *).new(.., @(@net.sf.oval.configuration.annotation.Constraint *) (*),..))")
   public static final String RULE7 = "OVal API usage violation 7: Constructor parameter constraints are only allowed in guarded classes";

}
