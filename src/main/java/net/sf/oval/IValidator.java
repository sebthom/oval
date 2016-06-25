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

import java.lang.reflect.Field;
import java.util.List;

import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.ValidationFailedException;

/**
 * An interface implemented by Validator for easier mocking.
 * 
 * @author Sebastian Thomschke
 */
public interface IValidator {

    /**
     * validates the field and getter constrains of the given object
     * and throws an ConstraintsViolatedException if any constraint
     * violations are detected
     * 
     * @param validatedObject the object to validate, cannot be null
     * @throws ConstraintsViolatedException
     * @throws ValidationFailedException
     * @throws IllegalArgumentException if <code>validatedObject == null</code>
     */
    void assertValid(final Object validatedObject) throws IllegalArgumentException, ValidationFailedException, ConstraintsViolatedException;

    /**
     * Validates the give value against the defined field constraints and throws
     * an ConstraintsViolatedException if any constraint violations are detected.<br>
     * 
     * @param validatedObject the object to validate, cannot be null
     * @param validatedField the field to validate, cannot be null
     * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>field == null</code>
     * @throws ConstraintsViolatedException
     * @throws ValidationFailedException
     */
    void assertValidFieldValue(final Object validatedObject, final Field validatedField, final Object fieldValueToValidate) throws IllegalArgumentException,
            ValidationFailedException, ConstraintsViolatedException;

    /**
     * validates the field and getter constrains of the given object
     *
     * @param validatedObject the object to validate, cannot be null
     * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
     * @throws ValidationFailedException
     * @throws IllegalArgumentException if <code>validatedObject == null</code>
     */
    List<ConstraintViolation> validate(final Object validatedObject) throws IllegalArgumentException, ValidationFailedException;

    /**
     * validates the field and getter constrains of the given object
     *
     * @param validatedObject the object to validate, cannot be null
     * @param profiles constraint profiles to validate against, by default the globally enabled profiles are used that.
     * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
     * @throws ValidationFailedException
     * @throws IllegalArgumentException if <code>validatedObject == null</code>
     */
    List<ConstraintViolation> validate(final Object validatedObject, String... profiles) throws IllegalArgumentException, ValidationFailedException;

    /**
     * Validates the give value against the defined field constraints.<br>
     * 
     * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
     * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>validatedField == null</code>
     * @throws ValidationFailedException
     */
    List<ConstraintViolation> validateFieldValue(final Object validatedObject, final Field validatedField, final Object fieldValueToValidate)
            throws IllegalArgumentException, ValidationFailedException;

}