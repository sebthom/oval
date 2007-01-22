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
package net.sf.oval.test.guard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.ParameterNameResolverAspectJImpl;
import net.sf.oval.Validator;
import net.sf.oval.configuration.XMLConfigurer;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.constraints.RegExCheck;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guarded;
import net.sf.oval.test.guard.ParameterConstraintsTest.TestEntity;

/**
 * @author Sebastian Thomschke
 */
public class XMLConfigurationTest extends TestCase
{
	@Guarded
	public static class User
	{
		// added @Length to test if overwrite=true works
		@Length(min = 10, max = 10)
		protected String userId;

		protected String managerId;

		protected String firstName;

		protected String lastName;

		public User()
		{
			// do nothing
		}

		public User(String userId, String managerId, int somethingElse)
		{
			this.userId = userId;
			this.managerId = managerId;
		}

		/**
		 * @return the managerId
		 */
		public String getManagerId()
		{
			return managerId;
		}

		/**
		 * @param managerId the managerId to set
		 */
		public void setManagerId(String managerId)
		{
			this.managerId = managerId;
		}
	}

	public void testImportedFile()
	{
		XMLConfigurer x = new XMLConfigurer();
		x.fromXML(XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest.xml"));

		Validator v = new Validator(x);
		v.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
		TestGuardAspect.INSTANCE.getGuard().setValidator(v);

		validateUser();
	}

	public void testSerializedObjectConfiguration()
	{
		XMLConfigurer x = new XMLConfigurer();

		/*
		 * define a configuration
		 */
		final Set<ConstraintSetConfiguration> constraintSetsConfig = new HashSet<ConstraintSetConfiguration>();
		{
			ConstraintSetConfiguration csf = new ConstraintSetConfiguration();
			constraintSetsConfig.add(csf);

			csf.id = "user.userid";
			csf.checks = new ArrayList<Check>();
			NotNullCheck nnc = new NotNullCheck();
			nnc.setMessage("{0} is null");
			csf.checks.add(nnc);
			RegExCheck rec = new RegExCheck();
			rec.setPattern(Pattern.compile("^[a-z0-9]{8}$", 0));
			rec.setMessage("{0} does not match the pattern {2}");
			csf.checks.add(rec);
		}

		final Set<ClassConfiguration> classConfigs = new HashSet<ClassConfiguration>();
		{
			ClassConfiguration cf = new ClassConfiguration();
			classConfigs.add(cf);
			cf.type = User.class;

			cf.fieldConfigurations = new HashSet<FieldConfiguration>();
			{
				FieldConfiguration fc = new FieldConfiguration();
				cf.fieldConfigurations.add(fc);

				fc.name = "firstName";
				fc.checks = new ArrayList<Check>();
				LengthCheck lc = new LengthCheck();
				lc.setMessage("{0} is not between {2} and {3} characters long");
				lc.setMax(3);
				fc.checks.add(lc);
			}
			{
				FieldConfiguration fc = new FieldConfiguration();
				cf.fieldConfigurations.add(fc);

				fc.name = "lastName";
				fc.checks = new ArrayList<Check>();
				LengthCheck lc = new LengthCheck();
				lc.setMessage("{0} is not between {2} and {3} characters long");
				lc.setMax(5);
				fc.checks.add(lc);
			}
			{
				FieldConfiguration fc = new FieldConfiguration();
				fc.overwrite = Boolean.TRUE;
				cf.fieldConfigurations.add(fc);

				fc.name = "userId";
				fc.checks = new ArrayList<Check>();
				AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
				acsc.setId("user.userid");
				fc.checks.add(acsc);
			}

			cf.constructorConfigurations = new HashSet<ConstructorConfiguration>();
			{
				ConstructorConfiguration cc = new ConstructorConfiguration();
				cf.constructorConfigurations.add(cc);
				cc.parameterConfigurations = new ArrayList<ParameterConfiguration>();

				AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
				acsc.setId("user.userid");

				ParameterConfiguration pc1 = new ParameterConfiguration();
				pc1.type = String.class;
				pc1.checks = new ArrayList<Check>();
				pc1.checks.add(acsc);
				cc.parameterConfigurations.add(pc1);
				ParameterConfiguration pc2 = new ParameterConfiguration();
				pc2.type = String.class;
				pc2.checks = new ArrayList<Check>();
				pc2.checks.add(acsc);
				cc.parameterConfigurations.add(pc2);
				ParameterConfiguration pc3 = new ParameterConfiguration();
				pc3.type = int.class;
				cc.parameterConfigurations.add(pc3);
			}

			cf.methodConfigurations = new HashSet<MethodConfiguration>();
			{
				AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
				acsc.setId("user.userid");

				MethodConfiguration mc = new MethodConfiguration();
				cf.methodConfigurations.add(mc);
				mc.name = "getManagerId";
				mc.returnValueConfiguration = new MethodReturnValueConfiguration();
				mc.returnValueConfiguration.checks = new ArrayList<Check>();
				mc.returnValueConfiguration.checks.add(acsc);

				mc = new MethodConfiguration();
				cf.methodConfigurations.add(mc);
				mc.name = "setManagerId";
				mc.parameterConfigurations = new ArrayList<ParameterConfiguration>();
				ParameterConfiguration pc1 = new ParameterConfiguration();
				pc1.type = String.class;
				pc1.checks = new ArrayList<Check>();
				pc1.checks.add(acsc);
				mc.parameterConfigurations.add(pc1);
			}
		}

		x.getPojoConfigurer().setClassConfigurations(classConfigs);
		x.getPojoConfigurer().setConstraintSetConfigurations(constraintSetsConfig);

		/*
		 * serialize the configuration to XML
		 */
		String xmlConfig = x.toXML();

		/*
		 * deserialize the configuration from XML
		 */
		x.fromXML(xmlConfig);

		Validator v = new Validator(x);
		v.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
		TestGuardAspect.INSTANCE.getGuard().setValidator(v);

		validateUser();
	}

	private void validateUser()
	{
		TestGuardAspect.guard.setSuppressPreConditionExceptions(TestEntity.class, true);

		ConstraintsViolatedAdapter listener = new ConstraintsViolatedAdapter();
		TestGuardAspect.guard.addListener(listener, User.class);

		listener.clear();
		try
		{
			new User(null, null, 1);
			fail("ConstraintViolationException expected");
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertEquals(2, violations.length);
			assertEquals(
					User.class.getName()
							+ "(class java.lang.String,class java.lang.String,int) Parameter 0 (userId) is null",
					violations[0].getMessage());
			assertEquals(
					User.class.getName()
							+ "(class java.lang.String,class java.lang.String,int) Parameter 1 (managerId) is null",
					violations[1].getMessage());
		}

		listener.clear();
		try
		{
			User user = new User("12345678", "12345678", 1);
			user.setManagerId(null);
			fail("ConstraintViolationException expected");
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertEquals(1, violations.length);
			assertEquals(User.class.getName()
					+ ".setManagerId(class java.lang.String) Parameter 0 (managerId) is null",
					violations[0].getMessage());
		}

		listener.clear();
		try
		{
			User user = new User();
			user.getManagerId();
			fail("ConstraintViolationException expected");
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertEquals(1, violations.length);
			assertEquals(User.class.getName() + ".getManagerId() is null", violations[0]
					.getMessage());
		}
	}
}
