/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
