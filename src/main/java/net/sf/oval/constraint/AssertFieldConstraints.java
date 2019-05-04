/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.configuration.annotation.Constraints;

/**
 * Check if the value satisfies the constraints defined for the specified field.
 *
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Constraint(checkWith = AssertFieldConstraintsCheck.class)
@Repeatable(AssertFieldConstraints.List.class)
public @interface AssertFieldConstraints {
   @Documented
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.PARAMETER, ElementType.METHOD})
   @Constraints
   public @interface List {
      /**
       * The AssertFieldConstraints constraints.
       */
      AssertFieldConstraints[] value();

      /**
       * Formula returning <code>true</code> if this constraint shall be evaluated and
       * <code>false</code> if it shall be ignored for the current validation.
       * <p>
       * <b>Important:</b> The formula must be prefixed with the name of the scripting language that is used.
       * E.g. <code>groovy:_this.amount > 10</code>
       * <p>
       * Available context variables are:<br>
       * <b>_this</b> -&gt; the validated bean<br>
       * <b>_value</b> -&gt; the value to validate (e.g. the field value, parameter value, method return value,
       * or the validated bean for object level constraints)
       */
      String when() default "";
   }

   /**
    * The class in which the field is declared. If omitted the current class and it's super
    * classes are searched for a field with the given name.
    * The default value Void.class means the current class.
    */
   Class<?> declaringClass() default Void.class;

   /**
    * The associated constraint profiles.
    */
   String[] profiles() default {};

   /**
    * Name of the field. If not specified, the constraints of the field with the same name as
    * the annotated constructor/method parameter are applied.
    */
   String value() default "";

   /**
    * Formula returning <code>true</code> if this constraint shall be evaluated and
    * <code>false</code> if it shall be ignored for the current validation.
    * <p>
    * <b>Important:</b> The formula must be prefixed with the name of the scripting language that is used.
    * E.g. <code>groovy:_this.amount > 10</code>
    * <p>
    * Available context variables are:<br>
    * <b>_this</b> -&gt; the validated bean<br>
    * <b>_value</b> -&gt; the value to validate (e.g. the field value, parameter value, method return value,
    * or the validated bean for object level constraints)
    */
   String when() default "";
}
