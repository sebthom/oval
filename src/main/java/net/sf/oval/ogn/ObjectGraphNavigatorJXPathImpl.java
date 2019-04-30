/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.ogn;

import java.lang.reflect.AccessibleObject;
import java.util.Locale;

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPointerFactory;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * <a href="http://commons.apache.org/jxpath/">JXPath</a> based object graph navigator implementation.
 *
 * @author Sebastian Thomschke
 */
public class ObjectGraphNavigatorJXPathImpl implements ObjectGraphNavigator {
   protected static final class BeanPointerEx extends BeanPointer {
      private static final long serialVersionUID = 1L;

      private final JXPathBeanInfo beanInfo;

      public BeanPointerEx(final NodePointer parent, final QName name, final Object bean, final JXPathBeanInfo beanInfo) {
         super(parent, name, bean, beanInfo);
         this.beanInfo = beanInfo;
      }

      public BeanPointerEx(final QName name, final Object bean, final JXPathBeanInfo beanInfo, final Locale locale) {
         super(name, bean, beanInfo, locale);
         this.beanInfo = beanInfo;
      }

      @Override // CHECKSTYLE:IGNORE EqualsHashCode
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (!super.equals(obj))
            return false;
         if (getClass() != obj.getClass())
            return false;
         final BeanPointerEx other = (BeanPointerEx) obj;
         if (beanInfo == null) {
            if (other.beanInfo != null)
               return false;
         } else if (!beanInfo.equals(other.beanInfo))
            return false;
         return true;
      }

      @Override
      public boolean isValidProperty(final QName name) {
         if (!super.isValidProperty(name))
            return false;

         // JXPath's default implementation returns true, even if the given property does not exit
         if (beanInfo.getPropertyDescriptor(name.getName()) == null)
            throw new JXPathNotFoundException("No pointer for xpath: " + toString() + "/" + name);

         return true;
      }
   }

   protected static final class BeanPointerFactoryEx extends BeanPointerFactory {
      @Override
      public NodePointer createNodePointer(final NodePointer parent, final QName name, final Object bean) {
         if (bean == null)
            return new NullPointer(parent, name);

         final JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
         return new BeanPointerEx(parent, name, bean, bi);
      }

      @Override
      public NodePointer createNodePointer(final QName name, final Object bean, final Locale locale) {
         final JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
         return new BeanPointerEx(name, bean, bi, locale);
      }

      @Override
      public int getOrder() {
         return BeanPointerFactory.BEAN_POINTER_FACTORY_ORDER - 1;
      }
   }

   static {
      /*
       * JXPath currently does not distinguish between invalid object graph paths, e.g. by referencing a non-existing property on a Java Bean,
       * and incomplete object graph paths because of null-values.
       * In both cases a JXPathNotFoundException is thrown if JXPathContext.lenient is <code>false</code>, and in both cases a NullPropertyPointer
       * is returned if JXPathContext.lenient is <code>true</code>.
       *
       * Therefore we install a patched BeanPointerFactory that checks the existence of properties and throws a JXPathNotFoundException if it does
       * not exist, no matter to which setting JXPathContext.lenient is set.
       */
      JXPathContextReferenceImpl.addNodePointerFactory(new BeanPointerFactoryEx());
   }

   @Override
   public ObjectGraphNavigationResult navigateTo(final Object root, final String xpath) throws InvalidConfigurationException {
      Assert.argumentNotNull("root", root);
      Assert.argumentNotNull("xpath", xpath);

      try {
         final JXPathContext ctx = JXPathContext.newContext(root);
         ctx.setLenient(true); // do not throw an exception if object graph is incomplete, e.g. contains null-values

         Pointer pointer = ctx.getPointer(xpath);

         // no match found or invalid xpath
         if (pointer instanceof NullPropertyPointer || pointer instanceof NullPointer)
            return null;

         if (pointer instanceof BeanPointer) {
            final Pointer parent = ((BeanPointer) pointer).getImmediateParentPointer();
            if (parent instanceof PropertyPointer) {
               pointer = parent;
            }
         }

         if (pointer instanceof PropertyPointer) {
            final PropertyPointer pp = (PropertyPointer) pointer;
            final Class<?> beanClass = pp.getBean().getClass();
            AccessibleObject accessor = ReflectionUtils.getField(beanClass, pp.getPropertyName());
            if (accessor == null) {
               accessor = ReflectionUtils.getGetter(beanClass, pp.getPropertyName());
            }
            return new ObjectGraphNavigationResult(root, xpath, pp.getBean(), accessor, pointer.getValue());
         }

         throw new InvalidConfigurationException("Don't know how to handle pointer [" + pointer + "] of type [" + pointer.getClass().getName() + "] for xpath ["
            + xpath + "]");
      } catch (final JXPathNotFoundException ex) {
         // thrown if the xpath is invalid
         throw new InvalidConfigurationException(ex);
      }
   }
}
