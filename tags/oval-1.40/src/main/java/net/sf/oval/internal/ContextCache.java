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
	private final static Map<Class< ? >, ClassContext> classContexts = new WeakHashMap<Class< ? >, ClassContext>();
	private final static Map<Field, FieldContext> fieldContexts = new WeakHashMap<Field, FieldContext>();
	private final static Map<Method, MethodEntryContext> methodEntryContexts = new WeakHashMap<Method, MethodEntryContext>();
	private final static Map<Method, MethodExitContext> methodExitContexts = new WeakHashMap<Method, MethodExitContext>();
	private final static Map<Method, MethodReturnValueContext> methodReturnValueContexts = new WeakHashMap<Method, MethodReturnValueContext>();

	public static ClassContext getClassContext(Class< ? > clazz)
	{
		synchronized (classContexts)
		{
			ClassContext ctx = classContexts.get(clazz);
			if (ctx == null)
			{
				ctx = new ClassContext(clazz);
				classContexts.put(clazz, ctx);
			}
			return ctx;
		}
	}

	public static FieldContext getFieldContext(Field field)
	{
		synchronized (fieldContexts)
		{
			FieldContext ctx = fieldContexts.get(field);
			if (ctx == null)
			{
				ctx = new FieldContext(field);
				fieldContexts.put(field, ctx);
			}
			return ctx;
		}
	}

	public static MethodEntryContext getMethodEntryContext(Method method)
	{
		synchronized (methodEntryContexts)
		{
			MethodEntryContext ctx = methodEntryContexts.get(method);
			if (ctx == null)
			{
				ctx = new MethodEntryContext(method);
				methodEntryContexts.put(method, ctx);
			}
			return ctx;
		}
	}

	public static MethodExitContext getMethodExitContext(Method method)
	{
		synchronized (methodExitContexts)
		{
			MethodExitContext ctx = methodExitContexts.get(method);
			if (ctx == null)
			{
				ctx = new MethodExitContext(method);
				methodExitContexts.put(method, ctx);
			}
			return ctx;
		}
	}

	public static MethodReturnValueContext getMethodReturnValueContext(Method method)
	{
		synchronized (methodReturnValueContexts)
		{
			MethodReturnValueContext ctx = methodReturnValueContexts.get(method);
			if (ctx == null)
			{
				ctx = new MethodReturnValueContext(method);
				methodReturnValueContexts.put(method, ctx);
			}
			return ctx;
		}
	}

	private ContextCache()
	{
		super();
	}
}
