/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import static net.sf.oval.Validator.*;

import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintsViolatedAdapter implements ConstraintsViolatedListener {
   private final List<ConstraintsViolatedException> violationExceptions = getCollectionFactory().createList(8);
   private final List<ConstraintViolation> violations = getCollectionFactory().createList(8);

   public void clear() {
      violationExceptions.clear();
      violations.clear();
   }

   public List<ConstraintsViolatedException> getConstraintsViolatedExceptions() {
      return violationExceptions;
   }

   public List<ConstraintViolation> getConstraintViolations() {
      return violations;
   }

   @Override
   public void onConstraintsViolatedException(final ConstraintsViolatedException exception) {
      violationExceptions.add(exception);
      violations.addAll(ArrayUtils.asList(exception.getConstraintViolations()));
   }
}
