/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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

import net.sf.oval.constraints.Constraint;

/**
 * @author Sebastian Thomschke
 */
abstract aspect ApiUsageAuditor
{
	pointcut getterMethods(): execution(!void *.is*()) || execution(!void *.get*());

	/*
	 * Warn about constraints for void methods 
	 */
	declare warning: execution(@(@Constraint *) void (@Guarded *).*(..)): 
		"Method return value constraints are not allowed for methods without return values";

	/*
	 * Warn about return value constraints for non-getter methods in classes not annotated with @Constrained 
	 */
	declare warning: execution(@(@Constraint *) * (!@Guarded *).*(..)) && !getterMethods(): 
		"Method return value constraints for non-getter methods are only allowed in classes annotated with @Constrainted";

	/*
	 * Warn about the @PreValidateThis annotation used on methods in classes not annotated with @Constrained
	 */
	declare warning: execution (@PreValidateThis * (!@Guarded *).*(..)): 
		"@PreValidateThis is only allowed in class annotated with @Constrainted";

	/*
	 * Warn about the @PostValidateThis annotation used on methods and constructors in classes not annotated with @Constrained
	 */
	declare warning: execution (@PostValidateThis * (!@Guarded *).*(..)) || execution (@PostValidateThis (!@Guarded *).new(..)): 
		"@PostValidateThis is only allowed in classes annotated with @Guarded";

	/*
	 * Warn about method parameter constraints in classes not annotated with @Constrained
	 */
	declare warning:
		execution(* (!@Guarded *).*(@(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Guarded *).*(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"Method parameter constraints are only allowed in class annotated with @Guarded";

	/*
	 * Warn about constructor parameter constraints in classes not annotated with @Constrained
	 */
	declare warning:
		execution((!@Guarded *).new(@(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Guarded *).new(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"Method parameter constraints are only allowed in class annotated with @Guarded";
}
