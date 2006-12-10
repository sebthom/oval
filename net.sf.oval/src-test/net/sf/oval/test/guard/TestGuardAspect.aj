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
package net.sf.oval.test.guard;

import net.sf.oval.Guard;
import net.sf.oval.Validator;
import net.sf.oval.aspectj.GuardAspect;
import net.sf.oval.aspectj.ParameterNameResolverAspectJImpl;

/**
 * @author Sebastian Thomschke
 */
public aspect TestGuardAspect extends GuardAspect
{
	public final static Validator validator = new Validator();
	public final static Guard guard = new Guard(validator);

	static TestGuardAspect INSTANCE;

	public TestGuardAspect()
	{
		super(guard);
		INSTANCE = this;
		validator.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
	}
}
