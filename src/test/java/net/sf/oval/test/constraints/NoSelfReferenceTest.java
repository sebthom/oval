/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.NoSelfReferenceCheck;

/**
 * @author Sebastian Thomschke
 */
public class NoSelfReferenceTest extends AbstractContraintsTest {

   @Test
   public void testNoSelfReference() {
      final NoSelfReferenceCheck check = new NoSelfReferenceCheck();
      super.testCheck(check);
      assertThat(check.isSatisfied(null, null, null)).isTrue();

      assertThat(check.isSatisfied(this, null, null)).isTrue();
      assertThat(check.isSatisfied(this, this, null)).isFalse();
      assertThat(check.isSatisfied(this, "bla", null)).isTrue();
   }
}
