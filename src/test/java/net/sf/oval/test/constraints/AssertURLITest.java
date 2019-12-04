/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.AssertURLCheck;
import net.sf.oval.constraint.AssertURLCheck.URIScheme;

/**
 * @author Makkari - initial implementation
 * @author Sebastian Thomschke
 */
public class AssertURLITest extends AbstractContraintsTest {
   private static final URIScheme[] PERMITTED_SCHEMES = {URIScheme.FTP, URIScheme.HTTP, URIScheme.HTTPS};

   public void testAssertURL() {
      final AssertURLCheck check = new AssertURLCheck();
      super.testCheck(check);
      assertNull(check.getPermittedURISchemes());

      check.setPermittedURISchemes(PERMITTED_SCHEMES);
      final URIScheme[] actualPermittedSchemes = check.getPermittedURISchemes();
      assertNotNull(actualPermittedSchemes);
      assertEquals(PERMITTED_SCHEMES.length, actualPermittedSchemes.length);
      for (int n = 0; n < PERMITTED_SCHEMES.length; n++) {
         assertEquals(PERMITTED_SCHEMES[n], actualPermittedSchemes[n]);
         assertEquals(PERMITTED_SCHEMES[n].toString(), actualPermittedSchemes[n].getScheme());
      }

      assertFalse(check.isConnect());
      check.setConnect(true);
      assertTrue(check.isConnect());

      check.setConnect(false);
      assertTrue(check.isSatisfied(this, null, null, validator));
      assertFalse(check.isSatisfied(this, "http", null, validator));
      assertFalse(check.isSatisfied(this, "https", null, validator));
      assertFalse(check.isSatisfied(this, "ftp", null, validator));
      assertTrue(check.isSatisfied(this, "http://www.google.com", null, validator));
      assertFalse(check.isSatisfied(this, "httpa://www.google.com", null, validator));
      assertTrue(check.isSatisfied(this, "https://www.google.com", null, validator));
      assertTrue(check.isSatisfied(this, "httPs://www.google.com", null, validator));
      assertTrue(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/README.mirrors.txt", null, validator));
      assertFalse(check.isSatisfied(this, "ptth://www.google.com", null, validator));
      assertFalse(check.isSatisfied(this, "http://www.g[oogle.com", null, validator));

      check.setConnect(true);
      assertTrue(check.isSatisfied(this, null, null, validator));
      assertFalse(check.isSatisfied(this, "http", null, validator));
      assertFalse(check.isSatisfied(this, "https", null, validator));
      assertFalse(check.isSatisfied(this, "ftp", null, validator));
      assertTrue(check.isSatisfied(this, "http://www.google.com", null, validator));
      assertTrue(check.isSatisfied(this, "https://www.google.com", null, validator));
      assertFalse(check.isSatisfied(this, "http://127.0.0.1:34343", null, validator));
      assertFalse(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/foo.html", null, validator));
      if (!System.getenv().containsKey("TRAVIS")) {
         assertTrue(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/README.mirrors.txt", null, validator));
      }

      check.setPermittedURISchemes(null);
   }
}
