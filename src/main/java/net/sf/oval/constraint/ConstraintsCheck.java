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
package net.sf.oval.constraint;

import java.lang.annotation.Annotation;
import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * Internal check holding multiple check instances of the same type, e.g. used for @Assert.List(...)
 *
 * @author Sebastian Thomschke
 */
public class ConstraintsCheck extends AbstractAnnotationCheck<Annotation> {
    private static final long serialVersionUID = 1L;

    public List<Check> checks;

    @Override
    protected ConstraintTarget[] getAppliesToDefault() {
        return new ConstraintTarget[] { ConstraintTarget.CONTAINER /*, ConstraintTarget.KEYS, ConstraintTarget.VALUES,
                                                                   ConstraintTarget.RECURSIVE*/ };
    }

    /**
     * <b>This method is not used.</b><br>
     * The validation of this special constraint is directly performed by the Validator class
     * 
     * @throws UnsupportedOperationException always thrown if this method is invoked
     */
    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
