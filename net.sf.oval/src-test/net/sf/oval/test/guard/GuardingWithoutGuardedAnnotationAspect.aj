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
package net.sf.oval.test.guard;

import net.sf.oval.guard.GuardAspect;
import net.sf.oval.guard.IsGuarded;
import net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.TestEntity;

/**
 * @author Sebastian Thomschke
 */
public aspect GuardingWithoutGuardedAnnotationAspect extends GuardAspect
{
	protected pointcut scope(): within(TestEntity);

	declare parents: TestEntity implements IsGuarded;
	
	public GuardingWithoutGuardedAnnotationAspect()
	{
		super();
	}
}
