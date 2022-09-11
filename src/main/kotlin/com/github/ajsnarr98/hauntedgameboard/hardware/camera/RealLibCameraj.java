package com.github.ajsnarr98.hauntedgameboard.hardware.camera;

import cz.adamh.utils.NativeUtils;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.Mat;

import java.io.IOException;

public class RealLibCameraj implements Camera {

    enum ErrorCode {
        SUCCESS(0);

        public final int code;

        private ErrorCode(int code) {
            this.code = code;
        }
    }

    static {
        try {
            NativeUtils.loadLibraryFromJar("/lib/camera.so");
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private boolean memAllocated = true;
    private boolean acquiredCamera = false;
    private final long cxxThis; // using the "store pointers as longs" convention

    public RealLibCameraj() {
        this.cxxThis = cxxConstruct();
    }

    @Nullable
    @Override
    public Object initialize(@NotNull Continuation<? super Boolean> $completion) {
        boolean success = acquireCamera(cxxThis) == ErrorCode.SUCCESS.code;
        if (success) {
            acquiredCamera = true;
        }
        $completion.resumeWith(success);
        return null;
    }

    @Nullable
    @Override
    public Object takePicture(@NotNull Continuation<? super Mat> $completion) {
        RawPicture rawPicture = new RawPicture();
        int err = takePicture(cxxThis, rawPicture);
        if (err != ErrorCode.SUCCESS.code) {
            // TODO
        }
        System.out.println("Picture with width, height (${rawPicture.width}, ${rawPicture.height})");
        // TODO
        $completion.resumeWith(new Mat());
        return null;
    }

    /**
     * Cleans memory and closes the camera.
     */
    @Override
    public void close() {
        if (memAllocated) {
            if (acquiredCamera) {
                releaseCamera(cxxThis);
            }
            cxxDestroy(cxxThis);
            memAllocated = false;
        }
    }

    private static native void showDebugLog(boolean showDebugLog);

    private static native int acquireCamera(long cxxThis);

    private static native int releaseCamera(long cxxThis);

    private static native int takePicture(long cxxThis, RawPicture picture);

    private static native long cxxConstruct();

    private static native void cxxDestroy(long cxxThis);

    public static class RawPicture {
        int[] pixels;
        int width;
        int height;

        public RawPicture() {
            pixels = new int[0];
            width = 0;
            height = 0;
        }
    }
}
