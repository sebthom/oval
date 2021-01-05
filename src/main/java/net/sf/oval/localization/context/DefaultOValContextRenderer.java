/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.context;

import java.util.List;

import net.sf.oval.context.IterableElementContext;
import net.sf.oval.context.MapKeyContext;
import net.sf.oval.context.MapValueContext;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 * @since 3.1
 */
public class DefaultOValContextRenderer implements OValContextRenderer {
   public static final DefaultOValContextRenderer INSTANCE = new DefaultOValContextRenderer();

   @Override
   public String render(final OValContext context) {
      return context.toString();
   }

   @Override
   public String render(final List<OValContext> contextPath) {
      final StringBuilder sb = new StringBuilder(3 * contextPath.size());
      boolean isFirst = true;
      for (final OValContext ctx : contextPath) {
         final boolean isContainerElementContext = ctx instanceof IterableElementContext || ctx instanceof MapKeyContext || ctx instanceof MapValueContext;
         if (isFirst) {
            isFirst = false;
            if (ctx.getDeclaringClass() != null) {
               sb.append(ctx.getDeclaringClass().getName());
               sb.append('.');
            }
         } else if (isContainerElementContext) {
            // do nothing special
         } else {
            sb.append('.');
         }

         sb.append(ctx.toStringUnqualified());
      }
      return sb.toString();
   }
}
