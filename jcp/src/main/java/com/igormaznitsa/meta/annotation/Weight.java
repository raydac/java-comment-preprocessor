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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to define computational weight of an entity, in fuzzy human-subjective relative units.
 * Also it can mark interface methods as their desired weight. For instance it makes easier to understand should implementation process something in the same thread or in another thread.
 *
 * @since 1.0
 */
@Documented
@Target( {ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface Weight {
  /**
   * Contains weight value for marked entity.
   *
   * @return weight value for marked entity.
   * @since 1.0
   */
  Unit value();

  /**
   * May contain some description or comment.
   *
   * @return comment or note as string
   * @since 1.0
   */
  String comment() default "";

  /**
   * Contains allowed units for execution weight. They are very subjective ones but allow to describe weight of a method in subjective units.
   * <b>NB! Keep in mind that the units are very subjective ones!</b>
   *
   * @since 1.0
   */
  enum Unit {
    /**
     * Variable weight, can be changed in wide interval. Synonym of undefined.
     *
     * @since 1.0
     */
    VARIABLE,
    /**
     * Very very extra light.
     *
     * @since 1.0
     */
    FLUFF,
    /**
     * Lighter than light one. May be just a getter for a field.
     *
     * @since 1.0
     */
    EXTRALIGHT,
    /**
     * Light, for instance a getter with some condition of light logic.
     *
     * @since 1.0
     */
    LIGHT,
    /**
     * Normal weight for regular execution with conditions and short loops.
     *
     * @since 1.0
     */
    NORMAL,
    /**
     * Contains long loops or calls for hard methods.
     *
     * @since 1.0
     */
    HARD,
    /**
     * A Call like ask "Ultimate Question of Life, the Universe, and Everything".
     *
     * @since 1.0
     */
    EXTRAHARD,
    /**
     * A Call of the method can break the universe.
     *
     * @since 1.0
     */
    BLACK_HOLE
  }
}
