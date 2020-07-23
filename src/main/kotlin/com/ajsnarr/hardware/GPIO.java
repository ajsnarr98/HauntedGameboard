package com.ajsnarr.hardware;

import cz.adamh.utils.NativeUtils;

import java.io.IOException;

/**
 * A class containing native methods for communicating with raspberry pi gpio pins.
 */
public class GPIO {
  
  static {
    try {
      NativeUtils.loadLibraryFromJar("/lib/gpio.so");
    } catch (IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static enum Level {
    ON(1),
    OFF(0),
    ERROR(-1);

    private final int val;
    Level(int val) { this.val = val; }
  }

  public static enum Mode {
    PI_INPUT(0),
    PI_OUTPUT(1),
    PI_ALT0(4),
    PI_ALT1(5),
    PI_ALT2(6),
    PI_ALT3(7),
    PI_ALT4(3),
    PI_ALT5(2);

    private final int val;
    Mode(int val) { this.val = val; }
  }

  /**
   * Initialises the pigpio library. Must call before using other functions.
   *
   * @return true if successful, false otherwise.
   */
  public static boolean initialize() {
    return _initialize() >= 0;
  }
  private static native int _initialize();

  /**
   * Terminates the pigpio library. Must be called before quiting.
   *
   * @return true if successful, false otherwise.
   */
  public static boolean terminate() {
    return _terminate() >= 0;
  }
  private static native int _terminate();

  /**
   * Sets the GPIO mode, typically input or output.
   *
   * @param gpio gpio pin to set the mode for
   * @param mode mode to set
   * @return true if successful, false otherwise.
   */
  public static boolean setMode(int gpio, Mode mode) {
    validateGpioPinNum(gpio);
    return _setMode(gpio, mode.val) >= 0;
  }
  private static native int _setMode(int gpio, int mode);

  /**
   * Gets the GPIO mode.
   *
   * @param gpio gpio pin to get mode for
   * @return mode of the given pin
   */
  public static Mode getMode(int gpio) {
    validateGpioPinNum(gpio);

    int modeVal =_getmode(gpio);
    for (Mode mode : Mode.values()) {
      if (mode.val == modeVal) {
        return mode;
      }
    }
    throw new IllegalStateException("Unknown mode returned from native _getMode");
  }
  private static native int _getmode(int gpio);

  /**
   * Reads the GPIO level, on or off.
   *
   * @param gpio gpio pin to read
   * @return on, off, or error.
   */
  public static Level read(int gpio) {
    validateGpioPinNum(gpio);

    int level = _read(gpio);
    if (level > 0) {
      return Level.ON;
    } else if (level == 0) {
      return Level.OFF;
    } else {
      return Level.ERROR;
    }
  }
  private static native int _read(int gpio);

  /**
   * Sets the GPIO level, on or off.
   *
   * @param gpio gpio pin to write to
   * @param level level to set. Must be ON or OFF
   * @return true if successful, false otherwise.
   */
  public static boolean write(int gpio, Level level) {
    validateGpioPinNum(gpio);
    if (level == Level.ERROR) throw new IllegalArgumentException("Must write either ON or OFF");
    return _write(gpio, level.val) >= 0;
  }
  private static native int _write(int gpio, int level);

  /**
   * Clears all waveforms and any related data and stops the current waveform.
   *
   * @return true if successful, false otherwise.
   */
  public static boolean waveClear() {
    return _waveClear() >= 0;
  }
  private static native int _waveClear();

  /**
   * Generate a ramp of waveforms for specified number of steps at the given
   * frequencies.
   *
   * @param gpio gpio pin to write to
   * @param rampFrequencies array of frequencies for each set of steps (in order)
   * @param rampNSteps parallel array of steps (in order)
   * @return true if successful, false otherwise.
   */
  public static boolean waveRamps(int gpio, int[] rampFrequencies, int[] rampNSteps) {
    validateGpioPinNum(gpio);
    if (rampFrequencies.length != rampNSteps.length) {
      throw new IllegalArgumentException("Both array args must have same length.");
    }
    if (rampFrequencies.length == 0) {
      throw new IllegalArgumentException("Passed waveRamp arrays cannot be empty");
    }
    return _waveRamps(gpio, rampFrequencies, rampNSteps) >= 0;
  }
  private static native int _waveRamps(int gpio, int[] rampFrequencies, int[] rampNSteps);

  /**
   * Return whether or not a waveform is being transmitted.
   * @return true if a waveform is being transmitted, false otherwise.
   */
  public static boolean waveIsBusy() {
    return _waveIsBusy();
  }
  private static native boolean _waveIsBusy();

  /**
   * Makes sure gpio is valid
   * @param gpio gpio pin number
   */
  private static void validateGpioPinNum(int gpio) throws IllegalArgumentException {
    if (gpio < 0 || gpio > 27) throw new IllegalArgumentException("Invalid GPIO pin number: " + gpio);
  }
}

