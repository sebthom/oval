package net.sf.oval.test.guard;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.oval.constraint.Assert;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
import net.sf.oval.guard.Pre;

public class PrePostMVELTest extends TestCase
{
	@Guarded
	public static class TestTransaction
	{
		@SuppressWarnings("unused")
		private Date date;

		@SuppressWarnings("unused")
		private String description;

		private BigDecimal value;

		@Pre(expr = "_this.value!=null && value2add!=null && _args[0]!=null", lang = "mvel", message = "PRE")
		public void increase1(
				@Assert(expr = "_value!=null", lang = "mvel", message = "ASSERT")
				BigDecimal value2add)
		{
			value = value.add(value2add);
		}

		@Post(expr = "_this.value>_old.value", old = "[\"value\":_this.value]", lang = "mvel", message = "POST")
		public void increase2(@NotNull
		BigDecimal value2add)
		{
			value = value.add(value2add);
		}
		
		@Post(expr = "_this.value>_old.value", old = "[\"value\":_this.value]", lang = "mvel", message = "POST")
		public void increase2buggy(@NotNull
		BigDecimal value2add)
		{
			value = value.subtract(value2add);
		}

		/**
		 * @return the value
		 */
		public BigDecimal getValue()
		{
			return value;
		}
	}

	public void testPreMVEL()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

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

	public void testPostMVEL()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		TestTransaction t = new TestTransaction();

		try
		{
			t.value = new BigDecimal(-2);
			t.increase2buggy(new BigDecimal(1));
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations()[0].getMessage(), "POST");
		}

		t.increase2(new BigDecimal(1));
	}
}
