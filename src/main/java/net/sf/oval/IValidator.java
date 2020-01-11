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
    * @throws IllegalArgumentException if <code>validatedObject == null</code>
    */
   void assertValid(Object validatedObject) throws IllegalArgumentException, ValidationFailedException, ConstraintsViolatedException;

   /**
    * Validates the give value against the defined field constraints and throws
    * an ConstraintsViolatedException if any constraint violations are detected.<br>
    *
    * @param validatedObject the object to validate, cannot be null
    * @param validatedField the field to validate, cannot be null
    * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>field == null</code>
    */
   void assertValidFieldValue(Object validatedObject, Field validatedField, Object fieldValueToValidate) throws IllegalArgumentException,
      ValidationFailedException, ConstraintsViolatedException;

   /**
    * validates the field and getter constrains of the given object
    *
    * @param validatedObject the object to validate, cannot be null
    * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
    * @throws IllegalArgumentException if <code>validatedObject == null</code>
    */
   List<ConstraintViolation> validate(Object validatedObject) throws IllegalArgumentException, ValidationFailedException;

   /**
    * validates the field and getter constrains of the given object
    *
    * @param validatedObject the object to validate, cannot be null
    * @param profiles constraint profiles to validate against, by default the globally enabled profiles are used that.
    * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
    * @throws IllegalArgumentException if <code>validatedObject == null</code>
    */
   List<ConstraintViolation> validate(Object validatedObject, String... profiles) throws IllegalArgumentException, ValidationFailedException;

   /**
    * Validates the give value against the defined field constraints.<br>
    *
    * @return a list with the detected constraint violations. if no violations are detected an empty list is returned
    * @throws IllegalArgumentException if <code>validatedObject == null</code> or <code>validatedField == null</code>
    */
   List<ConstraintViolation> validateFieldValue(Object validatedObject, Field validatedField, Object fieldValueToValidate) throws IllegalArgumentException,
      ValidationFailedException;

}
