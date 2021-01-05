/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertURLCheck;
import net.sf.oval.constraint.AssertURLCheck.URIScheme;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Makkari - initial implementation
 * @author Sebastian Thomschke
 */
public class AssertURLITest extends AbstractContraintsTest {

   private static final URIScheme[] PERMITTED_SCHEMES = {URIScheme.FTP, URIScheme.HTTP, URIScheme.HTTPS};

   @Test
   public void testAssertURL() {
      final AssertURLCheck check = new AssertURLCheck();
      super.testCheck(check);
      assertThat(check.getPermittedURISchemes()).isNull();

      check.setPermittedURISchemes(PERMITTED_SCHEMES);
      final URIScheme[] actualPermittedSchemes = check.getPermittedURISchemes();
      assertThat(actualPermittedSchemes).isNotNull();
      assertThat(actualPermittedSchemes).hasSameSizeAs(PERMITTED_SCHEMES);
      for (final URIScheme element : PERMITTED_SCHEMES) {
         ArrayUtils.containsEqual(actualPermittedSchemes, element);
      }

      assertThat(check.isConnect()).isFalse();
      check.setConnect(true);
      assertThat(check.isConnect()).isTrue();

      check.setConnect(false);
      assertThat(check.isSatisfied(this, null, null)).isTrue();
      assertThat(check.isSatisfied(this, "http", null)).isFalse();
      assertThat(check.isSatisfied(this, "https", null)).isFalse();
      assertThat(check.isSatisfied(this, "ftp", null)).isFalse();
      assertThat(check.isSatisfied(this, "http://www.google.com", null)).isTrue();
      assertThat(check.isSatisfied(this, "httpa://www.google.com", null)).isFalse();
      assertThat(check.isSatisfied(this, "https://www.google.com", null)).isTrue();
      assertThat(check.isSatisfied(this, "httPs://www.google.com", null)).isTrue();
      assertThat(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/README.mirrors.txt", null)).isTrue();
      assertThat(check.isSatisfied(this, "ptth://www.google.com", null)).isFalse();
      assertThat(check.isSatisfied(this, "http://www.g[oogle.com", null)).isFalse();

      check.setConnect(true);
      assertThat(check.isSatisfied(this, null, null)).isTrue();
      assertThat(check.isSatisfied(this, "http", null)).isFalse();
      assertThat(check.isSatisfied(this, "https", null)).isFalse();
      assertThat(check.isSatisfied(this, "ftp", null)).isFalse();
      assertThat(check.isSatisfied(this, "http://www.google.com", null)).isTrue();
      assertThat(check.isSatisfied(this, "https://www.google.com", null)).isTrue();
      assertThat(check.isSatisfied(this, "http://127.0.0.1:34343", null)).isFalse();
      assertThat(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/foo.html", null)).isFalse();
      if (!System.getenv().containsKey("TRAVIS")) {
         assertThat(check.isSatisfied(this, "ftp://ftp.uni-erlangen.de/debian/README.mirrors.txt", null)).isTrue();
      }

      check.setPermittedURISchemes((URIScheme[]) null);
   }
}
