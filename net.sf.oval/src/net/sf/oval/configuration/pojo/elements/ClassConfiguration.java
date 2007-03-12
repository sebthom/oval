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
	 * specifies if constraints defined for fields are applied to the
	 * parameter of the corresponding setter method
	 */
	public Boolean applyFieldConstraintsToSetter;
	
	/**
	 * Specifies if invariants are checked prior and after
	 * calls to non-private methods and constructors.
	 */
	public Boolean checkInvariants; 
}
