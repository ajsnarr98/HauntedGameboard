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
  public static boolean setMode(int gpio, int mode) {
    return _setMode(gpio, mode) >= 0;
  }
  private static native int _setMode(int gpio, int mode);

  /**
   * Gets the GPIO mode.
   *
   * @param gpio gpio pin to get mode for
   * @return mode of the given pin
   */
  public static int getMode(int gpio) {
    return _getmode(gpio);
  }
  private static native int _getmode(int gpio);

  /**
   * Reads the GPIO level, on or off.
   *
   * @param gpio gpio pin to read
   * @return GPIO level
   */
  public static int read(int gpio) {
    return _read(gpio);
  }
  private static native int _read(int gpio);

  /**
   * Sets the GPIO level, on or off.
   *
   * @param gpio gpio pin to write to
   * @param level level to set
   * @return true if successful, false otherwise.
   */
  public static boolean write(int gpio, int level) {
    return _write(gpio, level) >= 0;
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
}

