/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration.pojo.elements;

import java.util.List;

import net.sf.oval.Check;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetConfiguration extends ConfigurationElement {
   private static final long serialVersionUID = 1L;

   public List<Check> checks;

   /**
    * the id of the constraint set
    */
   public String id;
}
