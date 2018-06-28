/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal.util;

import static net.sf.oval.Validator.*;

import java.util.List;

/**
 * @author Sebastian Thomschke
 */
public final class ThreadLocalList<T> extends ThreadLocal<List<T>> {

   @Override
   public List<T> initialValue() {
      return getCollectionFactory().createList();
   }
}
