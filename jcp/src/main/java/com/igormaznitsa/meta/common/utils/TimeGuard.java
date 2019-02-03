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

import com.igormaznitsa.meta.annotation.Constraint;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import com.igormaznitsa.meta.annotation.Weight;
import com.igormaznitsa.meta.common.exceptions.MetaErrorListeners;
import com.igormaznitsa.meta.common.exceptions.TimeViolationError;
import com.igormaznitsa.meta.common.exceptions.UnexpectedProcessingError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * Allows to detect violations of execution time for code blocks or just measure time for them. It works separately for every Thread through ThreadLocal and check stack depth to be
 * informed about current operation level.
 *
 * @since 1.0
 */
@ThreadSafe
public final class TimeGuard {

  /**
   * Some variant of "null-device" for time alerts, it does absolutely nothing.
   *
   * @since 1.0
   */
  public static final TimeAlertListener NULL_TIME_ALERT_LISTENER = new TimeAlertListener() {
    private static final long serialVersionUID = -2291183279100986316L;

    @Override
    public void onTimeAlert(final long detectedTimeDelayInMilliseconds, final TimeData timeData) {
    }
  };
  /**
   * Inside thread local storage of registered processors.
   *
   * @since 1.0
   */
  @MustNotContainNull
  private static final ThreadLocal<List<TimeData>> REGISTRY = new ThreadLocal<List<TimeData>>() {
    @Override
    protected List<TimeData> initialValue() {
      return new ArrayList<>();
    }
  };

  private TimeGuard() {
  }

  /**
   * Add a time watcher. As target of notification meta error listeners will be used.
   *
   * @param alertMessage                  message for time violation
   * @param maxAllowedDelayInMilliseconds max allowed delay in milliseconds for executing block
   * @see #check()
   * @see #cancelAll()
   * @see MetaErrorListeners
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  // WARNING! Don't make a call from methods of the class to not break stack depth!
  public static void addGuard(@Nullable final String alertMessage, @Constraint("X>0") final long maxAllowedDelayInMilliseconds) {
    final List<TimeData> list = REGISTRY.get();
    list.add(new TimeData(ThreadUtils.stackDepth(), alertMessage, maxAllowedDelayInMilliseconds, null));
  }

  /**
   * Add a named time point.
   *
   * @param timePointName name for the time point
   * @param listener      listener to be notified
   * @see #checkPoint(java.lang.String)
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  // WARNING! Don't make a call from methods of the class to not break stack depth!
  public static void addPoint(@Nonnull final String timePointName, @Nonnull final TimeAlertListener listener) {
    final List<TimeData> list = REGISTRY.get();
    list.add(new TimeData(ThreadUtils.stackDepth(), timePointName, -1L, assertNotNull(listener)));
  }

  /**
   * Check named time point(s). Listener registered for the point will be notified and the point will be removed.
   *
   * @param timePointName the name of time point
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void checkPoint(@Nonnull final String timePointName) {
    final long time = System.currentTimeMillis();
    final int stackDepth = ThreadUtils.stackDepth();

    final List<TimeData> list = REGISTRY.get();
    final Iterator<TimeData> iterator = list.iterator();

    boolean detected = false;

    while (iterator.hasNext()) {
      final TimeData timeWatchItem = iterator.next();

      if (timeWatchItem.isTimePoint() && timeWatchItem.getDetectedStackDepth() >= stackDepth && timePointName.equals(timeWatchItem.getAlertMessage())) {
        detected = true;
        final long detectedDelay = time - timeWatchItem.getCreationTimeInMilliseconds();
        try {
          timeWatchItem.getAlertListener().onTimeAlert(detectedDelay, timeWatchItem);
        } catch (Exception ex) {
          final UnexpectedProcessingError error = new UnexpectedProcessingError("Error during time point processing", ex);
          MetaErrorListeners.fireError(error.getMessage(), error);
        } finally {
          iterator.remove();
        }
      }
    }
    if (!detected) {
      throw new IllegalStateException("Can't find time point [" + timePointName + ']');
    }
  }

  /**
   * Process all time points for the current stack level.
   *
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void checkPoints() {
    final long time = System.currentTimeMillis();
    final int stackDepth = ThreadUtils.stackDepth();

    final List<TimeData> list = REGISTRY.get();
    final Iterator<TimeData> iterator = list.iterator();

    while (iterator.hasNext()) {
      final TimeData timeWatchItem = iterator.next();

      if (timeWatchItem.isTimePoint() && timeWatchItem.getDetectedStackDepth() >= stackDepth) {
        final long detectedDelay = time - timeWatchItem.getCreationTimeInMilliseconds();
        try {
          timeWatchItem.getAlertListener().onTimeAlert(detectedDelay, timeWatchItem);
        } catch (Exception ex) {
          final UnexpectedProcessingError error = new UnexpectedProcessingError("Error during time point processing", ex);
          MetaErrorListeners.fireError(error.getMessage(), error);
        } finally {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Add a time watcher and provide processor of time violation.
   *
   * @param alertMessage                  message for time violation
   * @param maxAllowedDelayInMilliseconds max allowed delay in milliseconds for executing block
   * @param timeAlertListener             alert listener to be notified, if it is null then the global one will get notification
   * @see #check()
   * @see #cancelAll()
   * @since 1.0
   */
  // WARNING! Don't make a call from methods of the class to not break stack depth!
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void addGuard(@Nullable final String alertMessage,
                              @Constraint("X>0") final long maxAllowedDelayInMilliseconds,
                              @Nullable final TimeAlertListener timeAlertListener
  ) {
    final List<TimeData> list = REGISTRY.get();
    list.add(new TimeData(ThreadUtils.stackDepth(), alertMessage, maxAllowedDelayInMilliseconds, timeAlertListener));
  }

  /**
   * Cancel all time watchers and time points globally for the current thread.
   *
   * @see #cancel()
   * @since 1.0
   */
  @Weight(Weight.Unit.NORMAL)
  public static void cancelAll() {
    final List<TimeData> list = REGISTRY.get();
    list.clear();
    REGISTRY.remove();
  }

  /**
   * Cancel all time watchers and time points for the current stack level.
   *
   * @see #cancelAll()
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void cancel() {
    final int stackDepth = ThreadUtils.stackDepth();

    final List<TimeData> list = REGISTRY.get();
    final Iterator<TimeData> iterator = list.iterator();

    while (iterator.hasNext()) {
      final TimeData timeWatchItem = iterator.next();
      if (timeWatchItem.getDetectedStackDepth() >= stackDepth) {
        iterator.remove();
      }
    }
    if (list.isEmpty()) {
      REGISTRY.remove();
    }
  }

  /**
   * Check all registered time watchers for time bound violations.
   *
   * @see #addGuard(java.lang.String, long)
   * @see #addGuard(java.lang.String, long, com.igormaznitsa.meta.common.utils.TimeGuard.TimeAlertListener)
   * @since 1.0
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the current call stack depth")
  public static void check() {
    final long time = System.currentTimeMillis();

    final int stackDepth = ThreadUtils.stackDepth();

    final List<TimeData> list = REGISTRY.get();
    final Iterator<TimeData> iterator = list.iterator();

    while (iterator.hasNext()) {
      final TimeData timeWatchItem = iterator.next();
      if (timeWatchItem.getDetectedStackDepth() >= stackDepth) {
        final boolean timePoint = timeWatchItem.isTimePoint();
        try {
          final long detectedDelay = time - timeWatchItem.getCreationTimeInMilliseconds();
          if (timePoint) {
            try {
              timeWatchItem.getAlertListener().onTimeAlert(detectedDelay, timeWatchItem);
            } catch (Exception ex) {
              final UnexpectedProcessingError error = new UnexpectedProcessingError("Error during time point processing", ex);
              MetaErrorListeners.fireError(error.getMessage(), error);
            }
          } else if (detectedDelay > timeWatchItem.getMaxAllowedDelayInMilliseconds()) {
            final TimeAlertListener processor = timeWatchItem.getAlertListener();
            if (processor == NULL_TIME_ALERT_LISTENER) {
              MetaErrorListeners.fireError("Detected time violation without defined time alert listener", new TimeViolationError(detectedDelay, timeWatchItem));
            } else {
              try {
                processor.onTimeAlert(detectedDelay, timeWatchItem);
              } catch (Exception ex) {
                final UnexpectedProcessingError error = new UnexpectedProcessingError("Error during time alert processing", ex);
                MetaErrorListeners.fireError(error.getMessage(), error);
              }
            }
          }
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
   * Check that the thread local for the current thread contains time points or watchers.
   *
   * @return true if the thread local storage is empty, false otherwise
   */
  @Weight(value = Weight.Unit.NORMAL, comment = "May create list in thread local storage")
  public static boolean isEmpty() {
    final boolean result = REGISTRY.get().isEmpty();
    if (result) {
      REGISTRY.remove();
    }
    return result;
  }

  /**
   * Interface for any object to be informed about time alerts.
   *
   * @since 1.0
   */
  @ThreadSafe
  @Weight(Weight.Unit.EXTRALIGHT)
  public interface TimeAlertListener extends Serializable {

    /**
     * Process time.
     *
     * @param detectedTimeDelayInMilliseconds detected time delay in milliseconds
     * @param timeData                        data container contains initial parameters.
     * @since 1.0
     */
    void onTimeAlert(long detectedTimeDelayInMilliseconds, @Nonnull TimeData timeData);
  }

  /**
   * Data container for time watching action.
   *
   * @since 1.0
   */
  @ThreadSafe
  @Immutable
  public static final class TimeData implements Serializable {

    private static final long serialVersionUID = -2417415112571257128L;

    /**
     * Contains detected stack depth for creation.
     *
     * @since 1.0
     */
    private final int stackDepth;

    /**
     * Max allowed time delay in milliseconds.
     *
     * @since 1.0
     */
    private final long maxAllowedDelayInMilliseconds;

    /**
     * The Creation time of the data container in milliseconds.
     *
     * @since 1.0
     */
    private final long creationTimeInMilliseconds;

    /**
     * The Alert message to be provided into log or somewhere else, for time points it is ID.
     *
     * @since 1.0
     */
    private final String alertMessage;

    /**
     * Some provided processor to be called for alert.
     *
     * @since 1.0
     */
    private final TimeAlertListener alertListener;

    /**
     * The Constructor
     *
     * @param stackDepth                    stack depth
     * @param alertMessage                  alert message for time violation
     * @param maxAllowedDelayInMilliseconds max allowed time gap in milliseconds
     * @param violationListener             listener for the violation alert
     * @since 1.0
     */
    @Weight(Weight.Unit.LIGHT)
    public TimeData(@Constraint("X>1") final int stackDepth, @Nonnull final String alertMessage, final long maxAllowedDelayInMilliseconds, @Nullable final TimeAlertListener violationListener) {
      this.stackDepth = stackDepth;
      this.maxAllowedDelayInMilliseconds = maxAllowedDelayInMilliseconds;
      this.creationTimeInMilliseconds = System.currentTimeMillis();
      this.alertMessage = alertMessage;
      this.alertListener = GetUtils.ensureNonNull(violationListener, NULL_TIME_ALERT_LISTENER);
    }

    /**
     * Get alert listener if provided
     *
     * @return the provided alert listener
     * @since 1.0
     */
    @Nonnull
    public TimeAlertListener getAlertListener() {
      return this.alertListener;
    }

    /**
     * Get the alert message. For time points it is ID.
     *
     * @return defined alert message.
     * @since 1.0
     */
    @Nullable
    public String getAlertMessage() {
      return this.alertMessage;
    }

    /**
     * Get the detected stack depth during the container creation.
     *
     * @return the detected stack depth
     * @since 1.0
     */
    public int getDetectedStackDepth() {
      return this.stackDepth;
    }

    /**
     * Get the creation time of the container.
     *
     * @return the creation time in milliseconds
     * @since 1.0
     */
    public long getCreationTimeInMilliseconds() {
      return this.creationTimeInMilliseconds;
    }

    /**
     * Get defined max allowed time delay in milliseconds.
     *
     * @return the max allowed time delay in milliseconds
     * @since 1.0
     */
    public long getMaxAllowedDelayInMilliseconds() {
      return this.maxAllowedDelayInMilliseconds;
    }

    /**
     * Check that the object represents a named time point.
     *
     * @return true if the object represents a time point created for statistics.
     */
    public boolean isTimePoint() {
      return this.maxAllowedDelayInMilliseconds < 0L;
    }
  }
}
