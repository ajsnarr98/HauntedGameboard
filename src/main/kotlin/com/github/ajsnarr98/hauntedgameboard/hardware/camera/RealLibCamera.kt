package com.github.ajsnarr98.hauntedgameboard.hardware.camera

import com.github.ajsnarr98.hauntedgameboard.hardware.NativeLibraryLoadException
import cz.adamh.utils.NativeUtils
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.IOException

class RealLibCamera : Camera {

    enum class ErrorCode(val code: Int) {
        UNKNOWN(code = -1),
        SUCCESS(code = 0),
        ERR_CAMERA_MANAGER_FAILED_TO_START(code = 1),
        ERR_NO_CAMERAS_AVAILABLE(code = 2),
        ERR_FAILED_TO_FIND_CAMERA(code = 3),
        ERR_FAILED_TO_ACQUIRE_CAMERA(code = 4),
        ERR_FAILED_TO_GENERATE_CAMERA_CONFIG(code = 5),
        ERR_FAILED_TO_VALIDATE_STREAM_CONFIGURATIONS(code = 6),
        ERR_FAILED_TO_CONFIGURE_STREAMS(code = 7),
        ERR_FAILED_TO_ALLOCATE_CAPTURE_BUFFERS(code = 8),
        ERR_REQUEST_CREATION_FAILED(code = 9),
        ERR_REQUEST_CONCURRENT_STREAMS_WITHOUT_MATCHING_NUMBER_OF_BUFFERS(code = 10),
        ERR_REQUEST_COULD_NOT_ADD_BUFFER_TO_REQUEST(code = 11),
        ERR_FAILED_TO_START_CAMERA(code = 12),
        ERR_CAMERA_FAILED_TO_QUEUE_REQUEST(code = 13),
        ERR_CAPTURE_STOPPED(code = 14),
        ERR_WIDTH_OR_HEIGHT_IS_NOT_EVEN(code = 14),
        ERR_NON_SINGLE_PLANE_YUV_FOUND(code = 14),
        ERR_NOT_IMPLEMENTED(code = 15),
        ERR_UNHANDLED_PIXEL_FORMAT(code = 16),
        ERR_USING_LEGACY_CAMERA_STACK(code = 17);

        companion object {
            fun get(code: Int): ErrorCode {
                return ErrorCode.values().firstOrNull { it.code == code } ?: UNKNOWN
            }
        }
    }

    private var memAllocated = true
    private var acquiredCamera = false
    private val cxxThis: Long = cxxConstruct() // using the "store pointers as longs" convention

    override suspend fun initialize(): Boolean {

        // TODO have different mechanism for putting in debug mode?
        showDebugLog(showDebugLog = false, isVerbose = false)

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
            throw Camera.PictureException(
                "Native code failed to take picture with error code: ${
                    ErrorCode.get(
                        err
                    )
                }"
            )
        }
        println("Picture with width, height (${rawPicture.width}, ${rawPicture.height})")
        if (rawPicture.pixels.size != rawPicture.width * rawPicture.height * 3) {
            throw Camera.PictureException(
                "Pixels array does not match expected size." +
                        " Actual size is ${rawPicture.pixels.size} but" +
                        " expected ${rawPicture.width * rawPicture.height * 3}"
            )
        }
        val image = Mat(rawPicture.height, rawPicture.width, CvType.CV_8UC3)
        var i = 0
        for (h in 0 until rawPicture.height) {
            for (w in 0 until rawPicture.width) {
                image.put(
                    h, w,
                    byteArrayOf(
                        rawPicture.pixels[i],
                        rawPicture.pixels[i + 1],
                        rawPicture.pixels[i + 2]
                    )
                )
                i += 3
            }
        }
        return image
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
                throw NativeLibraryLoadException(e)
            }
        }

        private external fun showDebugLog(showDebugLog: Boolean, isVerbose: Boolean)
        private external fun acquireCamera(cxxThis: Long): Int
        private external fun releaseCamera(cxxThis: Long): Int
        private external fun takePicture(cxxThis: Long, picture: RawPicture): Int
        private external fun cxxConstruct(): Long
        private external fun cxxDestroy(cxxThis: Long)
    }
}