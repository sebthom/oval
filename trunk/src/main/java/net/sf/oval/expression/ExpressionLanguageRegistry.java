/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.exception.ExpressionLanguageNotAvailableException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageRegistry
{
	private static final Log LOG = Log.getLog(ExpressionLanguageRegistry.class);

	private final Map<String, ExpressionLanguage> elcache = Validator.getCollectionFactory().createMap(4);

	private ExpressionLanguage _initializeDefaultEL(final String languageId)
	{
		// JavaScript support
		if (("javascript".equals(languageId) || "js".equals(languageId))
				&& ReflectionUtils.isClassPresent("org.mozilla.javascript.Context"))
			return registerExpressionLanguage("js", registerExpressionLanguage("javascript", new ExpressionLanguageJavaScriptImpl()));

		// Groovy support
		if ("groovy".equals(languageId) && ReflectionUtils.isClassPresent("groovy.lang.Binding"))
			return registerExpressionLanguage("groovy", new ExpressionLanguageGroovyImpl());

		// BeanShell support
		if (("beanshell".equals(languageId) || "bsh".equals(languageId)) && ReflectionUtils.isClassPresent("bsh.Interpreter"))
			return registerExpressionLanguage("beanshell", registerExpressionLanguage("bsh", new ExpressionLanguageBeanShellImpl()));

		// OGNL support
		if ("ognl".equals(languageId) && ReflectionUtils.isClassPresent("ognl.Ognl"))
			return registerExpressionLanguage("ognl", new ExpressionLanguageOGNLImpl());

		// MVEL2 support
		if ("mvel".equals(languageId) && ReflectionUtils.isClassPresent("org.mvel2.MVEL"))
			return registerExpressionLanguage("mvel", new ExpressionLanguageMVELImpl());

		// JRuby support
		else if (("jruby".equals(languageId) || "ruby".equals(languageId)) && ReflectionUtils.isClassPresent("org.jruby.Ruby"))
			return registerExpressionLanguage("jruby", registerExpressionLanguage("ruby", new ExpressionLanguageJRubyImpl()));

		// JEXL2 support
		if ("jexl".equals(languageId) && ReflectionUtils.isClassPresent("org.apache.commons.jexl2.JexlEngine"))
			return registerExpressionLanguage("jexl", new ExpressionLanguageJEXLImpl());

		return null;
	}

	/**
	 *
	 * @param languageId the id of the language, cannot be null
	 *
	 * @throws IllegalArgumentException if <code>languageName == null</code>
	 * @throws ExpressionLanguageNotAvailableException
	 */
	public ExpressionLanguage getExpressionLanguage(final String languageId) throws IllegalArgumentException,
			ExpressionLanguageNotAvailableException
	{
		Assert.argumentNotNull("languageId", languageId);

		ExpressionLanguage el = elcache.get(languageId);

		if (el == null) el = _initializeDefaultEL(languageId);

		if (el == null) throw new ExpressionLanguageNotAvailableException(languageId);

		return el;
	}

	/**
	 *
	 * @param languageId the expression language identifier
	 * @param impl the expression language implementation
	 * @throws IllegalArgumentException if <code>languageId == null || expressionLanguage == null</code>
	 */
	public ExpressionLanguage registerExpressionLanguage(final String languageId, final ExpressionLanguage impl)
			throws IllegalArgumentException
	{
		Assert.argumentNotNull("languageId", languageId);
		Assert.argumentNotNull("impl", impl);

		LOG.info("Expression language '{1}' registered: {2}", languageId, impl);

		elcache.put(languageId, impl);
		return impl;
	}
}
