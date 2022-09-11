/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_github_ajsnarr98_hauntedgameboard_hardware_GPIO */

#include <stdio.h>
#include <string.h>

#include <pigpio.h>

#ifndef _Included_com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
#define _Included_com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _setLogLevel
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1setLogLevel
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1initialize
  (JNIEnv *, jclass);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _terminate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1terminate
  (JNIEnv *, jclass);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _setMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1setMode
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _getmode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1getMode
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1read
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _write
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1write
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _waveClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1waveClear
  (JNIEnv *, jclass);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _waveRamps
 * Signature: (I[I[I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1waveRamps
  (JNIEnv *, jclass, jint, jintArray, jintArray);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_GPIO
 * Method:    _waveIsBusy
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_gpiointerface_RealGPIO__1waveIsBusy
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
