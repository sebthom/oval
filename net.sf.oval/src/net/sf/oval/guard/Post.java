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
 * After the annotated method has been executed the condition is evaluated.<br>
 * <br>
 * In case of constraint violations the method will throw an ConstraintsViolatedException.
 *
 * @author Sebastian Thomschke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Post
{
	/**
	 * Formula in the given expression language describing the constraint. the formula must return true if the constraint is satisfied.
	 * <br>
	 * available variables are:<br>
	 * <b>_args[]</b> -&gt; the current parameter values<br>
	 * <b>_old</b> -&gt; the old values<br>
	 * <b>_returns</b> -&gt; the method's return value
	 * <b>_this</b> -&gt; the validated bean<br>
	 * additionally variables named accordingly to the parameters are available<br>
	 */
	String expr();

	/**
	 * Formula that is evaluated prior method execution.<br>
	 * The returned value can later be accessed in the constraint expression via the variable <b>_old</b>
	 */
	String old() default "";

	/**
	 * the expression language that is used
	 */
	String lang();

	/**
	 * message to be used for the ContraintsViolatedException
	 * 
	 * @see ConstraintsViolatedException
	 */
	String message() default "net.sf.oval.guard.Post.violated";

	/**
	 * The associated validation profiles.
	 */
	String[] profiles() default {};
}
