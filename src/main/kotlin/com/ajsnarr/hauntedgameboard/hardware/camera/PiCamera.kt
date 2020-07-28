package com.ajsnarr.hauntedgameboard.hardware.camera

import uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary

class PiCamera : Camera {

    companion object {
        // static initializer
        init {
            // load native library for picam
            // Extract the bundled picam native library to a temporary file and load it
            installTempLibrary();
        }
    }

}