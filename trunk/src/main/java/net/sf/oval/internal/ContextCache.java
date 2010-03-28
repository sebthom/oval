/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.oval.context.ClassContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodExitContext;
import net.sf.oval.context.MethodReturnValueContext;

/**
 * @author Sebastian Thomschke
 */
public final class ContextCache
{
	private static final Map<Class< ? >, ClassContext> CLASS_CONTEXTS = new WeakHashMap<Class< ? >, ClassContext>();
	private static final Map<Field, FieldContext> FIELD_CONTEXTS = new WeakHashMap<Field, FieldContext>();
	private static final Map<Method, MethodEntryContext> METHOD_ENTRY_CONTEXTS = new WeakHashMap<Method, MethodEntryContext>();
	private static final Map<Method, MethodExitContext> METHOD_EXIT_CONTEXTS = new WeakHashMap<Method, MethodExitContext>();
	private static final Map<Method, MethodReturnValueContext> METHOD_RETURN_VALUE_CONTEXTS = new WeakHashMap<Method, MethodReturnValueContext>();

	public static ClassContext getClassContext(final Class< ? > clazz)
	{
		synchronized (CLASS_CONTEXTS)
		{
			ClassContext ctx = CLASS_CONTEXTS.get(clazz);
			if (ctx == null)
			{
				ctx = new ClassContext(clazz);
				CLASS_CONTEXTS.put(clazz, ctx);
			}
			return ctx;
		}
	}

	public static FieldContext getFieldContext(final Field field)
	{
		synchronized (FIELD_CONTEXTS)
		{
			FieldContext ctx = FIELD_CONTEXTS.get(field);
			if (ctx == null)
			{
				ctx = new FieldContext(field);
				FIELD_CONTEXTS.put(field, ctx);
			}
			return ctx;
		}
	}

	public static MethodEntryContext getMethodEntryContext(final Method method)
	{
		synchronized (METHOD_ENTRY_CONTEXTS)
		{
			MethodEntryContext ctx = METHOD_ENTRY_CONTEXTS.get(method);
			if (ctx == null)
			{
				ctx = new MethodEntryContext(method);
				METHOD_ENTRY_CONTEXTS.put(method, ctx);
			}
			return ctx;
		}
	}

	public static MethodExitContext getMethodExitContext(final Method method)
	{
		synchronized (METHOD_EXIT_CONTEXTS)
		{
			MethodExitContext ctx = METHOD_EXIT_CONTEXTS.get(method);
			if (ctx == null)
			{
				ctx = new MethodExitContext(method);
				METHOD_EXIT_CONTEXTS.put(method, ctx);
			}
			return ctx;
		}
	}

	public static MethodReturnValueContext getMethodReturnValueContext(final Method method)
	{
		synchronized (METHOD_RETURN_VALUE_CONTEXTS)
		{
			MethodReturnValueContext ctx = METHOD_RETURN_VALUE_CONTEXTS.get(method);
			if (ctx == null)
			{
				ctx = new MethodReturnValueContext(method);
				METHOD_RETURN_VALUE_CONTEXTS.put(method, ctx);
			}
			return ctx;
		}
	}

	private ContextCache()
	{
		super();
	}
}
