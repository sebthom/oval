/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;

import net.sf.oval.CheckExclusion;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * Interface for constraint checks that are configurable via annotations.
 *
 * @author Sebastian Thomschke
 */
public interface AnnotationCheckExclusion<ExclusionAnnotation extends Annotation> extends CheckExclusion {

   /**
    * Configures the check exclusion based on the given exclusion annotation.
    *
    * @param exclusionAnnotation the exclusion annotation to use for configuration
    * @throws InvalidConfigurationException in case of an illegal configuration setting
    */
   void configure(ExclusionAnnotation exclusionAnnotation) throws InvalidConfigurationException;
}
