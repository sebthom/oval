/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.NoSelfReferenceCheck;

/**
 * @author Sebastian Thomschke
 */
public class NoSelfReferenceTest extends AbstractContraintsTest {
   public void testNoSelfReference() {
      final NoSelfReferenceCheck check = new NoSelfReferenceCheck();
      super.testCheck(check);
      assertTrue(check.isSatisfied(null, null, null, null));

      assertTrue(check.isSatisfied(this, null, null, null));
      assertFalse(check.isSatisfied(this, this, null, null));
      assertTrue(check.isSatisfied(this, "bla", null, null));
   }
}
