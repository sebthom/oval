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

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.exception.ObjectGraphNavigatorNotAvailableException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 *
 */
public class ObjectGraphNavigatorRegistry {
   private static final Log LOG = Log.getLog(ObjectGraphNavigatorRegistry.class);

   private final Map<String, ObjectGraphNavigator> cache = Validator.getCollectionFactory().createMap(2);

   private ObjectGraphNavigator _initializeDefaultOGN(final String id) {
      // JXPath support
      if ("jxpath".equals(id) && ReflectionUtils.isClassPresent("org.apache.commons.jxpath.JXPathContext"))
         return registerObjectGraphNavigator("jxpath", new ObjectGraphNavigatorJXPathImpl());

      if ("".equals(id))
         return registerObjectGraphNavigator("", new ObjectGraphNavigatorDefaultImpl());
      return null;
   }

   public ObjectGraphNavigator getObjectGraphNavigator(final String id) {
      Assert.argumentNotNull("id", id);

      ObjectGraphNavigator ogn = cache.get(id);

      if (ogn == null)
         ogn = _initializeDefaultOGN(id);

      if (ogn == null)
         throw new ObjectGraphNavigatorNotAvailableException(id);

      return ogn;
   }

   public ObjectGraphNavigator registerObjectGraphNavigator(final String id, final ObjectGraphNavigator ogn) throws IllegalArgumentException {
      Assert.argumentNotNull("id", id);
      Assert.argumentNotNull("ogn", ogn);

      LOG.info("Object Graph Navigator '{1}' registered: {2}", id, ogn);

      cache.put(id, ogn);
      return ogn;
   }
}
