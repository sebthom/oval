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
package net.sf.oval.aspectj;

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
	@DeclareWarning("execution(@(@net.sf.oval.annotations.Constraint *) void (@net.sf.oval.annotations.Constrained *).*(..))")
	public static final String warning1 = "Method return value constraints are not allowed for methods without return values";

	/*
	 * Warn about return value constraints for non-getter methods in classes not annotated with @Constrained 
	 */
	@DeclareWarning("execution(@(@net.sf.oval.annotations.Constraint *) * (!@net.sf.oval.annotations.Constrained *).*(..)) && !(execution(!void *.is*()) || execution(!void *.get*()))")
	public static final String warning2 = "Method return value constraints for non-getter methods are only allowed in classes annotated with @Constrained";

	/*
	 * Warn about the @PreValidateObject annotation used on methods in classes not annotated with @Constrained
	 */
	@DeclareWarning("execution (@net.sf.oval.annotations.PreValidateThis * (!@net.sf.oval.annotations.Constrained *).*(..))")
	public static final String warning3 = "@PreValidateThis is only allowed in class annotated with @Constrained";

	/*
	 * Warn about the @PostValidateObject annotation used on methods and constructors in classes not annotated with @Constrained
	 */
	@DeclareWarning("execution (@net.sf.oval.annotations.PostValidateThis * (!@net.sf.oval.annotations.Constrained *).*(..)) || execution (@net.sf.oval.annotations.PostValidateObject (!@net.sf.oval.annotations.Constrained *).new(..))")
	public static final String warning4 = "@PostValidateThis is only allowed in classes annotated with @Constrainted";

	/*
	 * Warn about method parameter constraints in classes not annotated with @Constrained
	 */
	@DeclareWarning("execution(* (!@net.sf.oval.annotations.Constrained *).*(@(@net.sf.oval.annotations.Constraint *) *, ..)) || execution(* (!@net.sf.oval.annotations.Constrained *).*(*, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution(* (!@net.sf.oval.annotations.Constrained *).*(*, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution(* (!@net.sf.oval.annotations.Constrained *).*(*, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution(* (!@net.sf.oval.annotations.Constrained *).*(*, *, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution(* (!@net.sf.oval.annotations.Constrained *).*(*, *, *, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..))")
	public static final String warning5 = "Method parameter constraints are only allowed in class annotated with @Constrainted";

	/*
	 * Warn about constructor parameter constraints in classes not annotated with @Constrained
	 */
	@DeclareWarning("execution((!@net.sf.oval.annotations.Constrained *).new(@(@net.sf.oval.annotations.Constraint *) *, ..)) || execution((!@net.sf.oval.annotations.Constrained *).new(*, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution((!@net.sf.oval.annotations.Constrained *).new(*, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution((!@net.sf.oval.annotations.Constrained *).new(*, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution((!@net.sf.oval.annotations.Constrained *).new(*, *, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..)) || execution((!@net.sf.oval.annotations.Constrained *).new(*, *, *, *, *, @(@net.sf.oval.annotations.Constraint *) *, ..))")
	public static final String warning6 = "Method parameter constraints are only allowed in class annotated with @Constrainted";

}
