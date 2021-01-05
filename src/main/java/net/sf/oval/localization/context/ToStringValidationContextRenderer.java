/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.context;

import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 *
 * @deprecated use {@link DefaultOValContextRenderer}
 */
@Deprecated
public class ToStringValidationContextRenderer implements OValContextRenderer {
   public static final ToStringValidationContextRenderer INSTANCE = new ToStringValidationContextRenderer();

   @Override
   public String render(final OValContext context) {
      return context.toString();
   }
}
