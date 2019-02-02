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
package com.igormaznitsa.meta.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to describe a runtime exception thrown by method.
 * @since 1.1.0
 */
@Documented
@Target({ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
public @interface ThrowsRuntimeException {
  /**
   * The Class of the runtime exception.
   * @return the class of the runtime exception
   */
  Class<? extends RuntimeException> value();
  /**
   * Description of the exception.
   * @return the description as text.
   */
  String reference () default "";
}
