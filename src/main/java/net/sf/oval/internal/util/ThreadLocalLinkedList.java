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

import java.util.LinkedList;

/**
 * @author Sebastian Thomschke
 */
public final class ThreadLocalLinkedList<T> extends ThreadLocal<LinkedList<T>> {

   @Override
   public LinkedList<T> initialValue() {
      return new LinkedList<T>();
   }
}
