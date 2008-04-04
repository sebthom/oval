/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

/**
 * @author Sebastian Thomschke
 */
public class MethodConfiguration extends ConfigurationElement
{
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
