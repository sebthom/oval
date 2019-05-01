/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.expression;

import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.jruby.embed.jsr223.JRubyEngine;
import org.jruby.embed.jsr223.JRubyEngineFactory;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageJRubyImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageJRubyImpl.class);

   private final JRubyEngine engine;

   public ExpressionLanguageJRubyImpl() {
      engine = (JRubyEngine) new JRubyEngineFactory().getScriptEngine();
   }

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating JavaScript expression: {1}", expression);
      try {
         final Bindings scope = engine.createBindings();
         final StringBuilder localVars = new StringBuilder();
         for (final Entry<String, ?> entry : values.entrySet()) {

            // workaround for http://ruby.11.x6.nabble.com/undefined-local-variable-in-ScriptEngine-eval-tp3452553p3452557.html
            scope.put("$" + entry.getKey(), entry.getValue()); // register as global var
            localVars.append(entry.getKey()) // // reference as local var
               .append("=$") //
               .append(entry.getKey()) //
               .append("\n");
         }

         return engine.eval(localVars.toString() + expression, scope);
      } catch (final ScriptException ex) {
         throw new ExpressionEvaluationException("Evaluating JRuby expression failed: " + expression, ex);
      }
   }
}
