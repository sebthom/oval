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
 *     Makkari - added AssertURLTest
 *******************************************************************************/
package net.sf.oval.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.oval.Validator;
import net.sf.oval.collection.CollectionFactoryJDKImpl;
import net.sf.oval.collection.CollectionFactoryJavalutionImpl;
import net.sf.oval.collection.CollectionFactoryTroveImpl;

/**
 * @author Sebastian Thomschke
 */
public final class AllTests
{
	private static void constraintsTests(final TestSuite suite) throws Exception
	{
		suite.addTestSuite(net.sf.oval.test.constraints.AssertTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.AssertFalseTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.AssertTrueTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.AssertURLTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.DateRangeTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.EmailTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.EqualToFieldTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.FutureTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.HasSubStringTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.InstanceOfAnyTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.InstanceOfTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.LengthTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MatchPatternTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MaxTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MaxLengthTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MaxSizeTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MemberOfTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MinTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MinLengthTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.MinSizeTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NoSelfReferenceTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotBlankTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotEmptyTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotEqualTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotEqualToFieldTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotMatchPatternTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotMemberOfTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotNegativeTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.NotNullTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.PastTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.RangeTest.class);
		suite.addTestSuite(net.sf.oval.test.constraints.SizeTest.class);
	}

	private static void guardTests(final TestSuite suite)
	{
		suite.addTestSuite(net.sf.oval.test.guard.AddingChecksTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ApplyFieldConstraintsToConstructorsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ApplyFieldConstraintsToParametersTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ApplyFieldConstraintsToSettersTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ConstraintSetTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.CustomConstraintMessageTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ExceptionTranslatorTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.InheritanceTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.InnerClassTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.MethodReturnValueConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.NullableTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.OverridingEqualsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.OverridingHashCodeTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ParameterConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostBeanShellTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostGroovyTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostJavascriptTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostJEXLTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostMVELTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostOGNLTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostRubyTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.PrePostValidateThisTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.ProbeModeTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.StaticMethodsTest.class);
		suite.addTestSuite(net.sf.oval.test.guard.XMLConfigurationTest.class);
	}

	private static void integrationTests(final TestSuite suite) throws Exception
	{
		suite.addTestSuite(net.sf.oval.test.integration.spring.SpringAOPAllianceTest.class);
		suite.addTestSuite(net.sf.oval.test.integration.spring.SpringValidatorTest.class);
	}

	public static Test suite() throws Exception
	{
		System.setSecurityManager(new SecurityManager());

		// $JUnit-BEGIN$
		final TestSuite suite = new TestSuite("Test for net.sf.oval");

		// Tests with JDK collections
		{
			final TestSuite suite1 = new TestSuite("Tests for net.sf.oval with JDK collections");
			constraintsTests(suite1);
			validatorTests(suite1);
			guardTests(suite1);
			integrationTests(suite1);
			final TestSetup setup1 = new TestSetup(suite1)
				{
					@Override
					protected void setUp() throws Exception
					{
						super.setUp();
						Validator.setCollectionFactory(new CollectionFactoryJDKImpl());
					}
				};
			suite.addTest(setup1);
		}

		// Tests with Javolution collections
		{
			final TestSuite suite1 = new TestSuite("Tests for net.sf.oval with Javolution collections");
			constraintsTests(suite1);
			validatorTests(suite1);
			guardTests(suite1);
			integrationTests(suite1);
			final TestSetup setup1 = new TestSetup(suite1)
				{
					@Override
					protected void setUp() throws Exception
					{
						super.setUp();
						Validator.setCollectionFactory(new CollectionFactoryJavalutionImpl());
					}
				};
			suite.addTest(setup1);
		}

		// Tests with Trove collections
		{
			final TestSuite suite1 = new TestSuite("Tests for net.sf.oval with Trove collections");
			constraintsTests(suite1);
			validatorTests(suite1);
			guardTests(suite1);
			integrationTests(suite1);
			final TestSetup setup1 = new TestSetup(suite1)
				{
					@Override
					protected void setUp() throws Exception
					{
						super.setUp();
						Validator.setCollectionFactory(new CollectionFactoryTroveImpl());
					}
				};
			suite.addTest(setup1);
		}
		// $JUnit-END$
		return suite;
	}

	private static void validatorTests(final TestSuite suite)
	{
		suite.addTestSuite(net.sf.oval.test.validator.AddingChecksTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertBeanShellTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertFieldConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertGroovyTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertJavascriptTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertJEXLTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertMVELTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertOGNLTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertRubyTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.AssertValidTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.CheckWithConstraintTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ConcurrencyTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ConditionalConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ConstraintViolationOrderTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.CustomAssertValidTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.InvariantMethodConstraintsValidationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.InheritanceTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.JPAAnnotationsConfigurerTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ObjectGraphTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ProfilesTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.SerializationTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.StaticFieldsAndGettersTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateClassWithoutConstraintsTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidateWithMethodConstraintTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.ValidatorAssertTest.class);
		suite.addTestSuite(net.sf.oval.test.validator.XMLConfigurationTest.class);
	}

	private AllTests()
	{
		super();
	}
}
