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
package com.igormaznitsa.meta.common.templates;

import com.igormaznitsa.meta.annotation.Warning;
import com.igormaznitsa.meta.common.exceptions.AlreadyDisposedError;
import com.igormaznitsa.meta.common.exceptions.MetaErrorListeners;
import com.igormaznitsa.meta.common.interfaces.Disposable;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Template providing disposable functionality. It makes notification of the GCEPS.
 * 
 * @see #doDispose() 
 * @see MetaErrorListeners
 * @since 1.0
 */
@ThreadSafe
public abstract class DisposableTemplate implements Disposable,Serializable {

  private static final AtomicLong DISPOSABLE_OBJECT_COUNTER = new AtomicLong();
  
  private static final long serialVersionUID = 789238003359873015L;
  
  private final AtomicBoolean disposedFlag = new AtomicBoolean();
  
  /**
   * The Constructor.
   * @since 1.0
   */
  @Warning("Must be called in successors")
  public DisposableTemplate () {
    DISPOSABLE_OBJECT_COUNTER.incrementAndGet();
  }

  /**
   * Auxiliary method to ensure that the object is not disposed.
   * @throws AlreadyDisposedError if the object has been already disposed, with notification of the global error listeners
   * @since 1.0
   */
  protected void assertNotDisposed(){
    if (this.disposedFlag.get()){
      final AlreadyDisposedError error = new AlreadyDisposedError("Object already disposed");
      MetaErrorListeners.fireError("Detected call to disposed object", error);
      throw error;
    }
  }
  
  @Override
  public boolean isDisposed () {
    return this.disposedFlag.get();
  }

  @Override
  public final void dispose () {
    if (this.disposedFlag.compareAndSet(false, true)){
      DISPOSABLE_OBJECT_COUNTER.decrementAndGet();
      doDispose();
    }else{
      assertNotDisposed();
    }
  }

  /**
   * Get the current number of created but not disposed object which have DisposableTemplate as super class.
   * @return long value shows number of non-disposed objects.
   * @since 1.0
   */
  public static long getNonDisposedObjectCounter(){
    return DISPOSABLE_OBJECT_COUNTER.get();
  }
  
  /**
   * The Template method is called once during disposing.
   */
  protected abstract void doDispose();
}
