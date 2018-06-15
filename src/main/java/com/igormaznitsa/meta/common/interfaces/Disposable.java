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
package com.igormaznitsa.meta.common.interfaces;

import com.igormaznitsa.meta.annotation.Weight;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Auxiliary interface describing disposable object.
 *
 * @since 1.0
 */
@ThreadSafe
@Weight (Weight.Unit.VARIABLE)
public interface Disposable {
  
  /**
   * Check that the object is disposed.
   * @return true if the object is disposed
   * @since 1.0
   */
  boolean isDisposed ();

  /**
   * Dispose object.
   * @since 1.0
   */
  void dispose ();

}
