package com.ajsnarr.main

import com.ajsnarr.hardware.GPIO;

fun main(args: Array<String>) {
  println("Hello, world!")
  
  
  GPIO.initialize();
  
  GPIO.terminate();
}
