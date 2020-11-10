/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.prowdloner.camera2libraryproject.prowdlib;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

// [Android Logger 에 대한 wrapper 객체]
public final class Prowd_Logger {
  private static final String DEFAULT_TAG = "<<<<<<<<<<<<<MyApp>>>>>>>>>>>>>"; // 원하는 이름으로 변경
  private static final int DEFAULT_MIN_LOG_LEVEL = Log.DEBUG;

  // 로깅시 무시할 클래스 활동에 대한 이름
  private static final Set<String> IGNORED_CLASS_NAMES;

  static {
    IGNORED_CLASS_NAMES = new HashSet<>(3);
    IGNORED_CLASS_NAMES.add("dalvik.system.VMStack");
    IGNORED_CLASS_NAMES.add("java.lang.Thread");
    IGNORED_CLASS_NAMES.add(Prowd_Logger.class.getCanonicalName());
  }

  private final String tag;
  private final String messagePrefix;
  private int minLogLevel = DEFAULT_MIN_LOG_LEVEL;

  // [생성자 구역]
  // 인자값으로 원하는 로깅 접두어를 설정 가능
  public Prowd_Logger(final Class<?> clazz) {
    this(clazz.getSimpleName());
  }
  public Prowd_Logger(final String messagePrefix) {
    this(DEFAULT_TAG, messagePrefix);
  }
  public Prowd_Logger(final String tag, final String messagePrefix) {
    this.tag = tag;
    final String prefix = messagePrefix == null ? getCallerSimpleName() : messagePrefix;
    this.messagePrefix = (prefix.length() > 0) ? prefix + ": " : prefix;
  }
  public Prowd_Logger() {
    this(DEFAULT_TAG, null);
  }
  public Prowd_Logger(final int minLogLevel) {
    this(DEFAULT_TAG, null);
    this.minLogLevel = minLogLevel;
  }

  /**
   * Return caller's simple name.
   *
   * <p>Android getStackTrace() returns an array that looks like this: stackTrace[0]:
   * dalvik.system.VMStack stackTrace[1]: java.lang.Thread stackTrace[2]:
   * com.google.android.apps.unveil.env.UnveilLogger stackTrace[3]:
   * com.google.android.apps.unveil.BaseApplication
   *
   * <p>This function returns the simple version of the first non-filtered name.
   *
   * @return caller's simple name
   */
  private static String getCallerSimpleName() {
    // Get the current callstack so we can pull the class of the caller off of it.
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    for (final StackTraceElement elem : stackTrace) {
      final String className = elem.getClassName();
      if (!IGNORED_CLASS_NAMES.contains(className)) {
        // We're only interested in the simple name of the class, not the complete package.
        final String[] classParts = className.split("\\.");
        return classParts[classParts.length - 1];
      }
    }

    return Prowd_Logger.class.getSimpleName();
  }

  public void setMinLogLevel(final int minLogLevel) {
    this.minLogLevel = minLogLevel;
  }

  public boolean isLoggable(final int logLevel) {
    return logLevel >= minLogLevel || Log.isLoggable(tag, logLevel);
  }

  private String toMessage(final String format, final Object... args) {
    return messagePrefix + (args.length > 0 ? String.format(format, args) : format);
  }

  public void v(final String format, final Object... args) {
    if (isLoggable(Log.VERBOSE)) {
      Log.v(tag, toMessage(format, args));
    }
  }

  public void v(final Throwable t, final String format, final Object... args) {
    if (isLoggable(Log.VERBOSE)) {
      Log.v(tag, toMessage(format, args), t);
    }
  }

  public void d(final String format, final Object... args) {
    if (isLoggable(Log.DEBUG)) {
      Log.d(tag, toMessage(format, args));
    }
  }

  public void d(final Throwable t, final String format, final Object... args) {
    if (isLoggable(Log.DEBUG)) {
      Log.d(tag, toMessage(format, args), t);
    }
  }

  public void i(final String format, final Object... args) {
    if (isLoggable(Log.INFO)) {
      Log.i(tag, toMessage(format, args));
    }
  }

  public void i(final Throwable t, final String format, final Object... args) {
    if (isLoggable(Log.INFO)) {
      Log.i(tag, toMessage(format, args), t);
    }
  }

  public void w(final String format, final Object... args) {
    if (isLoggable(Log.WARN)) {
      Log.w(tag, toMessage(format, args));
    }
  }

  public void w(final Throwable t, final String format, final Object... args) {
    if (isLoggable(Log.WARN)) {
      Log.w(tag, toMessage(format, args), t);
    }
  }

  public void e(final String format, final Object... args) {
    if (isLoggable(Log.ERROR)) {
      Log.e(tag, toMessage(format, args));
    }
  }

  public void e(final Throwable t, final String format, final Object... args) {
    if (isLoggable(Log.ERROR)) {
      Log.e(tag, toMessage(format, args), t);
    }
  }
}
