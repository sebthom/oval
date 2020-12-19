package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.Size;

/**
 * @author Gary Madden
 */
public class InvalidValueIndexTest extends TestCase {
   static class C {
      @Size(message = "A", min = 2)
      public String a;

      public Map<@Size(message = "B", min = 2) String, @Size(message = "C", min = 2) String> map;

      public List<@Size(message = "D", min = 2) String> list;
   }

   public void testInnvalidValueIndex() {
      final Validator validator = new Validator();

      final C c = new C();

      {
         c.a = "a";

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("A", violations.get(0).getMessage());
         assertNull(violations.get(0).getInvalidValueIndex());

         c.a = "aa";
      }

      {
         c.map = new HashMap<>();
         c.map.put("k", "vv");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("B", violations.get(0).getMessage());
         assertEquals("k", violations.get(0).getInvalidValueIndex());

         c.map = null;
      }

      {
         c.map = new HashMap<>();
         c.map.put("kk", "");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("C", violations.get(0).getMessage());
         assertEquals("kk", violations.get(0).getInvalidValueIndex());

         c.map = null;
      }

      {
         c.list = new ArrayList<>();
         c.list.add("aa");
         c.list.add("b");
         c.list.add("cc");

         final List<ConstraintViolation> violations = validator.validate(c);
         assertEquals(1, violations.size());
         assertEquals("D", violations.get(0).getMessage());
         assertEquals(1, violations.get(0).getInvalidValueIndex());

         c.list = null;
      }
   }
}
