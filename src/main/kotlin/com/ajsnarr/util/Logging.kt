
package com.ajsnarr.util

var LOG_LEVEL: LogLevel = LogLevel.VERBOSE

enum class LogLevel (val value: Int) {
  VERBOSE(0),
  DEBUG(1),
  INFO(2),
  ERROR(3);
}

