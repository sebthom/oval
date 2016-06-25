/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.expression;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;
import net.sf.oval.internal.util.ThreadLocalObjectCache;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageGroovyImpl extends AbstractExpressionLanguage {
    private static final Log LOG = Log.getLog(ExpressionLanguageGroovyImpl.class);

    private static final GroovyShell GROOVY_SHELL = new GroovyShell();

    private final ThreadLocalObjectCache<String, Script> threadScriptCache = new ThreadLocalObjectCache<String, Script>();

    public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
        LOG.debug("Evaluating Groovy expression: {1}", expression);
        try {
            final ObjectCache<String, Script> scriptCache = threadScriptCache.get();
            Script script = scriptCache.get(expression);
            if (script == null) {
                script = GROOVY_SHELL.parse(expression);
                scriptCache.put(expression, script);
            }

            final Binding binding = new Binding();
            for (final Entry<String, ?> entry : values.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
            script.setBinding(binding);
            return script.run();
        } catch (final Exception ex) {
            throw new ExpressionEvaluationException("Evaluating script with Groovy failed.", ex);
        }
    }
}