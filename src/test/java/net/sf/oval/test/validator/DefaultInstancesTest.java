/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.Validator;
import net.sf.oval.localization.context.DefaultOValContextRenderer;
import net.sf.oval.localization.context.ResourceBundleValidationContextRenderer;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;
import net.sf.oval.localization.value.ToStringMessageValueFormatter;

/**
 * @author Sebastian Thomschke
 */
public class DefaultInstancesTest {

   @Test
   public void testDefaultInstancesNotNull() {
      assertThat(DefaultOValContextRenderer.INSTANCE).isNotNull();
      assertThat(ResourceBundleMessageResolver.INSTANCE).isNotNull();
      assertThat(ResourceBundleValidationContextRenderer.INSTANCE).isNotNull();
      assertThat(ToStringMessageValueFormatter.INSTANCE).isNotNull();

      assertThat(Validator.getCollectionFactory()).isNotNull();
      assertThat(Validator.getContextRenderer()).isNotNull();
      assertThat(Validator.getLoggerFactory()).isNotNull();
      assertThat(Validator.getMessageResolver()).isNotNull();
      assertThat(Validator.getMessageValueFormatter()).isNotNull();
   }
}
