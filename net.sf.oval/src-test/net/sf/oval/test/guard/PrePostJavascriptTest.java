package net.sf.oval.test.guard;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.oval.constraints.Assert;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Pre;

public class PrePostJavascriptTest extends TestCase
{
	@Guarded
	public static class TestTransaction
	{
		@SuppressWarnings("unused")
		private Date date;

		@SuppressWarnings("unused")
		private String description;

		// Rhino's LiveConnect only supports access to public properties and methods
		public BigDecimal value;

		@Pre(expression = "_this.value!=null && value2add!=null && _args[0]!=null", language = "javascript", message = "PRE")
		public void increase(
				@Assert(expression = "value!=null", language = "javascript", message = "ASSERT")
				BigDecimal value2add)
		{
			value = value.add(value2add);
		}
	}

	public void testPreJavascript()
	{
		TestTransaction t = new TestTransaction();
		try
		{
			t.increase(new BigDecimal(1));
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "PRE");
		}

		try
		{
			t.value = new BigDecimal(2);
			t.increase(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "ASSERT");
		}
		try
		{
			t.increase(new BigDecimal(1));
		}
		catch (ConstraintsViolatedException ex)
		{
			System.out.println(ex.getConstraintViolations()[0].getMessage());
		}
	}

	public void testPostJavascript()
	{

	}
}
