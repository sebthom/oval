/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import net.sf.oval.guard.GuardAspect;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public aspect TestGuardAspect extends GuardAspect
{
    // only guard classes in the package net.sf.oval.test.guard that are annotated with @Guarded
    @Override
    protected pointcut scope(): within(net.sf.oval.test.guard.*) && @within(Guarded);

    public TestGuardAspect() {
        super();
    }
}
