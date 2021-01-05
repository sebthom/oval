/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint.exclusion;

import net.sf.oval.Check;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheckExclusion;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Sebastian Thomschke
 */
public class NullableExclusion extends AbstractAnnotationCheckExclusion<Nullable> {
   private static final long serialVersionUID = 1L;

   @Override
   public boolean isCheckExcluded(final Check check, final Object validatedObject, final Object valueToValidate, final OValContext context,
      final Validator validator) throws OValException {
      return check instanceof NotNullCheck;
   }
}
