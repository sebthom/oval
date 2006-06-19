/*
 * Created on 06.04.2006
 */
package net.sf.oval.aspectj;

import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.Constraint;
import net.sf.oval.annotations.PostValidateObject;
import net.sf.oval.annotations.PreValidateObject;

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
	 * Warn about the @PreValidateObject annotation used on methods in classes not annotated with @Constrained
	 */
	declare warning: execution (@PreValidateObject * (!@Constrained *).*(..)): 
		"@PreValidateObject is only allowed in class annotated with @Constrainted";

	/*
	 * Warn about the @PostValidateObject annotation used on methods and constructors in classes not annotated with @Constrained
	 */
	declare warning: execution (@PostValidateObject * (!@Constrained *).*(..)) || execution (@PostValidateObject (!@Constrained *).new(..)): 
		"@PostValidateObject is only allowed in classes annotated with @Constrainted";

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
		"Method parameter constraints are only allowed in class annotated with @Constrainted";

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
		"Method parameter constraints are only allowed in class annotated with @Constrainted";
}
