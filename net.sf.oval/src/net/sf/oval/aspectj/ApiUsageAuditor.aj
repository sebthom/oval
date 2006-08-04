/*
 * Created on 06.04.2006
 */
package net.sf.oval.aspectj;

import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.Constraint;
import net.sf.oval.annotations.PostValidateThis;
import net.sf.oval.annotations.PreValidateThis;

/**
 * @author Sebastian Thomschke
 *
 */
abstract aspect ApiUsageAuditor
{
	pointcut getterMethods(): execution(!void *.is*()) || execution(!void *.get*());

	/*
	 * Warn about constraints for void methods 
	 */
	declare warning: execution(@(@Constraint *) void (@Constrained *).*(..)): 
		"Method return value constraints are not allowed for methods without return values";

	/*
	 * Warn about return value constraints for non-getter methods in classes not annotated with @Constrained 
	 */
	declare warning: execution(@(@Constraint *) * (!@Constrained *).*(..)) && !getterMethods(): 
		"Method return value constraints for non-getter methods are only allowed in classes annotated with @Constrainted";

	/*
	 * Warn about the @PreValidateThis annotation used on methods in classes not annotated with @Constrained
	 */
	declare warning: execution (@PreValidateObject * (!@Constrained *).*(..)): 
		"@PreValidateThis is only allowed in class annotated with @Constrainted";

	/*
	 * Warn about the @PostValidateThis annotation used on methods and constructors in classes not annotated with @Constrained
	 */
	declare warning: execution (@PostValidateThis * (!@Constrained *).*(..)) || execution (@PostValidateObject (!@Constrained *).new(..)): 
		"@PostValidateThis is only allowed in classes annotated with @Constrained";

	/*
	 * Warn about method parameter constraints in classes not annotated with @Constrained
	 */
	declare warning:
		execution(* (!@Constrained *).*(@(@Constraint *) *, ..)) ||
		execution(* (!@Constrained *).*(*, @(@Constraint *) *, ..)) ||
		execution(* (!@Constrained *).*(*, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Constrained *).*(*, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Constrained *).*(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution(* (!@Constrained *).*(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"Method parameter constraints are only allowed in class annotated with @Constrained";

	/*
	 * Warn about constructor parameter constraints in classes not annotated with @Constrained
	 */
	declare warning:
		execution((!@Constrained *).new(@(@Constraint *) *, ..)) ||
		execution((!@Constrained *).new(*, @(@Constraint *) *, ..)) ||
		execution((!@Constrained *).new(*, *, @(@Constraint *) *, ..)) ||
		execution((!@Constrained *).new(*, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Constrained *).new(*, *, *, *, @(@Constraint *) *, ..)) ||
		execution((!@Constrained *).new(*, *, *, *, *, @(@Constraint *) *, ..)): 
		"Method parameter constraints are only allowed in class annotated with @Constrained";
}
