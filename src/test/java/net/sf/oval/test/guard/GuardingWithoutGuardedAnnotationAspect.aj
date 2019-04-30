/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import net.sf.oval.guard.IsGuarded;

/**
 * @author Sebastian Thomschke
 */
public aspect GuardingWithoutGuardedAnnotationAspect extends net.sf.oval.guard.GuardAspect {
    /*
     * the scope of the aspect are all inner classes of net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest
     */
    @Override
    protected pointcut scope(): within(net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.*);

    /*
     * add the IsGuarded marker interface to all inner classes
     * of net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest
     */
    declare parents: net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.* implements IsGuarded;

    public GuardingWithoutGuardedAnnotationAspect() {
        super();
    }
}
