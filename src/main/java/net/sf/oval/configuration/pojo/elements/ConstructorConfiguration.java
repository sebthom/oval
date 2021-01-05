/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration.pojo.elements;

import java.util.List;

/**
 * @author Sebastian Thomschke
 */
public class ConstructorConfiguration extends ConfigurationElement {
   private static final long serialVersionUID = 1L;

   public List<ParameterConfiguration> parameterConfigurations;

   public Boolean postCheckInvariants;
}
