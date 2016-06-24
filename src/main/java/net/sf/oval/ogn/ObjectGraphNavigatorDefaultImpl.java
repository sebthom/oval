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
package net.sf.oval.ogn;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Default object graph navigator implementation.
 * 
 * Object path separator is a colon (.), e.g. owner.address.street
 * 
 * The implementation currently is limited to address fields and properties. Separate items of arrays, maps or keys cannot be addressed.
 * 
 * @author Sebastian Thomschke
 */
public class ObjectGraphNavigatorDefaultImpl implements ObjectGraphNavigator
{
	public ObjectGraphNavigationResult navigateTo(final Object root, final String path)
			throws InvalidConfigurationException
	{
		Assert.argumentNotNull("root", root);
		Assert.argumentNotNull("path", path);

		Object parent = null;
		Object target = root;
		AccessibleObject targetAccessor = null;
		for (final String chunk : path.split("\\."))
		{
			parent = target;
			if (parent == null) return null;
			final Field field = ReflectionUtils.getFieldRecursive(parent.getClass(), chunk);
			if (field == null)
			{
				final Method getter = ReflectionUtils.getGetterRecursive(parent.getClass(), chunk);
				if (getter == null)
					throw new InvalidConfigurationException("Invalid object navigation path from root object class ["
							+ root.getClass().getName() + "] path: " + path);
				targetAccessor = getter;
				target = ReflectionUtils.invokeMethod(getter, parent);
			}
			else
			{
				targetAccessor = field;
				target = ReflectionUtils.getFieldValue(field, parent);
			}
		}
		return new ObjectGraphNavigationResult(root, path, parent, targetAccessor, target);
	}
}
