/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.meta.common.exceptions;

import com.igormaznitsa.meta.annotation.Weight;
import com.igormaznitsa.meta.common.utils.Assertions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service containing all error listeners for the common module methods and providing their notifications.
 *
 * @since 1.0
 */
@ThreadSafe
@Weight(Weight.Unit.NORMAL)
public final class MetaErrorListeners {

  private static final List<MetaErrorListener> ERROR_LISTENERS = new CopyOnWriteArrayList<MetaErrorListener>();

  private MetaErrorListeners() {
  }

  /**
   * Remove all listeners.
   *
   * @since 1.0
   */
  public static void clear() {
    ERROR_LISTENERS.clear();
  }

  /**
   * Add new fireError listener for global fireError events.
   *
   * @param value listener to be added
   * @since 1.0
   */
  public static void addErrorListener(@Nonnull final MetaErrorListener value) {
    ERROR_LISTENERS.add(Assertions.assertNotNull(value));
  }

  /**
   * Remove listener.
   *
   * @param value listener to be removed
   * @since 1.0
   */
  public static void removeErrorListener(@Nonnull final MetaErrorListener value) {
    ERROR_LISTENERS.remove(Assertions.assertNotNull(value));
  }

  /**
   * Check that there are registered listeners.
   *
   * @return true if presented listeners for global fireError events, false otherwise
   * @since 1.0
   */
  public static boolean hasListeners() {
    return !ERROR_LISTENERS.isEmpty();
  }

  /**
   * Send notifications to all listeners.
   *
   * @param text  message text
   * @param error error object
   * @since 1.0
   */
  @Weight(Weight.Unit.VARIABLE)
  public static void fireError(@Nonnull final String text, @Nonnull final Throwable error) {
    for (final MetaErrorListener p : ERROR_LISTENERS) {
      p.onDetectedError(text, error);
    }
  }
}
