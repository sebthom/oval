/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 * @author Makkari - live connect support
 */
public class AssertURLCheck extends AbstractAnnotationCheck<AssertURL> {
   /**
    * https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#scheme
    *
    * @author Sebastian Thomschke
    */
   public enum URIScheme {
      FTP("ftp"),
      HTTP("http"),
      HTTPS("https");

      private final String scheme;

      URIScheme(final String scheme) {
         this.scheme = scheme;
      }

      public String getScheme() {
         return scheme;
      }

      @Override
      public String toString() {
         return scheme;
      }
   }

   private static final long serialVersionUID = 1L;

   private static final Log LOG = Log.getLog(AssertURLCheck.class);

   private static boolean canConnect(final String url) {
      try {
         final URL theURL = new URL(url);
         final URLConnection conn = theURL.openConnection();
         conn.connect();
         conn.getInputStream().close();
         if (conn instanceof HttpURLConnection) {
            final HttpURLConnection httpConnection = (HttpURLConnection) conn;
            final int rc = httpConnection.getResponseCode();

            if (rc < HttpURLConnection.HTTP_BAD_REQUEST)
               return true;
            LOG.debug("Connecting failed with HTTP response code " + rc);
            return false;
         }
         return true;
      } catch (final IOException ex) {
         LOG.debug("Connecting failed with exception", ex);
         return false;
      }
   }

   /**
    * Specifies if a connection to the URL should be attempted to verify its validity.
    */
   private boolean connect = false;

   /**
    * Specifies the allowed URL schemes.
    */
   private final Map<String, URIScheme> permittedURISchemes = getCollectionFactory().createMap();

   @Override
   public void configure(final AssertURL constraintAnnotation) {
      super.configure(constraintAnnotation);
      setConnect(constraintAnnotation.connect());
      setPermittedURISchemes(constraintAnnotation.permittedURISchemes());
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   /**
    * Gets the allowed URL schemes.
    *
    * @return the permittedURISchemes
    */
   public URIScheme[] getPermittedURISchemes() {
      return permittedURISchemes.size() == 0 ? null : permittedURISchemes.values().toArray(new URIScheme[permittedURISchemes.size()]);
   }

   /**
    * Specifies if a connection to the URL should be attempted to verify its validity.
    *
    * @return the connect
    */
   public boolean isConnect() {
      return connect;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      final String uriString = valueToValidate.toString();

      try {
         // By constructing a java.net.URI object, the string representing the URI will be parsed against RFC 2396.
         // In case of non compliance a java.net.URISyntaxException will be thrown
         final URI uri = new URI(uriString);

         // Make sure that the URI contains: [scheme; scheme-specific-part]
         final String scheme = uri.getScheme();
         if (scheme == null || uri.getRawSchemeSpecificPart() == null) {
            LOG.debug("URI scheme or scheme-specific-part not specified");
            return false;
         }

         // Check whether the URI scheme is supported
         if (!permittedURISchemes.containsKey(scheme.toLowerCase()))
            return false;

         // If the connect flag is true then attempt to connect to the URL
         if (connect)
            return canConnect(uriString);
      } catch (final java.net.URISyntaxException ex) {
         LOG.debug("URI scheme or scheme-specific-part not specified", ex);
         return false;
      }

      return true;
   }

   /**
    * Specifies if a connection to the URL should be attempted to verify its validity.
    *
    * @param connect the connect to set
    */
   public void setConnect(final boolean connect) {
      this.connect = connect;
   }

   /**
    * Specifies the allowed URL schemes.
    *
    * @param permittedURISchemes the permittedURISchemes to set
    */
   public void setPermittedURISchemes(final URIScheme[] permittedURISchemes) {
      this.permittedURISchemes.clear();
      if (permittedURISchemes != null) {
         for (final URIScheme scheme : permittedURISchemes) {
            this.permittedURISchemes.put(scheme.scheme.toLowerCase(), scheme);
         }
      }
   }
}
