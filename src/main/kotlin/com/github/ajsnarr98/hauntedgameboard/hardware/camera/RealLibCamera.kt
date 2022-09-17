package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import cz.adamh.utils.NativeUtils
import org.opencv.core.Mat
import java.io.IOException

class RealLibCamera : Camera {

    enum class ErrorCode(val code: Int) {
        SUCCESS(code = 0),
    }

    private var memAllocated = true
    private var acquiredCamera = false
    private val cxxThis: Long = cxxConstruct() // using the "store pointers as longs" convention

    override suspend fun initialize(): Boolean {
        val success = acquireCamera(cxxThis) == ErrorCode.SUCCESS.code
        if (success) {
            acquiredCamera = true
        }
        return success
    }

    override suspend fun takePicture(): Mat {
        val rawPicture = RawPicture()
        val err = takePicture(cxxThis, rawPicture)
        if (err != ErrorCode.SUCCESS.code) {
            // TODO
        }
        println("Picture with width, height (${rawPicture.width}, ${rawPicture.height})")
        // TODO
        return Mat()
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
        var pixels: ByteArray = ByteArray(0) // expected BGR format
        var width: Int = 0
        var height: Int = 0
    }

    companion object {
        init {
            try {
                NativeUtils.loadLibraryFromJar("/nativelib/camera.so")
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