package net.sf.oval.test.validator;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.localization.context.ResourceBundleValidationContextRenderer;
import net.sf.oval.localization.context.ToStringValidationContextRenderer;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;
import net.sf.oval.localization.value.ToStringMessageValueFormatter;

public class DefaultInstancesTest extends TestCase
{
	public void testDefaultInstancesNotNull()
	{
		assertNotNull(ResourceBundleMessageResolver.INSTANCE);
		assertNotNull(ResourceBundleValidationContextRenderer.INSTANCE);
		assertNotNull(ToStringMessageValueFormatter.INSTANCE);
		assertNotNull(ToStringValidationContextRenderer.INSTANCE);

		assertNotNull(Validator.getCollectionFactory());
		assertNotNull(Validator.getContextRenderer());
		assertNotNull(Validator.getLoggerFactory());
		assertNotNull(Validator.getMessageResolver());
		assertNotNull(Validator.getMessageValueFormatter());
	}
}
