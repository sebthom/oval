/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.configuration.elements;

import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public class ClassConfiguration extends ConfigurationElement
{
	private static final long serialVersionUID = 1L;
	
	public Class< ? > type;
	public Set<FieldConfiguration> fieldConfigurations;
	public Set<ConstructorConfiguration> constructorConfigurations;
	public Set<MethodConfiguration> methodConfigurations;
	public Boolean applyFieldConstraintsToSetter;
}
