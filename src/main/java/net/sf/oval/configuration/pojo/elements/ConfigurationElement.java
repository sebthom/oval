/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.pojo.elements;

import java.io.Serializable;

/**
 * @author Sebastian Thomschke
 */
public class ConfigurationElement implements Serializable {
   private static final long serialVersionUID = 1L;

   /**
    * If set to <code>false</code> the checks defined here and in the child configuration elements will be added to
    * the already registered checks for the respective contexts.
    * 
    * If set to <code>true</code> the already registered checks for the respective contexts will be replaced by the
    * checks defined here and in the child configuration elements.
    * 
    * If not set (<code>null</code>) -> interpreted as false.
    */
   public Boolean overwrite;
}
