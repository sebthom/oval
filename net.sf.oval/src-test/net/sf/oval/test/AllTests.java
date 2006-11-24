/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public class AllTests
{

	public static Test suite() throws Exception
	{	
		final TestSuite suite = new TestSuite("Test for " + Validator.class.getPackage().getName());

		//$JUnit-BEGIN$

		/*
		 * Validator tests
		 */
		suite.addTestSuite(net.sf.oval.test.validator.AddingConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertFieldConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertValidConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.JPAAnnotationsConfigurerTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.InheritanceTest.class);
		suite
				.addTestSuite(net.sf.oval.test.validator.MethodReturnValueConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.SerializationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateClassWithoutConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateWithMethodConstraintTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.XMLConfigurationTest.class);

		/*
		 * Enforcer tests
		 */
		suite.addTestSuite(net.sf.oval.test.enforcer.AddingConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.ApplyFieldConstraintsToParametersTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.ApplyFieldConstraintsToSetterTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.ConstraintSetTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.CustomConstraintMessageTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.InheritanceTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.InnerClassTest.class);
		suite
				.addTestSuite(net.sf.oval.test.enforcer.MethodReturnValueConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.ParameterConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.PrePostValidateThisTest.class);
		suite.addTestSuite(net.sf.oval.test.enforcer.XMLConfigurationTest.class);
		//$JUnit-END$
		return suite;
	}
}
