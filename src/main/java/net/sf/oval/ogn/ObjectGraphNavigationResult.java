/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.ogn;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Sebastian Thomschke
 */
public class ObjectGraphNavigationResult {

   public final Object root;
   public final String path;

   /**
    * accessor's value
    */
   public final Object target;
   public final Object targetParent;

   /**
    * {@link Field} or {@link Method}
    */
   public final AccessibleObject targetAccessor;

   public ObjectGraphNavigationResult(final Object root, final String path, final Object targetParent, final AccessibleObject targetAccessor,
      final Object target) {
      this.root = root;
      this.path = path;
      this.targetParent = targetParent;
      this.targetAccessor = targetAccessor;
      this.target = target;
   }
}
