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
package net.sf.oval.context;

/**
 * @author Sebastian Thomschke
 */
public class ClassContext extends OValContext {
    private static final long serialVersionUID = 1L;

    private final Class<?> clazz;

    public ClassContext(final Class<?> clazz) {
        this.clazz = clazz;
        compileTimeType = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return clazz.getName();
    }
}
