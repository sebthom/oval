package net.sf.oval.test.guard;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.oval.constraints.Assert;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
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
		public void increase1(
				@Assert(expression = "value!=null", language = "javascript", message = "ASSERT")
				BigDecimal value2add)
		{
			value = value.add(value2add);
		}

		@Post(expression = "_this.value>0", language = "groovy", message = "POST")
		public void increase2(@NotNull
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
			t.increase1(new BigDecimal(1));
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "PRE");
		}

		try
		{
			t.value = new BigDecimal(2);
			t.increase1(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "ASSERT");
		}
		try
		{
			t.increase1(new BigDecimal(1));
		}
		catch (ConstraintsViolatedException ex)
		{
			System.out.println(ex.getConstraintViolations()[0].getMessage());
		}
	}

	public void testPostJavascript()
	{
		TestTransaction t = new TestTransaction();

		try
		{
			t.value = new BigDecimal(-2);
			t.increase2(new BigDecimal(1));
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "POST");
		}

		t.value = new BigDecimal(0);
		t.increase2(new BigDecimal(1));
	}
}
