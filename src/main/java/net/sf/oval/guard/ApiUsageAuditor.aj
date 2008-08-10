/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.guard;

import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.configuration.annotation.IsInvariant;

/**
 * @author Sebastian Thomschke
 */
abstract aspect ApiUsageAuditor
{
	// pointcut getterMethods(): execution(!void *.is*()) || execution(!void *.get*());

	/*
	 * instead of (!@Guarded *) we could use (!IsGuarded+) 
	 */

	/*
	 * Rule 1: Warn about return value constraints for void methods 
	 */
	declare warning: execution(!@SuppressOValWarnings @(@Constraint *) void *.*(..)): 
		"OVal API usage violation 1: Method return value constraints are not allowed for methods without return values";

	/*
	 * Rule 2: Warn about return value constraints for non-void, parameterized methods in classes that are not guarded 
	 */
	declare warning: execution(!@SuppressOValWarnings @(@Constraint *) !void (!@Guarded *).*(*,..)): 
		"OVal API usage violation 2: Method return value constraints for parameterized methods are only allowed in guarded classes";

	/*
	 * Rule 3: Warn about return value constraints for non-void, non-parameterized methods missing the @Invariant annotation in classes 
	 * that are not guarded
	 */
	declare warning: execution(!@SuppressOValWarnings !@IsInvariant @(@Constraint *) !void (!@Guarded *).*()): 
		"OVal API usage violation 3: Method return value constraints are only allowed if the method is annotated with @IsInvariant or the declaring class is guarded";

	/*
	 * Rule 4: Warn about the @PreValidateThis annotation used on methods in classes that are not guarded
	 */
	declare warning: execution (!@SuppressOValWarnings @PreValidateThis * (!@Guarded *).*(..)): 
		"OVal API usage violation 4: @PreValidateThis is only allowed in guarded class";

	/*
	 * Rule 5: Warn about the @PostValidateThis annotation used on methods and constructors in classes that are not guarded
	 */
	declare warning: execution (!@SuppressOValWarnings @PostValidateThis * (!@(Guarded || SuppressOValWarnings) *).*(..)) || execution (!@SuppressOValWarnings @PostValidateThis (!@Guarded *).new(..)): 
		"OVal API usage violation 5: @PostValidateThis is only allowed in guarded classes";

	/*
	 * Rule 6: Warn about method parameter constraints in classes that are not guarded
	 * TODO AspectJ seems to be broken here, it does not match based on annotations at parameter level
	 * e.g.	execution(* *.*(*,..)) => matches
	 * 		execution(* *.*(@java.util.SuppressWarnings *,..)) => does not match
	 *     	execution(* *.*(@net.sf.oval.constraints.NotNull *,..)) => does not match and results in warning: [Xlint:unmatchedTargetKind]
	 *      execution(* (!@Guarded *).*(@(@Constraint *) *, ..)) => does not match and results in warning: [Xlint:unmatchedTargetKind]
	 */
	declare warning:
		execution(* (!@Guarded *).*(@(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"OVal API usage violation 6: Method parameter constraints are only allowed in guarded class";

	/*
	 * Rule 7: Warn about constructor parameter constraints in classes that are not guarded
	 * TODO AspectJ seems to be broken here, it does not match based on annotations at parameter level 
	 */
	declare warning:
		execution((!@Guarded *).new(@(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"OVal API usage violation 7: Constructor parameter constraints are only allowed in guarded class";
}
