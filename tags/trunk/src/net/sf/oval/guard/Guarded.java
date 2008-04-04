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
	 * Automatically apply field constraints to 
	 * the corresponding parameters of constructors
	 * declared within the same class. A corresponding paramater
	 * is a parameter with the same name and type as the field.
	 */
	boolean applyFieldConstraintsToConstructors() default false;

	/**
	 * Automatically apply field constraints to the
	 * parameters of the corresponding setter methods 
	 * declared within the same class. A corresponding setter
	 * method is a method following the JavaBean convention and
	 * its parameter has as the same type as the field.
	 */
	boolean applyFieldConstraintsToSetters() default false;

	/**
	 * Specifies if invariants are checked after constructor
	 * execution and prior and after calls to non-private methods.
	 */
	boolean checkInvariants() default true;
}
