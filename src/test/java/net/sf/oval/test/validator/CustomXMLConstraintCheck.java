package net.sf.oval.test.validator;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;

public class CustomXMLConstraintCheck extends AbstractAnnotationCheck<Constraint>
{
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage()
	{
		return "Value must have more than 4 characters!";
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;
		return valueToValidate.toString().length() > 4;
	}
}