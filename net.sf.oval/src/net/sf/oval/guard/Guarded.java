/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation needs to be applied to classes where 
 * OVal's programming by contract features shall be used.<br>
 * <br>
 * The GuardAspect will weave the required AOP code into all
 * classes annotated with @Guarded.
 * 
 * @author Sebastian Thomschke
 * @see net.sf.oval.guard.GuardAspect
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Guarded
{
	/**
	 * apply constraints specified for fields to the parameter of the corresponding setter methods
	 * <br><br>
	 */
	boolean applyFieldConstraintsToSetter() default false;

	/**
	 * check invariants (constraints on fields and getter method return values) BEFORE any method
	 * is executed from outside the current class
	 */
	boolean autoPreValidateThis() default false;

	/**
	 * check invariants (constraints on fields and getter method return values) AFTER any method
	 * is executed from outside the current class
	 */
	boolean autoPostValidateThis() default false;
}
