package com.ajsnarr.main

import com.ajsnarr.hardware.GPIO;

fun main(args: Array<String>) {
  println("Hello, world!")

  val isInit = GPIO.initialize();
  if (isInit) {
    GPIO.terminate();
  } else {
    println("Failed to initialize GPIO")
  }
}
