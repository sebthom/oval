/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import static org.assertj.core.api.Assertions.*;

import net.sf.oval.Check;
import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public abstract class AbstractContraintsTest {
   protected final Validator validator = new Validator();

   /**
    * Performs basic tests of the check implementation.
    */
   protected void testCheck(final Check check) {
      check.setMessage("XYZ");
      assertThat(check.getMessage()).isEqualTo("XYZ");

      check.setProfiles("p1");
      assertThat(check.getProfiles()).isNotNull();
      assertThat(check.getProfiles()).hasSize(1);
      assertThat(check.getProfiles()[0]).isEqualTo("p1");

      check.setProfiles((String[]) null);
      assertThat(check.getProfiles() == null || check.getProfiles().length == 0).isTrue();
   }
}
