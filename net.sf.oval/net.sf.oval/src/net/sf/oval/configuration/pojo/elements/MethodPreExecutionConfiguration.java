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

import java.util.List;

import net.sf.oval.guard.PreCheck;

/**
 * @author Sebastian Thomschke
 */
public class MethodPreExecutionConfiguration extends ConfigurationElement
{
	private static final long serialVersionUID = 1L;

	/**
	 * checks that need to be verified after method execution
	 */
	public List<PreCheck> checks;
}
