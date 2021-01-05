/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.junit.Test;

import net.sf.oval.Check;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintViolationMessagesTest {

   @Test
   @SuppressWarnings("unchecked")
   public void testMessages() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      final ResourceBundle bundle = ResourceBundle.getBundle("net.sf.oval.Messages");
      for (final Enumeration<String> en = bundle.getKeys(); en.hasMoreElements();) {
         final String key = en.nextElement();
         if (key.endsWith(".violated")) {
            final String className = key.substring(0, key.length() - 9);

            final Class<Annotation> annotationClass = (Class<Annotation>) Class.forName(className);

            // check that the default message defined on the annotation is the same as the key read from the bundle
            final String annotationMessage = (String) ReflectionUtils.getMethod(annotationClass, "message").getDefaultValue();
            assertThat(annotationMessage).isEqualTo(key);
            final String annotationErrorCode = (String) ReflectionUtils.getMethod(annotationClass, "errorCode").getDefaultValue();
            assertThat(annotationErrorCode).isEqualTo(className);

            // check that the message key returned by the check instance is the same as the key read from the bundle
            final Check check = (Check) Class.forName(className + "Check").newInstance();
            assertThat(check.getMessage()).isEqualTo(key);
         } else if (key.endsWith(".parameter")) {
            final String className = key.substring(0, key.length() - 10);
            Class.forName(className);
         }
      }
   }
}
