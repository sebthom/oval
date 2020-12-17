/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Validatable;

/**
 * This annotation needs to be applied to classes where
 * OVal's programming by contract features shall be used.<br>
 * <br>
 * The GuardAspect will weave the required AOP code into all
 * classes annotated with @Guarded.
 *
 * @author Sebastian Thomschke
 * @author Chris Pheby - added {@link #inspectInterfaces()}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Guarded {
   /**
    * Automatically apply field constraints to the corresponding parameters of constructors declared within the same class.
    * A corresponding parameter is a parameter with the same name and type as the field.
    */
   boolean applyFieldConstraintsToConstructors() default false;

   /**
    * Automatically apply field constraints to the single parameter of the corresponding setter methods declared within the same class.
    * A corresponding setter method is a method following the JavaBean convention and its parameter has as the same type as the field.
    */
   boolean applyFieldConstraintsToSetters() default false;

   /**
    * Declares if parameter values of constructors and methods are expected to be not null.
    * This can be weakened by using the @net.sf.oval.constraint.exclusion.Nullable annotation on specific parameters.
    */
   boolean assertParametersNotNull() default false;

   /**
    * Declares if invariants are automatically checked after constructor execution and prior and after calls to non-private methods.
    */
   boolean checkInvariants() default true;

   /**
    * Declares if annotations can be applied to interfaces that this class implements - supporting a documentation function.
    *
    * @deprecated use {@link Validatable#inspectInterfaces()}.
    */
   @Deprecated
   boolean inspectInterfaces() default true;
}
