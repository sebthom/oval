package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.XmlConfigurer;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.OValConfiguration;
import net.sf.oval.constraints.AssertConstraintSetCheck;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNullCheck;

public class XmlConfigurationTest extends TestCase
{
	private static class Person
	{
		public String firstName;

		public String lastName;
	}

	public void testXmlConfigurer()
	{
		XmlConfigurer x = new XmlConfigurer();

		/*
		 * define a configuration
		 */
		OValConfiguration ovalConfig = new OValConfiguration();
		{
			ovalConfig.classesConfig = new HashSet<ClassConfiguration>();

			ovalConfig.constraintSetsConfig = new HashSet<ConstraintSetConfiguration>();
			{
				ConstraintSetConfiguration csf = new ConstraintSetConfiguration();
				ovalConfig.constraintSetsConfig.add(csf);

				csf.id = "myConstraintSet1";
				csf.checks = new ArrayList<Check>();
				NotNullCheck nnc = new NotNullCheck();
				nnc.setMessage("NOT_NULL");
				csf.checks.add(nnc);
			}

			ClassConfiguration cf = new ClassConfiguration();
			ovalConfig.classesConfig.add(cf);
			cf.type = Person.class;

			cf.fieldsConfig = new HashSet<FieldConfiguration>();
			{
				FieldConfiguration fc = new FieldConfiguration();
				cf.fieldsConfig.add(fc);

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
				cf.fieldsConfig.add(fc);

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
		}

		x.setOValConfiguration(ovalConfig);

		/*
		 * serialize the configuration to XML
		 */
		String xmlConfig = x.toXML();

		/*
		 * deserialize the configuration from XML
		 */
		x.fromXML(xmlConfig);

		System.out.println(xmlConfig);

		Validator validator = new Validator();
		validator.addChecks(ovalConfig);

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
	}
}
