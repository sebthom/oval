package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotBlank;

/**
 * @author Gary Madden
 */
public class InvalidValueIndexTest extends TestCase {
   static class C {
      @NotBlank(message = "BLANK_A")
      public String a;

      public Map<@NotBlank(message = "BLANK_K") String, @NotBlank(message = "BLANK_V") String> map;

      public List<@NotBlank(message = "BLANK_L") String> list;
   }

   public void testInnvalidValueIndex() {
      final Validator validator = new Validator();

      final C c = new C();

      {
         c.a = "";

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("BLANK_A", violations.get(0).getMessage());
         assertNull(violations.get(0).getInvalidValueIndex());

         c.a = null;
      }

      {
         c.map = new HashMap<>();
         c.map.put("", "v");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("BLANK_K", violations.get(0).getMessage());
         assertEquals("", violations.get(0).getInvalidValueIndex());

         c.map = null;
      }

      {
         c.map = new HashMap<>();
         c.map.put("k", "");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("BLANK_V", violations.get(0).getMessage());
         assertEquals("k", violations.get(0).getInvalidValueIndex());

         c.map = null;
      }

      {
         c.list = new ArrayList<>();
         c.list.add("a");
         c.list.add("");
         c.list.add("c");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("BLANK_L", violations.get(0).getMessage());
         assertEquals(1, violations.get(0).getInvalidValueIndex());

         c.list = null;
      }
   }
}
