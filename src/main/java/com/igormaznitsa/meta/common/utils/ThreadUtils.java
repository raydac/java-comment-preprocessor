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

import com.igormaznitsa.meta.annotation.Weight;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Auxiliary methods for thread processing.
 * @since 1.0
 */
@ThreadSafe
public final class ThreadUtils {

  private ThreadUtils () {
  }

  /**
   * Just suspend the current thread for defined interval in milliseconds.
   * @param milliseconds milliseconds to sleep
   * @return false if the sleep has been interrupted by InterruptedException, true otherwise.
   * @see Thread#sleep(long) 
   * @since 1.0
   */
  @Weight(Weight.Unit.VARIABLE)
  public static boolean silentSleep(final long milliseconds) {
    boolean result = true;
    try{
      Thread.sleep(milliseconds);
    }catch(InterruptedException ex){
      result = false;
    }
    return result;
  }
  
  /**
   * Get the stack element of the method caller.
   * @return the stack trace element for the calling method.
   * @since 1.0
   */
  @Weight (Weight.Unit.VARIABLE)
  @Nonnull
  public static StackTraceElement stackElement () {
    final StackTraceElement[] allElements = Thread.currentThread().getStackTrace();
    return allElements[2];
  }

  /**
   * Get the stack call depth for the caller.
   * @return the caller method stack depth.
   * @since 1.0
   */
  @Weight (Weight.Unit.VARIABLE)
  public static int stackDepth () {
    return Thread.currentThread().getStackTrace().length - 1;
  }
}
