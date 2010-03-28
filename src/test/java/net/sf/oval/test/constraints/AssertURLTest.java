/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010
 * Sebastian Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Makkari - initial implementation.
 *******************************************************************************/
package net.sf.oval.test.constraints;

import junit.framework.Assert;
import net.sf.oval.constraint.AssertURLCheck;
import net.sf.oval.constraint.AssertURLCheck.URIScheme;

/**
 * @author Makkari
 *
 */
public class AssertURLTest extends AbstractContraintsTest
{
	private static final URIScheme[] PERMITTED_SCHEMES = {URIScheme.FTP, URIScheme.HTTP, URIScheme.HTTPS};

	public void testAssertURL()
	{
		final AssertURLCheck check = new AssertURLCheck();
		super.testCheck(check);
		Assert.assertNull(check.getPermittedURISchemes());

		check.setPermittedURISchemes(AssertURLTest.PERMITTED_SCHEMES);
		final URIScheme[] actualPermittedSchemes = check.getPermittedURISchemes();
		Assert.assertNotNull(actualPermittedSchemes);
		Assert.assertEquals(AssertURLTest.PERMITTED_SCHEMES.length, actualPermittedSchemes.length);
		for (int n = 0; n < AssertURLTest.PERMITTED_SCHEMES.length; n++)
		{
			Assert.assertEquals(AssertURLTest.PERMITTED_SCHEMES[n], actualPermittedSchemes[n]);
			Assert.assertEquals(AssertURLTest.PERMITTED_SCHEMES[n].toString(), actualPermittedSchemes[n].getScheme());
		}

		Assert.assertFalse(check.isConnect());
		check.setConnect(true);
		Assert.assertTrue(check.isConnect());

		check.setConnect(false);
		Assert.assertTrue(check.isSatisfied(this, null, null, validator));
		Assert.assertFalse(check.isSatisfied(this, "http", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "https", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "ftp", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "http://www.google.com", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "https://www.google.com", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "httPs://www.google.com", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "ftp://ftp.is.co.za/rfc/rfc1808.txt", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "ptth://www.google.com", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "http://www.g[oogle.com", null, validator));

		check.setConnect(true);
		Assert.assertTrue(check.isSatisfied(this, null, null, validator));
		Assert.assertFalse(check.isSatisfied(this, "http", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "https", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "ftp", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "http://www.google.com", null, validator));
		Assert.assertTrue(check.isSatisfied(this, "https://www.verisign.com/site-map/index.html", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "http://127.0.0.1:34343", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "http://www.checkupdown.com/accounts/grpb/B1394343/", null,
				validator));
		Assert.assertTrue(check.isSatisfied(this, "ftp://ftp.debian.org/debian/README.html", null, validator));
		Assert.assertFalse(check.isSatisfied(this, "ftp://ftp.debian.org/debian/foo.html", null, validator));

		check.setPermittedURISchemes(null);
	}
}
