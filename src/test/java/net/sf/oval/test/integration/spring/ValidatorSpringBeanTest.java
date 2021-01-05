/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.integration.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.oval.Validator;
import net.sf.oval.test.validator.XMLConfigurationTest;

/**
 * @author Sebastian Thomschke
 */
public class ValidatorSpringBeanTest {

   @Test
   public void testValidatorSpringBean() {
      try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("ValidatorSpringBeanTest.xml", ValidatorSpringBeanTest.class)) {
         final Validator validator = ctx.getBean("validator", Validator.class);
         new XMLConfigurationTest().validateUser(validator);
      }
   }

}
