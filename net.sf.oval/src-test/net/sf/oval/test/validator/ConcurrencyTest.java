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
	public final static class TestEntity1
	{
		@NotNull
		@MaxLength(5)
		private String name;

		public String getName()
		{
			return name;
		}
	}

	public final static class TestEntity2
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

	public void testConcurrency() throws InterruptedException
	{
		final Validator validator = new Validator();

		final TestEntity1 sharedEntity = new TestEntity1();

		final Thread thread1 = new Thread(new Runnable()
			{
				public void run()
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
						catch (InterruptedException e)
						{
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		final Thread thread2 = new Thread(new Runnable()
			{
				public void run()
				{
					final TestEntity2 entity = new TestEntity2();

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
						catch (InterruptedException e)
						{
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		thread1.run();
		thread2.run();
		thread1.join();
		thread2.join();
	}
}
