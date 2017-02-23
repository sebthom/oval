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
