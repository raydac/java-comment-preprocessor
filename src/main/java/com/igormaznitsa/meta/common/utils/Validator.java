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

import javax.annotation.Nullable;

/**
 * Validator to check an object.
 *
 * @param <T> type of object
 * @see Assertions#assertIsValid(java.lang.Object, com.igormaznitsa.meta.common.utils.Validator)
 * @since 1.0.2
 */
public interface Validator<T> {
  /**
   * Validate an object.
   *
   * @param object object to be validated
   * @return true if the object is valid, false otherwise
   */
  boolean isValid(@Nullable T object);
}
