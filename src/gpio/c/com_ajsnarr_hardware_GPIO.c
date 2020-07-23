#include <jni.h>
/* Header for class com_ajsnarr_hardware_GPIO */

#ifndef _Included_com_ajsnarr_hardware_GPIO
#define _Included_com_ajsnarr_hardware_GPIO

// #include <stdio.h>
// #include <stdlib.h>
// #include <string.h>
// #include <signal.h>

#include <pigpio.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1initialize
  (JNIEnv *env, jclass clz) {
    printf("Hello from JNI!\n");
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _terminate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1terminate
  (JNIEnv *env, jclass clz) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _setMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1setMode
  (JNIEnv *env, jclass clz, jint gpio, jint mode) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _getmode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1getmode
  (JNIEnv *env, jclass clz, jint gpio) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1read
  (JNIEnv *env, jclass clz, jint gpio) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _write
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1write
  (JNIEnv *env, jclass clz, jint gpio, jint level) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _waveClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1waveClear
  (JNIEnv *env, jclass clz) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _waveRamps
 * Signature: (I[I[I)I
 */
JNIEXPORT jint JNICALL Java_com_ajsnarr_hardware_GPIO__1waveRamps
  (JNIEnv *env, jclass clz, jint gpio, jintArray rampFreq, jintArray rampNSteps) {
    return 0;
  }

/*
 * Class:     com_ajsnarr_hardware_GPIO
 * Method:    _waveIsBusy
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_ajsnarr_hardware_GPIO__1waveIsBusy
  (JNIEnv *env, jclass clz) {
    return 0;
  }

#ifdef __cplusplus
}
#endif
#endif
