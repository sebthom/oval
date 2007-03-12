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
package net.sf.oval.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks if the value satsifies the constraints defined for the specified field.
 * 
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Constraint(checkWith = AssertFieldConstraintsCheck.class)
public @interface AssertFieldConstraints
{
	/**
	 * The class in which the field is declared. If omitted the current class and it's super classes are searched for a field with the given name.
	 * The default value Object.class means the current class.
	 */
	Class declaringClass() default Object.class;

	/**
	 * Name of the field. If not specified, the constraints of the field with the same name as the annotated constructor/method parameter are applied.
	 */
	String value() default "";

	/**
	 * The associated validation profiles.
	 */
	String[] profiles() default {};
}
