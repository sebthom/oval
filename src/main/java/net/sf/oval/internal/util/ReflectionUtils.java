/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal.util;

import static net.sf.oval.Validator.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.oval.exception.AccessingFieldValueFailedException;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvokingMethodFailedException;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.internal.ContextCache;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public final class ReflectionUtils {
   private static final Log LOG = Log.getLog(ReflectionUtils.class);

   private static final ReflectPermission SUPPRESS_ACCESS_CHECKS_PERMISSION = new ReflectPermission("suppressAccessChecks");

   public static void assertPrivateAccessAllowed() throws ReflectionException {
      final SecurityManager manager = System.getSecurityManager();
      if (manager != null) {
         try {
            manager.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
         } catch (final SecurityException ex) {
            throw new ReflectionException("Current security manager configuration does not allow access to private fields and methods.", ex);
         }
      }
   }

   /**
    * Returns all annotations present on this class.
    *
    * @param clazz the class to inspect
    * @param inspectInterfaces whether to also return annotations declared on interface declaration
    * @return all annotations present on this class.
    */
   public static Annotation[] getAnnotations(final Class<?> clazz, final boolean inspectInterfaces) {
      if (!inspectInterfaces)
         return clazz.getAnnotations();

      final List<Annotation> annotations = ArrayUtils.asList(clazz.getAnnotations());
      for (final Class<?> next : ReflectionUtils.getInterfacesRecursive(clazz)) {
         final Annotation[] declaredAnnotations = next.getDeclaredAnnotations();
         annotations.addAll(ArrayUtils.asList(declaredAnnotations));
      }
      return annotations.toArray(new Annotation[annotations.size()]);
   }

   /**
    * Returns all annotations present on this method.
    *
    * @param method the method to inspect
    * @param inspectInterfaces whether to also return annotations declared on interface method declaration
    * @return all annotations present on this method.
    */
   public static Annotation[] getAnnotations(final Method method, final boolean inspectInterfaces) {
      if (!inspectInterfaces || !isPublic(method))
         return method.getAnnotations();

      final String methodName = method.getName();
      final Class<?>[] methodParameterTypes = method.getParameterTypes();

      final List<Annotation> annotations = ArrayUtils.asList(method.getAnnotations());
      for (final Class<?> nextClass : ReflectionUtils.getInterfacesRecursive(method.getDeclaringClass())) {
         try {
            ArrayUtils.addAll(annotations, nextClass.getDeclaredMethod(methodName, methodParameterTypes).getDeclaredAnnotations());
         } catch (final NoSuchMethodException ex) {
            // ignore
         }
      }
      return annotations.toArray(new Annotation[annotations.size()]);
   }

   /**
    * @return the field or null if the field does not exist
    */
   public static Field getField(final Class<?> clazz, final String fieldName) {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (final NoSuchFieldException ex) {
         return null;
      }
   }

   /**
    * @return the corresponding field for a setter method. Returns null if the method is not a
    *         JavaBean style setter or the field could not be located.
    */
   public static Field getFieldForSetter(final Method setter) {
      if (!isSetter(setter))
         return null;

      final Class<?>[] methodParameterTypes = setter.getParameterTypes();
      final String methodName = setter.getName();
      final Class<?> clazz = setter.getDeclaringClass();

      // calculate the corresponding field name based on the name of the setter method (e.g. method setName() => field
      // name)
      String fieldName = methodName.substring(3, 4).toLowerCase(getLocaleProvider().getLocale());
      if (methodName.length() > 4) {
         fieldName += methodName.substring(4);
      }

      Field field = null;
      try {
         field = clazz.getDeclaredField(fieldName);

         // check if field and method parameter are of the same type
         if (!field.getType().equals(methodParameterTypes[0])) {
            LOG.warn("Found field <{1}> in class <{2}>that matches setter <{3}> name, but mismatches parameter type.", fieldName, clazz.getName(), methodName);
            field = null;
         }
      } catch (final NoSuchFieldException e) {
         LOG.debug("Field not found", e);
      }

      // if method parameter type is boolean then check if a field with name isXXX exists (e.g. method setEnabled() =>
      // field isEnabled)
      if (field == null && (boolean.class.equals(methodParameterTypes[0]) || Boolean.class.equals(methodParameterTypes[0]))) {
         fieldName = "is" + methodName.substring(3);

         try {
            field = clazz.getDeclaredField(fieldName);

            // check if found field is of boolean or Boolean
            if (!boolean.class.equals(field.getType()) && Boolean.class.equals(field.getType())) {
               LOG.warn("Found field <{1}> in class <{2}>that matches setter <{3}> name, but mismatches parameter type.", fieldName, clazz.getName(),
                  methodName);
               field = null;
            }
         } catch (final NoSuchFieldException ex) {
            LOG.debug("Field not found", ex);
         }
      }

      return field;
   }

   public static Field getFieldRecursive(final Class<?> clazz, final String fieldName) {
      final Field f = getField(clazz, fieldName);
      if (f != null)
         return f;

      final Class<?> superclazz = clazz.getSuperclass();
      if (superclazz == null)
         return null;

      return getFieldRecursive(superclazz, fieldName);
   }

   public static Object getFieldValue(final Field field, final Object target) throws AccessingFieldValueFailedException {
      try {
         setAccessible(field, true);
         return field.get(target);
      } catch (final Exception ex) {
         throw new AccessingFieldValueFailedException(field.getName(), target, ContextCache.getFieldContext(field), ex);
      }
   }

   public static Method getGetter(final Class<?> clazz, final String propertyName) {
      final String appendix = propertyName.substring(0, 1).toUpperCase(getLocaleProvider().getLocale()) + propertyName.substring(1);
      try {
         return clazz.getDeclaredMethod("get" + appendix);
      } catch (final NoSuchMethodException ex) {
         LOG.trace("getXXX method not found.", ex);
      }
      try {
         return clazz.getDeclaredMethod("is" + appendix);
      } catch (final NoSuchMethodException ex) {
         LOG.trace("isXXX method not found.", ex);
         return null;
      }
   }

   public static Method getGetterRecursive(final Class<?> clazz, final String propertyName) {
      final Method m = getGetter(clazz, propertyName);
      if (m != null)
         return m;

      final Class<?> superclazz = clazz.getSuperclass();
      if (superclazz == null)
         return null;

      return getGetterRecursive(superclazz, propertyName);
   }

   public static List<Method> getInterfaceMethods(final Method method) {
      // static methods cannot be overridden
      if (isStatic(method))
         return null;

      final Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
      if (interfaces.length == 0)
         return null;

      final String methodName = method.getName();
      final Class<?>[] parameterTypes = method.getParameterTypes();

      final List<Method> methods = getCollectionFactory().createList(interfaces.length);
      for (final Class<?> iface : interfaces) {
         final Method m = getMethod(iface, methodName, parameterTypes);
         if (m != null) {
            methods.add(m);
         }
      }
      return methods;
   }

   /**
    * @param clazz the class to inspect
    * @return a set with all implemented interfaces
    */
   public static Set<Class<?>> getInterfacesRecursive(final Class<?> clazz) {
      final Set<Class<?>> interfaces = getCollectionFactory().createSet(2);
      return getInterfacesRecursive(clazz, interfaces);
   }

   private static Set<Class<?>> getInterfacesRecursive(Class<?> clazz, final Set<Class<?>> interfaces) {
      while (clazz != null) {
         for (final Class<?> next : clazz.getInterfaces()) {
            interfaces.add(next);
            getInterfacesRecursive(next, interfaces);
         }
         clazz = clazz.getSuperclass();
      }
      return interfaces;
   }

   /**
    * @return the method or null if the method does not exist
    */
   public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      try {
         return clazz.getDeclaredMethod(methodName, parameterTypes);
      } catch (final NoSuchMethodException ex) {
         return null;
      }
   }

   /**
    * @return the method or null if the method does not exist
    */
   public static Method getMethodRecursive(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      final Method m = getMethod(clazz, methodName, parameterTypes);
      if (m != null)
         return m;

      final Class<?> superclazz = clazz.getSuperclass();
      if (superclazz == null)
         return null;

      return getMethodRecursive(superclazz, methodName, parameterTypes);
   }

   /**
    * Returns an array of arrays that represent the annotations on the formal parameters, in declaration order,
    * of the method represented by this method.
    *
    * @param method the method to inspect
    * @param inspectInterfaces whether to also return annotations declared on interface method declaration
    * @return an array of arrays that represent the annotations on the formal parameters, in declaration order,
    *         of the method represented by this method.
    */
   @SuppressWarnings("unchecked")
   public static Annotation[][] getParameterAnnotations(final Method method, final boolean inspectInterfaces) {
      if (!inspectInterfaces || !isPublic(method))
         return method.getParameterAnnotations();

      final String methodName = method.getName();
      final Class<?>[] methodParameterTypes = method.getParameterTypes();
      final int methodParameterTypesCount = methodParameterTypes.length;

      final HashSet<Annotation>[] methodParameterAnnotations = new HashSet[methodParameterTypesCount];

      final Class<?> clazz = method.getDeclaringClass();
      final Set<Class<?>> classes = ReflectionUtils.getInterfacesRecursive(clazz);
      classes.add(clazz);
      for (final Class<?> nextClass : classes) {
         try {
            final Method nextMethod = nextClass.getDeclaredMethod(methodName, methodParameterTypes);
            for (int i = 0; i < methodParameterTypesCount; i++) {
               final Annotation[] paramAnnos = nextMethod.getParameterAnnotations()[i];
               if (paramAnnos.length > 0) {
                  HashSet<Annotation> cummulatedParamAnnos = methodParameterAnnotations[i];
                  if (cummulatedParamAnnos == null) {
                     cummulatedParamAnnos = new HashSet<>();
                     methodParameterAnnotations[i] = cummulatedParamAnnos;
                  }
                  Collections.addAll(cummulatedParamAnnos, paramAnnos);
               }
            }
         } catch (final NoSuchMethodException ex) {
            // ignore
         }
      }

      final Annotation[][] result = new Annotation[methodParameterTypesCount][];
      for (int i = 0; i < methodParameterTypesCount; i++) {
         final HashSet<Annotation> paramAnnos = methodParameterAnnotations[i];
         result[i] = paramAnnos == null ? new Annotation[0] : methodParameterAnnotations[i].toArray(new Annotation[methodParameterAnnotations[i].size()]);

      }
      return result;
   }

   public static Method getSetter(final Class<?> clazz, final String propertyName) {
      final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(getLocaleProvider().getLocale()) + propertyName.substring(1);

      final Method[] declaredMethods = clazz.getDeclaredMethods();
      for (final Method method : declaredMethods)
         if (methodName.equals(method.getName()) && method.getParameterTypes().length == 1)
            return method;
      LOG.trace("No setter for {} not found on class {}.", propertyName, clazz);
      return null;
   }

   public static Method getSetterRecursive(final Class<?> clazz, final String propertyName) {
      final Method m = getSetter(clazz, propertyName);
      if (m != null)
         return m;

      final Class<?> superclazz = clazz.getSuperclass();
      if (superclazz == null)
         return null;

      return getSetterRecursive(superclazz, propertyName);
   }

   public static Method getSuperMethod(final Method method) {
      // static methods cannot be overridden
      if (isStatic(method))
         return null;

      final String methodName = method.getName();
      final Class<?>[] parameterTypes = method.getParameterTypes();

      Class<?> currentClass = method.getDeclaringClass();

      while (currentClass != null && currentClass != Object.class) {
         currentClass = currentClass.getSuperclass();

         final Method m = getMethod(currentClass, methodName, parameterTypes);
         if (m != null && !isPrivate(m))
            return m;
      }
      return null;
   }

   public static String guessFieldName(final Method getter) {
      String fieldName = getter.getName();

      if (fieldName.startsWith("get") && fieldName.length() > 3) {
         fieldName = fieldName.substring(3);
         if (fieldName.length() == 1) {
            fieldName = fieldName.toLowerCase(getLocaleProvider().getLocale());
         } else {
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         }
      } else if (fieldName.startsWith("is") && fieldName.length() > 2) {
         fieldName = fieldName.substring(2);
         if (fieldName.length() == 1) {
            fieldName = fieldName.toLowerCase(getLocaleProvider().getLocale());
         } else {
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
         }
      }

      return fieldName;
   }

   public static boolean hasField(final Class<?> clazz, final String fieldName) {
      return getField(clazz, fieldName) != null;
   }

   public static boolean hasMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      return getMethod(clazz, methodName, parameterTypes) != null;
   }

   /**
    *
    * @param method the method to invoke
    * @param obj the object on which to invoke the method
    * @param args the method arguments
    */
   @SuppressWarnings("unchecked")
   public static <T> T invokeMethod(final Method method, final Object obj, final Object... args) throws InvokingMethodFailedException,
      ConstraintsViolatedException {
      try {
         setAccessible(method, true);

         return (T) method.invoke(obj, args);
      } catch (final Exception ex) {
         if (ex.getCause() instanceof ConstraintsViolatedException)
            throw (ConstraintsViolatedException) ex.getCause();
         throw new InvokingMethodFailedException("Executing method " + method.getName() + " failed.", obj, ContextCache.getMethodReturnValueContext(method),
            ex);
      }
   }

   /**
    * Returns true if an annotation for the specified type is present on this method, else false.
    *
    * @param method the method to inspect
    * @param annotationClass the Class object corresponding to the annotation type
    * @param inspectInterfaces whether to also check annotations declared on interface method declaration
    * @return true if an annotation for the specified annotation type is present on this method, else false
    */
   public static boolean isAnnotationPresent(final Method method, final Class<? extends Annotation> annotationClass, final boolean inspectInterfaces) {
      if (method.isAnnotationPresent(annotationClass))
         return true;

      if (!inspectInterfaces || !isPublic(method))
         return false;

      final String methodName = method.getName();
      final Class<?>[] methodParameterTypes = method.getParameterTypes();

      for (final Class<?> next : getInterfacesRecursive(method.getDeclaringClass())) {
         try {
            if (next.getDeclaredMethod(methodName, methodParameterTypes).isAnnotationPresent(annotationClass))
               return true;
         } catch (final NoSuchMethodException ex) {
            // ignore
         }
      }
      return false;
   }

   public static boolean isClassPresent(final String className) {
      try {
         Class.forName(className);
         return true;
      } catch (final ClassNotFoundException ex) {
         return false;
      } catch (final NoClassDefFoundError ex) {
         LOG.error("Failed to load class [" + className + "]", ex);
         return false;
      }
   }

   public static boolean isFinal(final Member member) {
      return (member.getModifiers() & Modifier.FINAL) != 0;
   }

   /**
    * determines if a method is a JavaBean style getter method
    */
   public static boolean isGetter(final Method method) {
      return method.getParameterTypes().length == 0 && (method.getName().startsWith("is") || method.getName().startsWith("get"));
   }

   // public Constructor getDeclaredConstructorOfNonStaticInnerClass(Class)
   public static boolean isNonStaticInnerClass(final Class<?> clazz) {
      return clazz.getName().indexOf('$') > -1 && (clazz.getModifiers() & Modifier.STATIC) == 0;
   }

   public static boolean isPackage(final Member member) {
      return (member.getModifiers() & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED)) == 0;
   }

   public static boolean isPrivate(final Member member) {
      return (member.getModifiers() & Modifier.PRIVATE) != 0;
   }

   public static boolean isPrivateAccessAllowed() {
      final SecurityManager manager = System.getSecurityManager();
      if (manager != null) {
         try {
            manager.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
         } catch (final SecurityException ex) {
            return false;
         }
      }
      return true;
   }

   public static boolean isProtected(final Member member) {
      return (member.getModifiers() & Modifier.PROTECTED) != 0;
   }

   public static boolean isPublic(final Member member) {
      return (member.getModifiers() & Modifier.PUBLIC) != 0;
   }

   /**
    * determines if a method is a JavaBean style setter method
    */
   public static boolean isSetter(final Method method) {
      final Class<?>[] methodParameterTypes = method.getParameterTypes();

      // check if method has exactly one parameter
      if (methodParameterTypes.length != 1)
         return false;

      final String methodName = method.getName();
      final int methodNameLen = methodName.length();

      // check if the method's name starts with setXXX
      if (methodNameLen < 4 || !methodName.startsWith("set"))
         return false;

      return true;
   }

   public static boolean isStatic(final Member member) {
      return (member.getModifiers() & Modifier.STATIC) != 0;
   }

   public static boolean isTransient(final Member member) {
      return (member.getModifiers() & Modifier.TRANSIENT) != 0;
   }

   /**
    * determines if a method is a void method
    */
   public static boolean isVoidMethod(final Method method) {
      return method.getReturnType() == void.class;
   }

   public static void setAccessible(final AccessibleObject obj, final boolean accessible) {
      if (obj.isAccessible() == accessible)
         return;

      AccessController.doPrivileged(new SetAccessibleAction(obj, accessible));
   }

   public static boolean setViaSetter(final Object target, final String propertyName, final Object propertyValue) {
      assert target != null;
      assert propertyName != null;
      final Method setter = getSetterRecursive(target.getClass(), propertyName);
      if (setter != null) {
         try {
            setter.invoke(target, propertyValue);
         } catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            LOG.debug("Setting {1} failed on {2} failed.", propertyName, target, ex);
            return false;
         }
      }
      return false;
   }

   private ReflectionUtils() {
   }
}
