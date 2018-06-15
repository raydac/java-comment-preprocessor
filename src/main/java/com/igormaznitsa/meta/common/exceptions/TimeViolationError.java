/*
 * Copyright 2015 Igor Maznitsa.
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
package com.igormaznitsa.meta.common.exceptions;

import com.igormaznitsa.meta.common.utils.TimeGuard;
import javax.annotation.Nonnull;

/**
 * The Error shows that some time bounds violation detected during execution.
 * 
 * @see TimeGuard
 * @since 1.0
 */
public class TimeViolationError extends AssertionError {

  private static final long serialVersionUID = 9175073973098827533L;
  
  private final long detectedTimeInMilliseconds;
  private final TimeGuard.TimeData item;
  
  /**
   * The Constructor.
   * @param detectedTimeInMilliseconds the detected time in milliseconds.
   * @param item the data container contains registration data for time watcher
   * @since 1.0
   */
  public TimeViolationError(final long detectedTimeInMilliseconds, @Nonnull final TimeGuard.TimeData item){
    super(item.getAlertMessage());
    this.detectedTimeInMilliseconds = detectedTimeInMilliseconds;
    this.item = item;
  }
  
  /**
   * Get the difference between the expected max time and the detected time.
   * @return difference in milliseconds between max time and detected time
   * @since 1.0
   */
  public long getDetectedViolationInMilliseconds(){
    return this.detectedTimeInMilliseconds-this.item.getMaxAllowedDelayInMilliseconds();
  }
  
  /**
   * Get the detected time in milliseconds.
   * @return the detected time in milliseconds
   * @since 1.0
   */
  public long getDetectedTimeInMilliseconds(){
    return this.detectedTimeInMilliseconds;
  }
  
  /**
   * Get the data container which was created during time watcher registration.
   * @return the data container contains data for time watcher
   */
  @Nonnull
  public TimeGuard.TimeData getData(){
    return this.item;
  }
}
