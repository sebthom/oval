/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.pojo.elements;

import java.util.Set;

/**
 * @author Sebastian Thomschke
 * @author Chris Pheby - added {@link #inspectInterfaces}
 */
public class ClassConfiguration extends ConfigurationElement {
   private static final long serialVersionUID = 1L;

   /**
    * class type
    */
   public Class<?> type;

   /**
    * object level constraints configuration
    */
   public ObjectConfiguration objectConfiguration;

   /**
    * field constraints configuration
    */
   public Set<FieldConfiguration> fieldConfigurations;

   /**
    * constructor constraints configuration
    */
   public Set<ConstructorConfiguration> constructorConfigurations;

   /**
    * method constraints configuration
    */
   public Set<MethodConfiguration> methodConfigurations;

   /**
    * Automatically apply field constraints to the corresponding parameters
    * of constructors declared within the same class. A corresponding
    * parameter is a parameter with the same name and type as the field.
    */
   public Boolean applyFieldConstraintsToConstructors;

   /**
    * Automatically apply field constraints to the parameters of the
    * corresponding setter methods declared within the same class. A
    * corresponding setter method is a method following the JavaBean
    * convention and its parameter has as the same type as the field.
    */
   public Boolean applyFieldConstraintsToSetters;

   /**
    * Declares if parameter values of constructors and methods are expected to be not null.
    * This can be weakened by using the @net.sf.oval.constraint.exclusion.Nullable annotation on specific parameters.
    */
   public Boolean assertParametersNotNull;

   /**
    * Specifies if invariants are checked prior and after calls to
    * non-private methods and constructors.
    */
   public Boolean checkInvariants;

   /**
    * Specifies whether annotations can be applied to interfaces that this class implements,
    * supporting a documentation function
    */
   public Boolean inspectInterfaces;
}
