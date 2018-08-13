/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.configuration.annotation.IsInvariant;

/**
 * @author Sebastian Thomschke
 */
public abstract aspect ApiUsageAuditor {

   // expression(<modifiers> <return type> <fq_class_name>.<method_name>(params))

   // pointcut getterMethods(): execution(!void *.is*()) || execution(!void *.get*());

   /*
    * instead of (!@Guarded *) we could use (!IsGuarded+)
    */

   /*
    * Rule 1: Warn about return value constraints for void methods
    */
   declare warning: execution(!@SuppressOValWarnings @(@Constraint *) void (!@SuppressOValWarnings *).*(..)):
      "OVal API usage violation 1: Method return value constraints are not allowed for methods without return values";

   /*
    * Rule 2: Warn about return value constraints for non-void, parameterized methods in classes that are not guarded
    */
   declare warning: execution(!@SuppressOValWarnings @(@Constraint *) !void (!@(Guarded || SuppressOValWarnings) *).*(*,..)):
      "OVal API usage violation 2: Method return value constraints for parameterized methods are only allowed in guarded classes";

   /*
    * Rule 3: Warn about return value constraints for non-void, non-parameterized methods missing the @Invariant annotation in classes
    * that are not guarded
    */
   declare warning: execution(!@SuppressOValWarnings !@IsInvariant @(@Constraint *) !void (!@(Guarded || SuppressOValWarnings) *).*()):
      "OVal API usage violation 3: Method return value constraints are only allowed if the method is annotated with @IsInvariant or the declaring class is guarded";

   /*
    * Rule 4: Warn about the @PreValidateThis annotation used on methods in classes that are not guarded
    */
   declare warning: execution(!@SuppressOValWarnings @PreValidateThis * (!@(Guarded || SuppressOValWarnings) *).*(..)):
      "OVal API usage violation 4: @PreValidateThis is only allowed in guarded classes";

   /*
    * Rule 5: Warn about the @PostValidateThis annotation used on methods and constructors in classes that are not guarded
    */
   declare warning: execution(!@SuppressOValWarnings @PostValidateThis * (!@(Guarded || SuppressOValWarnings) *).*(..)) || //
                    execution(!@SuppressOValWarnings @PostValidateThis   (!@(Guarded || SuppressOValWarnings) *).new(..)):
      "OVal API usage violation 5: @PostValidateThis is only allowed in guarded classes";

   /*
    * Rule 6: Warn about method parameter constraints in classes that are not guarded
    */
   declare warning: execution(!@SuppressOValWarnings * (!@(Guarded || SuppressOValWarnings) *).*(.., @(@Constraint *) (*),..)):
      "OVal API usage violation 6: Method parameter constraints are only allowed in guarded classes";

    /*
     * Rule 7: Warn about constructor parameter constraints in classes that are not guarded
     */
    declare warning: execution(!@SuppressOValWarnings (!@(Guarded || SuppressOValWarnings) *).new(.., @(@Constraint *) (*),..)):
       "OVal API usage violation 7: Constructor parameter constraints are only allowed in guarded classes";
}
