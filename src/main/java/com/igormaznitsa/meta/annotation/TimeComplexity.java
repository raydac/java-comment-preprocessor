/*
 * Copyright 2016 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.meta.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.igormaznitsa.meta.Complexity;

/**
 * Allows to mark executable entity by its time complexity description.
 * @since 1.1.2
 * @see Complexity
 * @see Weight
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface TimeComplexity {
  /**
   * Contains time complexity for marked entity.
   *
   * @return time complexity value for marked entity.
   * @since 1.1.2
   */
  Complexity value();

  /**
   * May contain some description or comment.
   *
   * @return comment or note as string
   * @since 1.1.2
   */
  String comment() default "";

}
