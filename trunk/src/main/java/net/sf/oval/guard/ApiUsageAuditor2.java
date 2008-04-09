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

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareWarning;

/**
 * This is an annotations based version of the ApiUsageAuditor aspect
 *
 * @author Sebastian Thomschke
 */
@Aspect
public abstract class ApiUsageAuditor2
{
	/*
	 * Rule 1: Warn about return value constraints for void methods 
	 */
	@DeclareWarning("execution(@(@net.sf.oval.constraints.Constraint *) void *.*(..))")
	public static final String rule1 = "OVal API usage violation 1: Method return value constraints are not allowed for methods without return values";

	/*
	 * Rule 2: Warn about return value constraints for non-void, parameterized methods in classes that are not guarded 
	 */
	@DeclareWarning("execution(@(@net.sf.oval.constraints.Constraint *) !void (!@net.sf.oval.guard.Guarded *).*(*,..))")
	public static final String rule2 = "OVal API usage violation 2: Method return value constraints for parameterized methods are only allowed in guarded classes";

	/*
	 * Rule 3: Warn about return value constraints for non-void, non-parameterized methods missing the @Invariant annotation in classes 
	 * that are not guarded
	 */
	@DeclareWarning("execution(!@net.sf.oval.configuration.annotation.IsInvariant @(@net.sf.oval.constraints.Constraint *) !void (!@net.sf.oval.guard.Guarded *).*())")
	public static final String rule3 = "OVal API usage violation 3: Method return value constraints are only allowed if the method is annotated with @IsInvariant or the declaring class is guarded";

	/*
	 * Rule 4: Warn about the @PreValidateThis annotation used on methods in classes that are not guarded
	 */
	@DeclareWarning("execution (@net.sf.oval.guard.PreValidateThis * (!@net.sf.oval.guard.Guarded *).*(..))")
	public static final String rule4 = "OVal API usage violation 4: @PreValidateThis is only allowed in guarded classes";

	/*
	 * Rule 5: Warn about the @PostValidateObject annotation used on methods and constructors in classes not annotated with @Guarded
	 */
	@DeclareWarning("execution (@net.sf.oval.guard.PostValidateThis * (!@net.sf.oval.guard.Guarded *).*(..)) || execution (@net.sf.oval.guard.PostValidateObject (!@net.sf.oval.guard.Guarded *).new(..))")
	public static final String rule5 = "OVal API usage violation 5: @PostValidateThis is only allowed in guarded classes";

	/*
	 * Rule 6: Warn about method parameter constraints in classes that are not guarded
	 */
	@DeclareWarning("execution(* (!@net.sf.oval.guard.Guarded *).*(@(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..))")
	public static final String rule6 = "OVal API usage violation 6: Method parameter constraints are only allowed in guarded classes";

	/*
	 * Rule 7: Warn about constructor parameter constraints in classes that are not guarded
	 */
	@DeclareWarning("execution((!@net.sf.oval.guard.Guarded *).new(@(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..))")
	public static final String rule7 = "OVal API usage violation 7: Method parameter constraints are only allowed in guarded classes";

}
