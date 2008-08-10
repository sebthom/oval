/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
package net.sf.oval.test.validator;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConcurrencyTest extends TestCase
{
	public static final class TestEntity1
	{
		@NotNull
		@MaxLength(5)
		public String name;

		public String getName()
		{
			return name;
		}
	}

	public static final class TestEntity2
	{
		public String name;

		@NotNull
		@MaxLength(5)
		@IsInvariant
		public String getName()
		{
			return name;
		}
	}

	private final static class TestRunner implements Runnable
	{
		private final boolean[] failed;
		private final Validator validator;
		private final TestEntity1 sharedEntity;

		public TestRunner(final Validator validator, final TestEntity1 sharedEntity, final boolean[] failed)
		{
			this.validator = validator;
			this.sharedEntity = sharedEntity;
			this.failed = failed;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run()
		{
			try
			{
				final TestEntity1 entity = new TestEntity1();

				for (int i = 0; i < 100; i++)
				{
					Assert.assertEquals(1, validator.validate(sharedEntity).size());

					entity.name = null;
					Assert.assertEquals(1, validator.validate(entity).size());

					entity.name = "1234";
					Assert.assertEquals(0, validator.validate(entity).size());

					entity.name = "123456";
					Assert.assertEquals(1, validator.validate(entity).size());

					try
					{
						Thread.sleep(5);
					}
					catch (final InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
				}
			}
			catch (final RuntimeException ex)
			{
				ex.printStackTrace();
				failed[0] = true;
			}
		}
	}

	public void testConcurrency() throws InterruptedException
	{
		final Validator validator = new Validator();

		final TestEntity1 sharedEntity = new TestEntity1();

		final boolean[] failed = {false};

		final Thread thread1 = new Thread(new TestRunner(validator, sharedEntity, failed));
		final Thread thread2 = new Thread(new TestRunner(validator, sharedEntity, failed));

		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		assertFalse(failed[0]);
	}
}
