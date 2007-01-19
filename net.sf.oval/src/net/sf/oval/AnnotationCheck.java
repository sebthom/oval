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
package net.sf.oval;

import java.lang.annotation.Annotation;

/**
 * Interface for classes that can check/validate if the corresponding 
 * constraint annotation is satisfied.
 * 
 * @author Sebastian Thomschke
 */
public interface AnnotationCheck<ConstraintAnnotation extends Annotation> extends Check
{
	/**
	 * @return the constraint annotation used to configure this check
	 */
	ConstraintAnnotation getConstraintAnnotation();

	void configure(ConstraintAnnotation constraintAnnotation);
}
