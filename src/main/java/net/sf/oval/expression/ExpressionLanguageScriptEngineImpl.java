/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.expression;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.oval.Validator;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

/**
 * JSR223 Support
 *
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageScriptEngineImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageScriptEngineImpl.class);

   private static final ScriptEngineManager FACTORY = new ScriptEngineManager();

   static {
      final List<Object> languages = Validator.getCollectionFactory().createList();
      for (final ScriptEngineFactory ef : FACTORY.getEngineFactories()) {
         languages.add(ef.getNames());
      }
      LOG.info("Available ScriptEngine language names: {1}", languages);
   }

   public static ExpressionLanguageScriptEngineImpl get(final String languageId) {
      final ScriptEngine engine = FACTORY.getEngineByName(languageId);
      return engine == null ? null : new ExpressionLanguageScriptEngineImpl(engine);
   }

   private final Compilable compilable;
   private final ScriptEngine engine;
   private final ObjectCache<String, CompiledScript> compiledCache;

   protected ExpressionLanguageScriptEngineImpl(final ScriptEngine engine) {
      this.engine = engine;
      if (engine instanceof Compilable) {
         compilable = (Compilable) engine;
         compiledCache = new ObjectCache<String, CompiledScript>() {
            @Override
            protected CompiledScript load(final String expression) {
               try {
                  return compilable.compile(expression);
               } catch (final ScriptException ex) {
                  throw new ExpressionEvaluationException("Parsing " + engine.get(ScriptEngine.NAME) + " expression failed: " + expression, ex);
               }
            }
         };
      } else {
         compilable = null;
         compiledCache = null;
      }
   }

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating JavaScript expression: {1}", expression);
      try {
         final Bindings scope = engine.createBindings();
         for (final Entry<String, ?> entry : values.entrySet()) {
            scope.put(entry.getKey(), entry.getValue());
         }

         if (compilable != null) {
            final CompiledScript compiled = compiledCache.get(expression);
            return compiled.eval(scope);
         }
         return engine.eval(expression, scope);
      } catch (final ScriptException ex) {
         throw new ExpressionEvaluationException("Evaluating " + engine.get(ScriptEngine.NAME) + " expression failed: " + expression, ex);
      }
   }
}
