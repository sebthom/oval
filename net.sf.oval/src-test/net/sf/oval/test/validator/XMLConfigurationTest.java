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
package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.XMLConfigurer;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNullCheck;

/**
 * @author Sebastian Thomschke
 */
public class XMLConfigurationTest extends TestCase
{
	private static class Person
	{
		protected String firstName;

		protected String lastName;

		public String getLastName()
		{
			return lastName;
		}
	}

	public void testXmlConfigurer()
	{
		XMLConfigurer x = new XMLConfigurer();

		/*
		 * define a configuration
		 */
		final Set<ConstraintSetConfiguration> constraintSetsConfig = new HashSet<ConstraintSetConfiguration>();
		{
			ConstraintSetConfiguration csf = new ConstraintSetConfiguration();
			constraintSetsConfig.add(csf);

			csf.id = "myConstraintSet1";
			csf.checks = new ArrayList<Check>();
			NotNullCheck nnc = new NotNullCheck();
			nnc.setMessage("NOT_NULL");
			csf.checks.add(nnc);
		}

		final Set<ClassConfiguration> classConfigs = new HashSet<ClassConfiguration>();
		{
			ClassConfiguration cf = new ClassConfiguration();
			classConfigs.add(cf);
			cf.type = Person.class;

			cf.fieldConfigurations = new HashSet<FieldConfiguration>();
			{
				FieldConfiguration fc = new FieldConfiguration();
				cf.fieldConfigurations.add(fc);

				fc.name = "firstName";
				fc.checks = new ArrayList<Check>();
				AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
				acsc.setId("myConstraintSet1");
				fc.checks.add(acsc);
				LengthCheck lc = new LengthCheck();
				lc.setMax(3);
				fc.checks.add(lc);
			}
			{
				FieldConfiguration fc = new FieldConfiguration();
				cf.fieldConfigurations.add(fc);

				fc.name = "lastName";
				fc.checks = new ArrayList<Check>();
				AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
				acsc.setId("myConstraintSet1");
				fc.checks.add(acsc);
				LengthCheck lc = new LengthCheck();
				lc.setMessage("LENGTH");
				lc.setMax(5);
				fc.checks.add(lc);
			}

			cf.methodConfigurations = new HashSet<MethodConfiguration>();
			{
				MethodConfiguration mc = new MethodConfiguration();
				cf.methodConfigurations.add(mc);
				mc.name = "getLastName";
				mc.returnValueChecks = new ArrayList<Check>();
				NotNullCheck nnc = new NotNullCheck();
				nnc.setMessage("NOT_NULL");
				mc.returnValueChecks.add(nnc);
			}
		}

		x.setClassConfigurations(classConfigs);
		x.setConstraintSetConfigurations(constraintSetsConfig);

		/*
		 * serialize the configuration to XML
		 */
		String xmlConfig = x.toXML();

		/*
		 * deserialize the configuration from XML
		 */
		x.fromXML(xmlConfig);

		System.out.println(xmlConfig);

		Validator validator = new Validator(x);
		//validator.getConfigurers().add(x);

		Person p = new Person();
		p.lastName = "";
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));

		p.firstName = "";
		p.lastName = "123456";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("LENGTH"));

		p.lastName = null;
		violations = validator.validate(p);
		assertTrue(violations.size() == 2);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
		assertTrue(violations.get(1).getMessage().equals("NOT_NULL"));
	}
}
