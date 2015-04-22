package net.sf.oval.test.validator;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.xml.XMLConfigurer;

public class CustomXMLConstraintTest extends TestCase
{
	public static class Entity
	{
		private String message;

		public String getMessage()
		{
			return message;
		}
	}

	public void testCustomXMLConstraint()
	{
		final Validator validator = new Validator(new XMLConfigurer(
				CustomXMLConstraintTest.class.getResourceAsStream("CustomXMLConstraintTest.xml")));

		final Entity e = new Entity();
		assertEquals(1, validator.validate(e).size());
		e.message = "123";
		assertEquals(1, validator.validate(e).size());
		e.message = "12345";
		assertEquals(0, validator.validate(e).size());
	}
}
