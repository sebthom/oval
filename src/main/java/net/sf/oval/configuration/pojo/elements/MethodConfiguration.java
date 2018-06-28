/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.pojo.elements;

import java.util.List;

/**
 * @author Sebastian Thomschke
 */
public class MethodConfiguration extends ConfigurationElement {
   private static final long serialVersionUID = 1L;

   public String name;
   public Boolean isInvariant;

   public List<ParameterConfiguration> parameterConfigurations;
   public MethodReturnValueConfiguration returnValueConfiguration;

   public Boolean postCheckInvariants;
   public MethodPostExecutionConfiguration postExecutionConfiguration;

   public Boolean preCheckInvariants;
   public MethodPreExecutionConfiguration preExecutionConfiguration;
}
