/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.expression;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.jruby.CompatVersion;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageJRubyImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageJRubyImpl.class);

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating JRuby expression: {1}", expression);
      try {
         final RubyInstanceConfig config = new RubyInstanceConfig();
         config.setCompatVersion(CompatVersion.RUBY1_9);
         final Ruby runtime = JavaEmbedUtils.initialize(new ArrayList<String>(), config);

         final StringBuilder localVars = new StringBuilder();
         for (final Entry<String, ?> entry : values.entrySet()) {
            runtime.getGlobalVariables().set("$" + entry.getKey(), JavaEmbedUtils.javaToRuby(runtime, entry.getValue()));
            localVars.append(entry.getKey()) //
               .append("=$") //
               .append(entry.getKey()) //
               .append("\n");
         }
         final IRubyObject result = runtime.evalScriptlet(localVars + expression);
         return JavaEmbedUtils.rubyToJava(runtime, result, Object.class);
      } catch (final RuntimeException ex) {
         throw new ExpressionEvaluationException("Evaluating JRuby expression failed: " + expression, ex);
      }
   }
}
