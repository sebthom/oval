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
package net.sf.oval.internal.util;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

/**
 * @author Sebastian Thomschke
 */
public final class SetAccessibleAction implements PrivilegedAction<Object> {
    private final AccessibleObject ao;

    public SetAccessibleAction(final AccessibleObject ao) {
        this.ao = ao;
    }

    public Object run() {
        ao.setAccessible(true);
        return null;
    }
}