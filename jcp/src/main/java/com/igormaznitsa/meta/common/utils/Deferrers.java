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

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import com.igormaznitsa.meta.annotation.Warning;
import com.igormaznitsa.meta.annotation.Weight;
import com.igormaznitsa.meta.common.exceptions.MetaErrorListeners;
import com.igormaznitsa.meta.common.exceptions.UnexpectedProcessingError;
import com.igormaznitsa.meta.common.interfaces.Disposable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * Auxiliary tool to defer some actions and process them in some point in future. it check stack depth and executes only locally (for the stack level) defer actions. <b>It works
 * through ThreadLocal so that actions saved separately for every thread.</b>
 *
 * @see ThreadLocal
 * @since 1.0
 */
@ThreadSafe
public final class Deferrers {

  /**
   * Inside registry for defer actions.
   *
   * @since 1.0
   */
  @SuppressWarnings("AnonymousHasLambdaAlternative")
  @MustNotContainNull
  private static final ThreadLocal<List<Deferred>> REGISTRY = new ThreadLocal<List<Deferred>>() {
    @Override
    protected List<Deferred> initialValue() {
      return new ArrayList<>();
    }
  };

  private Deferrers() {
  }

  /**
   * Defer some action.
   *
   * @param deferred action to be defer.
   * @return the same object from arguments
   * @since 1.0
   */
  @Weight(Weight.Unit.NORMAL)
  public static Deferred defer(@Nonnull final Deferred deferred) {
    REGISTRY.get().add(assertNotNull(deferred));
    return deferred;
  }

  /**
   * Defer object containing public close() method. It catches all exceptions during closing and make notifications only for global error listeners. It finds a public 'close'
   * method of the object and call that through reflection.
   *
   * @param <T>       type of the object to be processed
   * @param closeable an object with close() method.
   * @return the same object from arguments.
   * @since 1.0
   */
  @Warning("Using reflection")
  @Weight(Weight.Unit.NORMAL)
  public static <T> T deferredClose(@Nullable final T closeable) {
    if (closeable != null) {
      defer(new Deferred() {
        private static final long serialVersionUID = 2265124256013043847L;

        @Override
        public void executeDeferred() throws Exception {
          try {
            closeable.getClass().getMethod("close").invoke(closeable);
          } catch (Exception thr) {
            MetaErrorListeners.fireError("Error during deferred closing action", thr);
          }
        }
      });
    }
    return closeable;
  }

  /**
   * Defer closing of an closeable object.
   *
   * @param <T>       type of closeable object
   * @param closeable an object implements java.io.Closeable
   * @return the same closeable object from arguments
   * @since 1.0
   */
  @Weight(Weight.Unit.NORMAL)
  public static <T extends Closeable> T defer(@Nullable final T closeable) {
    if (closeable != null) {
      defer(new Deferred() {
        private static final long serialVersionUID = 2265124256013043847L;

        @Override
        public void executeDeferred() throws Exception {
          IOUtils.closeQuetly(closeable);
        }
      });
    }
    return closeable;
  }

  /**
   * Defer execution of some runnable action.
   *
   * @param runnable some runnable action to be executed in future
   * @return the same runnable object from arguments.
   * @throws AssertionError if the runnable object is null
   */
  @Weight(Weight.Unit.NORMAL)
  public static Runnable defer(@Nonnull final Runnable runnable) {
    assertNotNull(runnable);
    defer(new Deferred() {
      private static final long serialVersionUID = 2061489024868070733L;
      private final Runnable value = runnable;

      @Override
      public void executeDeferred() throws Exception {
        this.value.run();
      }
    });
    return runnable;
  }

  /**
   * Defer execution of some disposable object.
   *
   * @param disposable some disposable object to be processed.
   * @return the same object from arguments
   * @throws AssertionError if the disposable object is null
   * @see Disposable
   */
  @Weight(Weight.Unit.NORMAL)
  public static Disposable defer(@Nonnull final Disposable disposable) {
    assertNotNull(disposable);
    defer(new Deferred() {
      private static final long serialVersionUID = 7940162959962038010L;
      private final Disposable value = disposable;

      @Override
      public void executeDeferred() throws Exception {
        this.value.dispose();
      }
    });
    return disposable;
  }

  /**
   * Cancel all defer actions globally.
   *
   * @since 1.0
   */
  @Weight(Weight.Unit.NORMAL)
  public static void cancelAllDeferredActionsGlobally() {
    final List<Deferred> list = REGISTRY.get();
    list.clear();
    REGISTRY.remove();
  }

  /**
   * Cancel all defer actions for the current stack depth.
   *
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void cancelDeferredActions() {
    final int stackDepth = ThreadUtils.stackDepth();

    final List<Deferred> list = REGISTRY.get();

    list.removeIf(deferred -> deferred.getStackDepth() >= stackDepth);
    if (list.isEmpty()) {
      REGISTRY.remove();
    }
  }

  /**
   * Process all defer actions for the current stack depth level.
   *
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void processDeferredActions() {
    final int stackDepth = ThreadUtils.stackDepth();

    final List<Deferred> list = REGISTRY.get();
    final Iterator<Deferred> iterator = list.iterator();

    while (iterator.hasNext()) {
      final Deferred deferred = iterator.next();
      if (deferred.getStackDepth() >= stackDepth) {
        try {
          deferred.executeDeferred();
        } catch (Exception ex) {
          final UnexpectedProcessingError error = new UnexpectedProcessingError("Error during deferred action processing", ex);
          MetaErrorListeners.fireError(error.getMessage(), error);
        } finally {
          iterator.remove();
        }
      }
    }
    if (list.isEmpty()) {
      REGISTRY.remove();
    }
  }

  /**
   * Check that presented defer actions for the current thread.
   *
   * @return true if presented, false otherwise
   * @since 1.0
   */
  @Weight(Weight.Unit.NORMAL)
  public static boolean isEmpty() {
    final boolean result = REGISTRY.get().isEmpty();
    if (result) {
      REGISTRY.remove();
    }
    return result;
  }

  /**
   * Class wrapping executeDeferred method and stack depth for action.
   *
   * @since 1.0
   */
  @Immutable
  @Weight(Weight.Unit.VARIABLE)
  public abstract static class Deferred implements Serializable {

    private static final long serialVersionUID = -1134788854676942497L;

    private final int stackDepth;

    /**
     * The Constructor.
     *
     * @since 1.0
     */
    @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth@")
    public Deferred() {
      this.stackDepth = ThreadUtils.stackDepth() - 1;
    }

    /**
     * Get the stack depth detected during object creation.
     *
     * @return the stack depth
     * @since 1.0
     */
    public int getStackDepth() {
      return this.stackDepth;
    }

    /**
     * Execute call.
     *
     * @throws Exception it will be thrown for error.
     * @since 1.0
     */
    public abstract void executeDeferred() throws Exception;
  }
}
