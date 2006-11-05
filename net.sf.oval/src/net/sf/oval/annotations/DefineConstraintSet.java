/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines a constraint set
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DefineConstraintSet
{
	/**
	 * The local id of the constraint set.<br>
	 * The constraint set id is <code>[class name] + "." + [local id]</code>
	 * where [class name] is the name of the class in which this annotation is used.
	 *  
	 * @return
	 */
	String value();
}