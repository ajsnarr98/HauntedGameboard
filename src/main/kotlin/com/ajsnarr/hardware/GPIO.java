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
//    System.loadLibrary("gpio");
  }
  
  private static native int _initialize();
  public static int initialize() {
    return _initialize();
  }
  
  private static native int _terminate();
  
  private static native int _setMode(int gpio, int mode);
  
  private static native int _getmode(int gpio);
  
  private static native int _read(int gpio);
  
  private static native int _write(int gpio, int level);
  
  private static native int _waveClear();
  
  private static native int _waveRamps(int gpio, int[] rampFrequencies, int[] rampNSteps);
  
  private static native boolean _waveIsBusy();
}

