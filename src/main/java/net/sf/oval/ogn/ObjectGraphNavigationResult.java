/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.ogn;

import java.lang.reflect.AccessibleObject;

/**
 * @author Sebastian Thomschke
 */
public class ObjectGraphNavigationResult {
    public final Object root;

    public final String path;

    public final Object targetParent;

    /**
     * field or method
     */
    public final AccessibleObject targetAccessor;

    /**
     * accessor's value
     */
    public final Object target;

    public ObjectGraphNavigationResult(final Object root, final String path, final Object targetParent, final AccessibleObject targetAccessor,
            final Object target) {
        this.root = root;
        this.path = path;
        this.targetParent = targetParent;
        this.targetAccessor = targetAccessor;
        this.target = target;
    }
}
