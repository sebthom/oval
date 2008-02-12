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
 *     Makkari - live connect support.
 *******************************************************************************/
package net.sf.oval.constraint;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class AssertURLCheck extends AbstractAnnotationCheck<AssertURL>
{
	/**
	 * http://en.wikipedia.org/wiki/URI_scheme
	 * 
	 * @author Sebastian Thomschke
	 *
	 */
	public static enum URIScheme
	{
		FTP("ftp"),
		HTTP("http"),
		HTTPS("https");

		private final String scheme;

		private URIScheme(final String scheme)
		{
			this.scheme = scheme;
		}

		/**
		 * @return the scheme
		 */
		public String getScheme()
		{
			return scheme;
		}

		@Override
		public String toString()
		{
			return scheme;
		}
	}

	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.getLog(AssertURLCheck.class);

	/**
	 * Specifies if a connection to the URL should be attempted to verify its validity. 
	 */
	private boolean connect = false;

	/**
	 * Specifies the allowed URL schemes.
	 */
	private URIScheme[] permittedURISchemes;

	private boolean canConnect(final String url)
	{
		try
		{
			final URL theURL = new URL(url);
			final URLConnection conn = theURL.openConnection();
			conn.connect();
			conn.getInputStream().close();
			if (conn instanceof HttpURLConnection)
			{
				final HttpURLConnection httpConnection = (HttpURLConnection) conn;
				final int rc = httpConnection.getResponseCode();

				if (rc < HttpURLConnection.HTTP_BAD_REQUEST) return true;
				LOG.trace("Connecting failed with HTTP response code " + rc);
				return false;
			}
		}
		catch (final IOException e)
		{
			LOG.trace("Connecting failed with exception", e);
			return false;
		}
		return true;
	}

	@Override
	public void configure(final AssertURL constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setConnect(constraintAnnotation.connect());
		setPermittedURISchemes(constraintAnnotation.permittedURISchemes());
	}

	/**
	 * Gets the allowed URL schemes.
	 * @return the permittedURISchemes
	 */
	public URIScheme[] getPermittedURISchemes()
	{
		return permittedURISchemes == null ? null : permittedURISchemes.clone();
	}

	/**
	 * Specifies if a connection to the URL should be attempted to verify its validity.
	 * 
	 * @return the connect
	 */
	public boolean isConnect()
	{
		return connect;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
	{
		if (valueToValidate == null) return true;

		final String URIString = valueToValidate.toString();

		try
		{
			// By constructing a java.net.URI object, the string representing the URI will be parsed against RFC 2396.
			// In case of non compliance a java.net.URISyntaxException will be thrown
			final URI uri = new URI(URIString);

			// Make sure that the URI contains: [scheme; scheme-specific-part]
			final String scheme = uri.getScheme();
			if (scheme == null || uri.getRawSchemeSpecificPart() == null)
			{
				LOG.trace("URI scheme or scheme-specific-part not specified");
				return false;
			}

			// Check whether the URI scheme is supported
			if (!isURISchemeValid(scheme.toLowerCase())) return false;

			// If the connect flag is true then attempt to connect to the URL
			if (connect) return canConnect(URIString);
		}
		catch (final java.net.URISyntaxException ex)
		{
			LOG.trace("URI scheme or scheme-specific-part not specified");
			return false;
		}

		return true;
	}

	private boolean isURISchemeValid(final String url)
	{
		if (permittedURISchemes != null)
		{
			for (final URIScheme scheme : permittedURISchemes)
			{
				if (url.startsWith(scheme.getScheme())) return true;
			}
		}
		return false;
	}

	/**
	 * Specifies if a connection to the URL should be attempted to verify its validity.
	 * 
	 * @param connect the connect to set
	 */
	public void setConnect(final boolean connect)
	{
		this.connect = connect;
	}

	/**
	 * Specifies the allowed URL schemes.
	 * 
	 * @param permittedURISchemes the permittedURISchemes to set
	 */
	public void setPermittedURISchemes(final URIScheme[] permittedURISchemes)
	{
		this.permittedURISchemes = permittedURISchemes == null ? null : permittedURISchemes.clone();
	}
}
