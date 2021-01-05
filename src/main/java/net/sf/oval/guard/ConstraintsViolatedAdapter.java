/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.guard;

import static net.sf.oval.Validator.*;

import java.util.Collections;
import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.exception.ConstraintsViolatedException;

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
      Collections.addAll(violations, exception.getConstraintViolations());
   }
}
