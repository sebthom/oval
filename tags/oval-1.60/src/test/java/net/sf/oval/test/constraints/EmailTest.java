package net.sf.oval.test.constraints;

import net.sf.oval.constraint.EmailCheck;

public class EmailTest extends AbstractContraintsTest
{
	public void testEmail()
	{
		final EmailCheck check = new EmailCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		assertTrue(check.isSatisfied(null, "testjee@yahoo.com", null, null));
		assertTrue(check.isSatisfied(null, "test_jee@yahoo.co", null, null));
		assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.uk", null, null));
		assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.biz", null, null));
		assertTrue(check.isSatisfied(null, "test_jee@yahoo.com", null, null));
		assertTrue(check.isSatisfied(null, "test_jee@yahoo.net", null, null));
		assertTrue(check.isSatisfied(null, "user@subname1.subname2.subname3.domainname.co.uk", null, null));
		assertTrue(check.isSatisfied(null, "test.j'ee@yahoo.co.uk", null, null));
		assertTrue(check.isSatisfied(null, "test.j'e.e'@yahoo.co.uk", null, null));
		assertTrue(check.isSatisfied(null, "testj'ee@yahoo.com", null, null));
		assertTrue(check.isSatisfied(null, "test&jee@yahoo.com", null, null));
		assertTrue(check.isSatisfied(null, "test_j.s.@yahoo.com", null, null));

		assertFalse(check.isSatisfied(null, "test_jee#marry@yahoo.co.uk", null, null));
		assertFalse(check.isSatisfied(null, "test_jee@ yahoo.co.uk", null, null));
		assertFalse(check.isSatisfied(null, "test_jee  @yahoo.co.uk", null, null));
		assertFalse(check.isSatisfied(null, "test_j ee  @yah oo.co.uk", null, null));
		assertFalse(check.isSatisfied(null, "test_jee  @yah oo.co.uk", null, null));
		assertFalse(check.isSatisfied(null, "test_jee @ yahoo.com", null, null));
		assertFalse(check.isSatisfied(null, "user@subname1.subname2.subname3.domainn#ame.co.uk", null, null));
	}
}