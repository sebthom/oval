/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
package net.sf.oval.configuration.pojo.elements;

import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public class ClassConfiguration extends ConfigurationElement
{
	private static final long serialVersionUID = 1L;

	/**
	 * class type
	 */
	public Class< ? > type;

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
	 * Automatically apply field constraints to 
	 * the corresponding parameters of constructors
	 * declared within the same class. A corresponding paramater
	 * is a parameter with the same name and type as the field.
	 */
	public Boolean applyFieldConstraintsToConstructors;

	/**
	 * Automatically apply field constraints to the
	 * parameters of the corresponding setter methods 
	 * declared within the same class. A corresponding setter
	 * method is a method following the JavaBean convention and
	 * its parameter has as the same type as the field.
	 */
	public Boolean applyFieldConstraintsToSetters;
		
	/**
	 * Specifies if invariants are checked prior and after
	 * calls to non-private methods and constructors.
	 */
	public Boolean checkInvariants; 
}
