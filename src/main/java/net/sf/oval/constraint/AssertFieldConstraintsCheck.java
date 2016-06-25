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
package net.sf.oval.constraint;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertFieldConstraintsCheck extends AbstractAnnotationCheck<AssertFieldConstraints> {
    private static final long serialVersionUID = 1L;

    private String fieldName;

    private Class<?> declaringClass;

    @Override
    public void configure(final AssertFieldConstraints constraintAnnotation) {
        super.configure(constraintAnnotation);
        setFieldName(constraintAnnotation.value());
        setDeclaringClass(constraintAnnotation.declaringClass());
    }

    @Override
    protected ConstraintTarget[] getAppliesToDefault() {
        return new ConstraintTarget[] { ConstraintTarget.CONTAINER };
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getErrorCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSeverity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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

    public void setDeclaringClass(final Class<?> declaringClass) {
        this.declaringClass = declaringClass == Void.class ? null : declaringClass;
    }

    @Override
    public void setErrorCode(final String errorCode) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void setMessage(final String message) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSeverity(final int severity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
