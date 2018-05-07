/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class CustomXMLConstraintCheck extends AbstractAnnotationCheck<Constraint> {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Value must have more than 4 characters!";
    }

    @Override
    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
        if (valueToValidate == null)
            return true;
        return valueToValidate.toString().length() > 4;
    }
}