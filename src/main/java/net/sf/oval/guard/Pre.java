/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.guard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before the annotated method is executed the expression is evaluated.<br>
 * This evaluation happens <u>after</u> the single parameter constraints were validated
 * and only if no parameter constraint violations were detected.
 * <br>
 * If constraint violations occur, the annotated method will not be executed
 * instead it will throw a ConstraintsViolatedException exception.
 *
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Pre {

   /**
    * error code passed to the ConstraintViolation object
    */
   String errorCode() default "net.sf.oval.guard.Pre";

   /**
    * formula in the given expression language describing the constraint. the formula must return true if the constraint is satisfied.
    * <br>
    * available variables are:<br>
    * <b>_this</b> -&gt; the validated bean<br>
    * <b>_args[]</b> -&gt; the current parameter values<br>
    * additionally variables matching the parameter names are available<br>
    */
   String expr();

   /**
    * the expression language that is used
    */
   String lang();

   /**
    * message to be used for the ContraintsViolatedException
    *
    * @see net.sf.oval.exception.ConstraintsViolatedException
    */
   String message() default "net.sf.oval.guard.Pre.violated";

   /**
    * The associated constraint profiles.
    */
   String[] profiles() default {};

   /**
    * severity passed to the ConstraintViolation object
    */
   int severity() default 0;
}
