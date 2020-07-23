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
  
  public static boolean initialize() {
    return _initialize() >= 0;
  }
  private static native int _initialize();
  
  public static boolean terminate() {
    return _terminate() >= 0;
  }
  private static native int _terminate();
  
  public static boolean setMode(int gpio, int mode) {
    return _setMode(gpio, mode) >= 0;
  }
  private static native int _setMode(int gpio, int mode);
  
  public static int getMode(int gpio) {
    return _getmode(gpio);
  }
  private static native int _getmode(int gpio);
  
  public static int read(int gpio) {
    return _read(gpio);
  }
  private static native int _read(int gpio);
  
  public static boolean write(int gpio, int level) {
    return _write(gpio, level) >= 0;
  }
  private static native int _write(int gpio, int level);
  
  public static boolean waveClear() {
    return _waveClear() >= 0;
  }
  private static native int _waveClear();
  
  public static boolean waveRamps(int gpio, int[] rampFrequencies, int[] rampNSteps) {
    return _waveRamps(gpio, rampFrequencies, rampNSteps) >= 0;
  }
  private static native int _waveRamps(int gpio, int[] rampFrequencies, int[] rampNSteps);
  
  public static boolean waveIsBusy() {
    return _waveIsBusy();
  }
  private static native boolean _waveIsBusy();
}

