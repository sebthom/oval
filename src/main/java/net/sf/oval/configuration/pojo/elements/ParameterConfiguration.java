/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.pojo.elements;

import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;

/**
 * @author Sebastian Thomschke
 */
public class ParameterConfiguration extends ConfigurationElement {
    private static final long serialVersionUID = 1L;

    /**
     * the type of the parameter
     */
    public Class<?> type;

    /**
     * the checks for the parameter
     */
    public List<Check> checks;

    /**
     * the check exclusions for the parameter
     */
    public List<CheckExclusion> checkExclusions;

    public boolean hasCheckExclusions() {
        return checkExclusions != null && checkExclusions.size() > 0;
    }

    public boolean hasChecks() {
        return checks != null && checks.size() > 0;
    }
}
