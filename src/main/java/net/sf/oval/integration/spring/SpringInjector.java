/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.integration.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.sf.oval.internal.Log;

/**
 * Injects spring beans into unmanaged Java objects having {@link org.springframework.beans.factory.annotation.Autowired},
 * {@link org.springframework.beans.factory.annotation.Value} and {@link javax.inject.Inject} annotations
 * and executes {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet} and {@link javax.annotation.PostConstruct} callback methods.
 *
 * <pre>
 * &lt;bean class="net.sf.oval.integration.spring.SpringInjector" /&gt;
 * </pre>
 *
 * or
 *
 * <pre>
 * &lt;context:component-scan base-package="net.sf.oval.integration.spring" /&gt;
 * </pre>
 *
 * @author Sebastian Thomschke
 */
@Component
public class SpringInjector {
   private static final Log LOG = Log.getLog(SpringInjector.class);

   private static SpringInjector instance;

   public static SpringInjector get() {
      Assert.notNull(instance, "No SpringInjector instance created yet. Add  <bean class=\"" + SpringInjector.class.getName()
         + "\" /> to your spring configuration!");

      return instance;
   }

   @Autowired
   private AutowireCapableBeanFactory beanFactory;

   protected SpringInjector() {
      LOG.info("Instantiated.");

      instance = this;
   }

   /**
    * processes @PostConstruct, InitializingBean#afterPropertiesSet
    */
   public void initialize(final Object unmanagedBean) {
      beanFactory.initializeBean(unmanagedBean, "bean");
   }

   /**
    * processes @Autowired, @Inject
    */
   public void inject(final Object unmanagedBean) {
      beanFactory.autowireBean(unmanagedBean);
   }
}
