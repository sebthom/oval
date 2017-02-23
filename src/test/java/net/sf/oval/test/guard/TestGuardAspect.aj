/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import net.sf.oval.guard.GuardAspect;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public aspect TestGuardAspect extends GuardAspect
{
	// only guard classes in the package net.sf.oval.test.guard that are annotated with @Guarded
	protected pointcut scope(): within(net.sf.oval.test.guard.*) && @within(Guarded);

	public TestGuardAspect()
	{
		super();
	}
}
