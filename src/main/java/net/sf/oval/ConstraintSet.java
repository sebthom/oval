/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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

   public Collection<Check> getChecks() {
      return checks;
   }

   public String getId() {
      return id;
   }

   public void setChecks(final Collection<Check> checks) {
      this.checks = checks;
   }
}
