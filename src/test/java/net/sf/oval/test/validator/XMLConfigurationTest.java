/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.pojo.POJOConfigurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.xml.XMLConfigurer;
import net.sf.oval.constraint.AssertCheck;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.NotNullCheck;

/**
 * @author Sebastian Thomschke
 */
public class XMLConfigurationTest extends TestCase {

   public static class User {
      // added @Length to test if overwrite=true works
      @Length(min = 10, max = 10)
      protected String userId;

      protected String managerId;

      protected String firstName;

      protected String lastName;

      /**
       * @return the managerId
       */
      public String getManagerId() {
         return managerId;
      }
   }

   public void testMultipleXMLFiles() {
      final XMLConfigurer x1 = new XMLConfigurer();
      x1.fromXML(XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest1.inc.xml"));
      final XMLConfigurer x2 = new XMLConfigurer();
      x2.fromXML(XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest2.inc.xml"));
      validateUser(new Validator(x1, x2));
   }

   public void testMultipleXMLFilesWithXIncluded() {
      final XMLConfigurer x1 = new XMLConfigurer();
      x1.fromXML(new File("src/test/resources/net/sf/oval/test/validator/XMLConfigurationTest.xml"));
      validateUser(new Validator(x1));
   }

   public void testSerializedObjectConfiguration() throws Exception {
      final XMLConfigurer x = new XMLConfigurer();

      /*
       * define a configuration
       */
      final Set<ConstraintSetConfiguration> constraintSetsConfig = new HashSet<ConstraintSetConfiguration>();
      {
         final ConstraintSetConfiguration csf = new ConstraintSetConfiguration();
         constraintSetsConfig.add(csf);

         csf.id = "user.userid";
         csf.checks = new ArrayList<Check>();
         final NotNullCheck nnc = new NotNullCheck();
         nnc.setMessage("{context} is null");
         csf.checks.add(nnc);
         final MatchPatternCheck rec = new MatchPatternCheck();
         rec.setPattern(Pattern.compile("^[a-z0-9]{8}$", 0));
         rec.setMessage("{context} does not match the pattern {pattern}");
         rec.setProfiles("a", "b");
         csf.checks.add(rec);
      }

      final Set<ClassConfiguration> classConfigs = new HashSet<ClassConfiguration>();
      {
         final ClassConfiguration cf = new ClassConfiguration();
         classConfigs.add(cf);
         cf.type = User.class;

         cf.objectConfiguration = new ObjectConfiguration();
         {
            cf.objectConfiguration.checks = new ArrayList<Check>();

            final AssertCheck ac = new AssertCheck();
            ac.setExpr("_this.firstName != _this.lastName");
            ac.setMessage("firstName and lastName must not be the same");
            ac.setLang("groovy");
            cf.objectConfiguration.checks.add(ac);
         }

         cf.fieldConfigurations = new HashSet<FieldConfiguration>();
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "firstName";
            fc.checks = new ArrayList<Check>();
            final AssertCheck ac = new AssertCheck();
            ac.setExpr("_value != null && _value.length() <= 3");
            ac.setMessage("{context} cannot be longer than 3 characters");
            ac.setLang("groovy");
            fc.checks.add(ac);
         }
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "lastName";
            fc.checks = new ArrayList<Check>();
            final LengthCheck lc = new LengthCheck();
            lc.setMessage("{context} is not between {min} and {max} characters long");
            lc.setMin(1);
            lc.setMax(5);
            lc.setAppliesTo(ConstraintTarget.CONTAINER, ConstraintTarget.KEYS);
            fc.checks.add(lc);
         }
         {
            final FieldConfiguration fc = new FieldConfiguration();
            fc.overwrite = Boolean.TRUE;
            cf.fieldConfigurations.add(fc);

            fc.name = "userId";
            fc.checks = new ArrayList<Check>();
            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");
            fc.checks.add(acsc);
         }

         cf.methodConfigurations = new HashSet<MethodConfiguration>();
         {
            final MethodConfiguration mc = new MethodConfiguration();
            cf.methodConfigurations.add(mc);
            mc.name = "getManagerId";
            mc.isInvariant = true;
            mc.returnValueConfiguration = new MethodReturnValueConfiguration();
            mc.returnValueConfiguration.checks = new ArrayList<Check>();
            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");
            mc.returnValueConfiguration.checks.add(acsc);
         }
      }

      x.getPojoConfigurer().setClassConfigurations(classConfigs);
      x.getPojoConfigurer().setConstraintSetConfigurations(constraintSetsConfig);

      /*
       * test POJO Configurer object serialization
       */
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(x.getPojoConfigurer());
      oos.flush();
      oos.close();

      final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
      final ObjectInputStream ois = new ObjectInputStream(bin);
      x.setPojoConfigurer((POJOConfigurer) ois.readObject());
      ois.close();

      /*
       * test XML de/serialization
       */
      final String xmlConfig = x.toXML();
      // System.out.println(xmlConfig);
      x.fromXML(xmlConfig);
      validateUser(new Validator(x));
   }

   public void validateUser(final Validator validator) {
      final User usr = new User();

      usr.lastName = "1";
      usr.userId = "12345678";
      usr.managerId = "12345678";

      /*
       * check constraints for firstName
       */
      usr.firstName = "123456";
      List<ConstraintViolation> violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".firstName cannot be longer than 3 characters", violations.get(0).getMessage());

      usr.firstName = "";

      /*
       * check constraints for lastName
       */
      usr.lastName = "123456";
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".lastName is not between 1 and 5 characters long", violations.get(0).getMessage());

      usr.lastName = "1";

      /*
       * check constraints for userId
       */
      usr.userId = null;
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".userId is null", violations.get(0).getMessage());

      usr.userId = "%$$e3";
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".userId does not match the pattern ^[a-z0-9]{8}$", violations.get(0).getMessage());
      usr.userId = "12345678";

      /*
       * check constraints for managerId
       */
      usr.managerId = null;
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".getManagerId() is null", violations.get(0).getMessage());

      usr.managerId = "%$$e3";
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals(User.class.getName() + ".getManagerId() does not match the pattern ^[a-z0-9]{8}$", violations.get(0).getMessage());

      /*
       * check object constraints
       */
      usr.userId = "12345678";
      usr.managerId = "12345678";

      usr.lastName = "abc";
      usr.firstName = "abc";
      violations = validator.validate(usr);
      assertEquals(1, violations.size());
      assertEquals("firstName and lastName must not be the same", violations.get(0).getMessage());
   }
}
