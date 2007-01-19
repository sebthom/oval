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
	 * Warn about constraints for void methods 
	 */
	@DeclareWarning("execution(@(@net.sf.oval.constraints.Constraint *) void (@net.sf.oval.guard.Guarded *).*(..))")
	public static final String warning1 = "Method return value constraints are not allowed for methods without return values";

	/*
	 * Warn about return value constraints for non-getter methods in classes not annotated with @Guarded 
	 */
	@DeclareWarning("execution(@(@net.sf.oval.constraints.Constraint *) * (!@net.sf.oval.guard.Guarded *).*(..)) && !(execution(!void *.is*()) || execution(!void *.get*()))")
	public static final String warning2 = "Method return value constraints for non-getter methods are only allowed in classes annotated with @Guarded";

	/*
	 * Warn about the @PreValidateObject annotation used on methods in classes not annotated with @Guarded
	 */
	@DeclareWarning("execution (@net.sf.oval.guard.PreValidateThis * (!@net.sf.oval.guard.Guarded *).*(..))")
	public static final String warning3 = "@PreValidateThis is only allowed in class annotated with @Guarded";

	/*
	 * Warn about the @PostValidateObject annotation used on methods and constructors in classes not annotated with @Guarded
	 */
	@DeclareWarning("execution (@net.sf.oval.guard.PostValidateThis * (!@net.sf.oval.guard.Guarded *).*(..)) || execution (@net.sf.oval.guard.PostValidateObject (!@net.sf.oval.guard.Guarded *).new(..))")
	public static final String warning4 = "@PostValidateThis is only allowed in classes annotated with @Guarded";

	/*
	 * Warn about method parameter constraints in classes not annotated with @Guarded
	 */
	@DeclareWarning("execution(* (!@net.sf.oval.guard.Guarded *).*(@(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution(* (!@net.sf.oval.guard.Guarded *).*(*, *, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..))")
	public static final String warning5 = "Method parameter constraints are only allowed in class annotated with @Guarded";

	/*
	 * Warn about constructor parameter constraints in classes not annotated with @Guarded
	 */
	@DeclareWarning("execution((!@net.sf.oval.guard.Guarded *).new(@(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..)) || execution((!@net.sf.oval.guard.Guarded *).new(*, *, *, *, *, @(@net.sf.oval.constraints.Constraint *) *, ..))")
	public static final String warning6 = "Method parameter constraints are only allowed in class annotated with @Guarded";

}
