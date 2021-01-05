/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to apply class-level validation configurations.
 *
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Validatable {

   /**
    * Declares if annotations applied to interfaces are validated.
    */
   boolean inspectInterfaces() default true;

   /**
    * List of interfaces that shall not be inspected.
    *
    * Only applicable if {@link #inspectInterfaces()} is set to <code>true</code>.
    */
   Class<?>[] excludedInterfaces() default {};

   /**
    * If specified only these interfaces are inspected otherwise all implemented interfaces.
    *
    * Only applicable if {@link #inspectInterfaces()} is set to <code>true</code>.
    */
   Class<?>[] includedInterfaces() default {};
}
