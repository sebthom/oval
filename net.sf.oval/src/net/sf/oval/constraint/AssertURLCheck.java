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
package net.sf.oval.constraint;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

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
			if (conn instanceof HttpURLConnection)
			{
				final HttpURLConnection httpConnection = (HttpURLConnection) conn;
				httpConnection.connect();
				final int rc = httpConnection.getResponseCode();

				if (rc < 400) return true;
				return false;
			}
			return false;
		}
		catch (final IOException e)
		{
			return false;
		}
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
		return permittedURISchemes;
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

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		if (value == null) return true;

		final String url = value.toString();

		// test if the URI scheme is allowed
		if (!isURISchemeValid(url)) return false;

		if (connect) return canConnect(url);

		// TODO validate URL e.g. using regular expressions

		return false;
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
		this.permittedURISchemes = permittedURISchemes;
	}
}
