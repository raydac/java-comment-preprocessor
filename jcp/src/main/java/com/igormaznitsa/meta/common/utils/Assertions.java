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

package com.igormaznitsa.meta.common.utils;

import com.igormaznitsa.meta.annotation.MayContainNull;
import com.igormaznitsa.meta.annotation.Weight;
import com.igormaznitsa.meta.common.exceptions.AlreadyDisposedError;
import com.igormaznitsa.meta.common.exceptions.InvalidObjectError;
import com.igormaznitsa.meta.common.exceptions.MetaErrorListeners;
import com.igormaznitsa.meta.common.interfaces.Disposable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

/**
 * Set of auxiliary methods for assertion.
 *
 * @since 1.0
 */
@ThreadSafe
@Weight(Weight.Unit.LIGHT)
public final class Assertions {

  private Assertions() {
  }

  /**
   * Throw assertion error for some cause
   *
   * @param message description of the cause.
   * @return generated error, but it throws AssertionError before return so that the value just for IDE.
   * @throws AssertionError will be thrown
   * @since 1.0
   */
  @Nonnull
  public static Error fail(@Nullable final String message) {
    final AssertionError error = new AssertionError(GetUtils.ensureNonNull(message, "failed"));
    MetaErrorListeners.fireError("Asserion error", error);
    if (true) {
      throw error;
    }
    return error;
  }

  /**
   * Assert that value is null
   *
   * @param <T>    type of the object to check
   * @param object the object to check
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if the value is not null
   * @since 1.0
   */
  @Nullable
  public static <T> T assertNull(@Nullable final T object) {
    return assertNull(null, object);
  }

  /**
   * Assert that value is null
   *
   * @param <T>         type of the object to check
   * @param failMessage the message to be provided for failure, can be null
   * @param object      the object to check
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if the value is not null
   * @since 1.1.0
   */
  public static <T> T assertNull(@Nullable final String failMessage, @Nullable final T object) {
    if (object != null) {
      final String txt = failMessage == null ? "Object must be NULL" : failMessage;
      final AssertionError error = new AssertionError(txt);
      MetaErrorListeners.fireError(txt, error);
      throw error;
    }
    return object;
  }

  /**
   * Assert that value is not null
   *
   * @param <T>    type of the object to check
   * @param object the object to check
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if the value is null
   * @since 1.0
   */
  @Nonnull
  public static <T> T assertNotNull(@Nullable final T object) {
    return assertNotNull(null, object);
  }

  /**
   * Assert that value is not null
   *
   * @param <T>         type of the object to check
   * @param failMessage the message to be provided as error description, can be null
   * @param object      the object to check
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if the value is null
   * @since 1.1.0
   */
  @Nonnull
  public static <T> T assertNotNull(@Nullable final String failMessage, @Nullable final T object) {
    if (object == null) {
      final String txt = failMessage == null ? "Object must not be NULL" : failMessage;
      final AssertionError error = new AssertionError(txt);
      MetaErrorListeners.fireError(txt, error);
      throw error;
    }
    return object;
  }

  /**
   * Assert that array doesn't contain null value.
   *
   * @param <T>   type of the object to check
   * @param array an array to be checked for null value
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if either array is null or it contains null
   * @since 1.0
   */
  @Nonnull
  public static <T> T[] assertDoesntContainNull(@Nonnull final T[] array) {
    assertNotNull(array);
    for (final T obj : array) {
      if (obj == null) {
        final AssertionError error = new AssertionError("Array must not contain NULL");
        MetaErrorListeners.fireError("Asserion error", error);
        throw error;
      }
    }
    return array;
  }

  /**
   * Assert condition flag is TRUE. GEL will be notified about error.
   *
   * @param message   message describing situation
   * @param condition condition which must be true
   * @throws AssertionError if the condition is not true
   * @since 1.0
   */
  public static void assertTrue(@Nullable final String message, final boolean condition) {
    if (!condition) {
      final AssertionError error = new AssertionError(GetUtils.ensureNonNull(message, "Condition must be TRUE"));
      MetaErrorListeners.fireError(error.getMessage(), error);
      throw error;
    }
  }

  /**
   * Assert that value is equal to some etalon value.
   *
   * @param <T>    type of object to be checked.
   * @param etalon etalon value
   * @param value  value to check
   * @return value if it is equal to etalon
   * @throws AssertionError if the value id not equal to the etalon
   * @since 1.1.1
   */
  public static <T> T assertEquals(@Nullable final T etalon, @Nullable final T value) {
    if (etalon == null) {
      assertNull(value);
    } else {
      if (!(etalon == value || etalon.equals(value))) {
        final AssertionError error = new AssertionError("Value is not equal to etalon");
        MetaErrorListeners.fireError(error.getMessage(), error);
        throw error;
      }
    }
    return value;
  }

  /**
   * Assert condition flag is FALSE. GEL will be notified about error.
   *
   * @param message   message describing situation
   * @param condition condition which must be false
   * @throws AssertionError if the condition is true
   * @since 1.0
   */
  public static void assertFalse(@Nullable final String message, final boolean condition) {
    if (condition) {
      final AssertionError error = new AssertionError(GetUtils.ensureNonNull(message, "Condition must be FALSE"));
      MetaErrorListeners.fireError(error.getMessage(), error);
      throw error;
    }
  }

  /**
   * Assert that collection doesn't contain null value.
   *
   * @param <T>        type of collection to check
   * @param collection a collection to be checked for null value
   * @return the same input parameter if all is ok
   * @throws AssertionError it will be thrown if either collection is null or it contains null
   * @since 1.0
   */
  @Nonnull
  public static <T extends Collection<?>> T assertDoesntContainNull(@Nonnull final T collection) {
    assertNotNull(collection);
    for (final Object obj : collection) {
      assertNotNull(obj);
    }
    return collection;
  }

  /**
   * Assert that a disposable object is not disposed.
   *
   * @param <T>        type of the object
   * @param disposable disposable object to be checked
   * @return the disposable object if it is not disposed yet
   * @throws AlreadyDisposedError it will be thrown if the object is already disposed;
   * @since 1.0
   */
  @Nonnull
  public static <T extends Disposable> T assertNotDisposed(@Nonnull final T disposable) {
    if (disposable.isDisposed()) {
      final AlreadyDisposedError error = new AlreadyDisposedError("Object already disposed");
      MetaErrorListeners.fireError("Asserion error", error);
      throw error;
    }
    return disposable;
  }

  /**
   * Check that object is presented among provided elements and replace the object by equal element from the list.
   *
   * @param <T>  type of object
   * @param obj  object to be checked
   * @param list list of elements for checking
   * @return equal element provided in the list
   * @throws AssertionError if object is not found among defined ones
   * @since 1.0.2
   */
  @SafeVarargs
  @Nullable
  public static <T> T assertAmong(@Nullable T obj, @MayContainNull @Nonnull final T... list) {
    if (obj == null) {
      for (final T i : assertNotNull(list)) {
        if (i == null) {
          return i;
        }
      }
    } else {
      final int objHash = obj.hashCode();
      for (final T i : assertNotNull(list)) {
        if (obj == i || (i != null && objHash == i.hashCode() && obj.equals(i))) {
          return i;
        }
      }
    }
    final AssertionError error = new AssertionError("Object is not found among elements");
    MetaErrorListeners.fireError("Asserion error", error);
    throw error;
  }

  /**
   * Check an object by a validator.
   *
   * @param <T>       object type
   * @param obj       object to be checked
   * @param validator validator for the operation
   * @return the object if it is valid
   * @throws InvalidObjectError will be thrown if the object is invalid
   * @since 1.0.2
   */
  @Nullable
  public static <T> T assertIsValid(@Nullable T obj, @Nonnull Validator<T> validator) {
    if (assertNotNull(validator).isValid(obj)) {
      return obj;
    } else {
      final InvalidObjectError error = new InvalidObjectError("Detected invalid object", obj);
      MetaErrorListeners.fireError("Invalid object", error);
      throw error;
    }
  }

}
