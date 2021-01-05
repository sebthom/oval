/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
