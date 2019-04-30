/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;

import net.sf.oval.Check;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * Interface for constraint checks that are configurable via annotations.
 *
 * @author Sebastian Thomschke
 */
public interface AnnotationCheck<ConstraintAnnotation extends Annotation> extends Check {

   /**
    * Configures the check based on the given constraint annotation.
    *
    * @param constraintAnnotation the constraint annotation to use for configuration
    * @throws InvalidConfigurationException in case of an illegal configuration setting
    */
   void configure(ConstraintAnnotation constraintAnnotation) throws InvalidConfigurationException;
}
