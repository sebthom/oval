/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *     Chris Pheby - inspectInterfaces
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
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Guarded
{
	/**
	 * Automatically apply field constraints to the corresponding parameters of
	 * constructors declared within the same class.
	 * A corresponding parameter is a parameter with the same name and type as the field.
	 */
	boolean applyFieldConstraintsToConstructors() default false;

	/**
	 * Automatically apply field constraints to the single parameter of the corresponding 
	 * setter methods declared within the same class.
	 * A corresponding setter method is a method following the JavaBean convention and
	 * its parameter has as the same type as the field.
	 */
	boolean applyFieldConstraintsToSetters() default false;

	/**
	 * Declares if parameter values of constructors and methods are expected to be not null.
	 * This can be weakened by using the @net.sf.oval.constraint.exclusion.Nullable annotation on specific parameters.
	 */
	boolean assertParametersNotNull() default false;

	/**
	 * Declares if invariants are automatically checked after constructor execution and 
	 * prior and after calls to non-private methods.
	 */
	boolean checkInvariants() default true;

	/**
	 * Declares if annotations can be applied to interfaces that this class implements - supporting a documentation
	 * function
	 */
	boolean inspectInterfaces() default false;
}
