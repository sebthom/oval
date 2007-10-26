/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
package net.sf.oval.integration.spring;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ValidationFailedException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author Sebastian Thomschke
 */
public class SpringValidator implements org.springframework.validation.Validator, InitializingBean
{
	private final static Logger LOG = Logger.getLogger(SpringValidator.class.getName());

	private Validator validator;

	public void afterPropertiesSet() throws Exception
	{
		Assert.notNull(validator, "Property [validator] must be set");
	}

	/**
	 * @return the validator
	 */
	public Validator getValidator()
	{
		return validator;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(final Validator validator)
	{
		this.validator = validator;
	}

	public boolean supports(final Class clazz)
	{
		return true;
	}

	public void validate(final Object objectToValidate, final Errors errors)
	{
		try
		{
			for (final ConstraintViolation violation : validator.validate(objectToValidate))
			{
				final OValContext ctx = violation.getContext();
				final String errorCode = violation.getClass().getName();
				final String errorMessage = violation.getMessage();

				if (ctx instanceof FieldContext)
				{
					final String fieldName = ((FieldContext) ctx).getField().getName();
					errors.rejectValue(fieldName, errorCode, errorMessage);
				}
				else
				{
					errors.reject(errorCode, errorMessage);
				}
			}
		}
		catch (final ValidationFailedException ex)
		{
			SpringValidator.LOG.log(Level.SEVERE, "Unexpected error while validating", ex);

			errors.reject(ex.getMessage());
		}
	}
}
