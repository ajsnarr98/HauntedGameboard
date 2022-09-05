package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import cz.adamh.utils.NativeUtils
import org.opencv.core.Mat
import java.io.IOException
import kotlin.coroutines.Continuation

class RealLibCamera : Camera {

    private var memAllocated = true
    private val acquiredCamera = false
    private val cxxThis: Long = cxxConstruct() // using the "store pointers as longs" convention

    override suspend fun initialize(): Boolean {
        return false
    }

    override suspend fun takePicture(): Mat {
        TODO("Not yet implemented")
    }

    /**
     * Cleans memory and closes the camera.
     */
    override fun close() {
        if (memAllocated) {
            if (acquiredCamera) {
                releaseCamera(cxxThis)
            }
            cxxDestroy(cxxThis)
            memAllocated = false
        }
    }

    class RawPicture {
        var pixels: Array<IntArray>

        init {
            pixels = Array(0) { IntArray(0) }
        }
    }

    companion object {
        init {
            try {
                NativeUtils.loadLibraryFromJar("/lib/camera.so")
            } catch (e: IOException) {
                throw RuntimeException(e.toString())
            }
        }

        private external fun showDebugLog(showDebugLog: Boolean)
        private external fun acquireCamera(cxxThis: Long): Int
        private external fun releaseCamera(cxxThis: Long): Int
        private external fun takePicture(cxxThis: Long, picture: RawPicture): Int
        private external fun cxxConstruct(): Long
        private external fun cxxDestroy(cxxThis: Long)
    }
}