/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import net.sf.oval.guard.IsGuarded;

/**
 * @author Sebastian Thomschke
 */
public aspect GuardingWithoutGuardedAnnotationAspect extends net.sf.oval.guard.GuardAspect
{
	/* 
	 * the scope of the aspect are all inner classes of net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest
	 */
	protected pointcut scope(): within(net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.*);

	/* 
	 * add the IsGuarded marker interface to all inner classes 
	 * of net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest
	 */
	declare parents: net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.* implements IsGuarded;
	
	public GuardingWithoutGuardedAnnotationAspect()
	{
		super();
	}
}
