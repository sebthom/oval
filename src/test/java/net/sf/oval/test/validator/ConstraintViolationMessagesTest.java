/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.ResourceBundle;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintViolationMessagesTest extends TestCase
{

	public void testMessages() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		ResourceBundle bundle = ResourceBundle.getBundle("net.sf.oval.Messages");
		for (Enumeration<String> en = bundle.getKeys(); en.hasMoreElements();)
		{
			String key = en.nextElement();
			if (key.endsWith(".violated"))
			{
				String className = key.substring(0, key.length() - 9);

				@SuppressWarnings("unchecked")
				Class<Annotation> annotationClass = (Class<Annotation>) Class.forName(className);

				// check that the default message defined on the annotation is the same as the key read from the bundle
				String annotationMessage = (String) ReflectionUtils.getMethod(annotationClass, "message")
						.getDefaultValue();
				assertEquals(key, annotationMessage);
				String annotationErrorCode = (String) ReflectionUtils.getMethod(annotationClass, "errorCode")
						.getDefaultValue();
				assertEquals(className, annotationErrorCode);

				// check that the message key returned by the check instance is the same as the key read from the bundle
				Check check = (Check) Class.forName(className + "Check").newInstance();
				assertEquals(key, check.getMessage());
			}
			else if (key.endsWith(".parameter"))
			{
				String className = key.substring(0, key.length() - 10);
				Class.forName(className);
			}
		}
	}
}
