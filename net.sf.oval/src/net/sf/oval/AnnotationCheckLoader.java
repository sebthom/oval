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

import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.ReflectionException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public interface AnnotationCheckLoader
{
	<Constraint extends Annotation> AnnotationCheck<Constraint> loadCheck(
			Constraint constraint, OValContext context) throws ReflectionException;
}
