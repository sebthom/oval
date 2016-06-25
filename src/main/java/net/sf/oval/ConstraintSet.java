/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval;

import java.util.Collection;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSet {
    private Collection<Check> checks;

    private final String id;

    public ConstraintSet(final String id) {
        this.id = id;
    }

    /**
     * @return the checks
     */
    public Collection<Check> getChecks() {
        return checks;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param checks the checks to set
     */
    public void setChecks(final Collection<Check> checks) {
        this.checks = checks;
    }
}
