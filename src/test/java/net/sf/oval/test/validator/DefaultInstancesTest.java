/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.localization.context.ResourceBundleValidationContextRenderer;
import net.sf.oval.localization.context.ToStringValidationContextRenderer;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;
import net.sf.oval.localization.value.ToStringMessageValueFormatter;

/**
 * @author Sebastian Thomschke
 */
public class DefaultInstancesTest extends TestCase {
   public void testDefaultInstancesNotNull() {
      assertNotNull(ResourceBundleMessageResolver.INSTANCE);
      assertNotNull(ResourceBundleValidationContextRenderer.INSTANCE);
      assertNotNull(ToStringMessageValueFormatter.INSTANCE);
      assertNotNull(ToStringValidationContextRenderer.INSTANCE);

      assertNotNull(Validator.getCollectionFactory());
      assertNotNull(Validator.getContextRenderer());
      assertNotNull(Validator.getLoggerFactory());
      assertNotNull(Validator.getMessageResolver());
      assertNotNull(Validator.getMessageValueFormatter());
   }
}
