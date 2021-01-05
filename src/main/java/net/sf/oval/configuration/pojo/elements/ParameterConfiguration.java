/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration.pojo.elements;

import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;

/**
 * @author Sebastian Thomschke
 */
public class ParameterConfiguration extends ConfigurationElement {
   private static final long serialVersionUID = 1L;

   /**
    * the type of the parameter
    */
   public Class<?> type;

   /**
    * the checks for the parameter
    */
   public List<Check> checks;

   /**
    * the check exclusions for the parameter
    */
   public List<CheckExclusion> checkExclusions;

   public boolean hasCheckExclusions() {
      return checkExclusions != null && !checkExclusions.isEmpty();
   }

   public boolean hasChecks() {
      return checks != null && !checks.isEmpty();
   }
}
