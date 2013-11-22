/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.oval.test.integration.spring;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.test.validator.XMLConfigurationTest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ValidatorSpringBeanTest extends TestCase
{
	public void testValidatorSpringBean()
	{
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("ValidatorSpringBeanTest.xml",
				ValidatorSpringBeanTest.class);
		try
		{
			final Validator validator = ctx.getBean("validator", Validator.class);
			new XMLConfigurationTest().validateUser(validator);
		}
		finally
		{
			ctx.close();
		}
	}
}
