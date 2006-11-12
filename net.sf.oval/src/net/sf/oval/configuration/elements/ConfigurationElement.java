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

/**
 * @author Sebastian Thomschke
 */
public class ConfigurationElement
{
	/**
	 * if set to false the checks defined here and in the child configuration elements will be added to the already registered checks for the respective contexts
	 * if set to true the already registered checks for the respective contexts will be replaced by the checks defined here and in the child configuration elements  
	 */
	public Boolean overwrite;
}
