/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;

import net.sf.oval.CheckExclusion;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * Interface for constraint checks that are configurable via annotations.
 * 
 * @author Sebastian Thomschke
 */
public interface AnnotationCheckExclusion<ExclusionAnnotation extends Annotation> extends CheckExclusion
{
	/**
	 * Configures the check exclusion based on the given exclusion annotation.
	 * @param exclusionAnnotation the exclusion annotation to use for configuration
	 * @throws InvalidConfigurationException in case of an illegal configuration setting
	 */
	void configure(ExclusionAnnotation exclusionAnnotation) throws InvalidConfigurationException;
}
