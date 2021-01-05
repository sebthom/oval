/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
