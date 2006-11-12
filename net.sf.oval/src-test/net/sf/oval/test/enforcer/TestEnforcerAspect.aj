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
package net.sf.oval.test.enforcer;

import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.Validator;
import net.sf.oval.aspectj.ConstraintsEnforcerAspect;

/**
 * @author Sebastian Thomschke
 */
public aspect TestEnforcerAspect extends ConstraintsEnforcerAspect
{
	public final static Validator validator = new Validator();
	public final static ConstraintsEnforcer constraintsEnforcer = new ConstraintsEnforcer(validator);

	public TestEnforcerAspect()
	{
		super(constraintsEnforcer);
	}
}
