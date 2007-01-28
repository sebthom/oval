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
package net.sf.oval.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Sebastian Thomschke
 */
public class AllTests
{

	public static Test suite() throws Exception
	{
		final TestSuite suite = new TestSuite("Test for net.sf.oval");

		//$JUnit-BEGIN$

		/*
		 * Validator tests
		 */
		suite.addTestSuite(net.sf.oval.test.validator.AddingConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertFieldConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertValidConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertGroovyTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertJavascriptTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.JPAAnnotationsConfigurerTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.InheritanceTest.class);
		suite
				.addTestSuite(net.sf.oval.test.validator.MethodReturnValueConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ProfilesTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.SerializationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.StaticFieldsAndGettersTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateClassWithoutConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateWithMethodConstraintTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.XMLConfigurationTest.class);

		/*
		 * Guard tests
		 */
		suite.addTestSuite(net.sf.oval.test.guard.AddingConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ApplyFieldConstraintsToParametersTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ApplyFieldConstraintsToSetterTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ConstraintSetTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.CustomConstraintMessageTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ExceptionTranslatorTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.InheritanceTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.InnerClassTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.MethodReturnValueConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ParameterConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostValidateThisTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostJavascriptTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostGroovyTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.StaticMethodsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.XMLConfigurationTest.class);
		//$JUnit-END$
		return suite;
	}
}
