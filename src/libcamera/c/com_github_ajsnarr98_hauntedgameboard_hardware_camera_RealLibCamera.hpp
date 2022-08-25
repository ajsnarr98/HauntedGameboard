#include <jni.h>
/* Header for class com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera */

#ifndef _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
#define _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera

#pragma once

#include <sys/mman.h>

#include <condition_variable>
#include <iostream>
#include <memory>
#include <mutex>
#include <queue>
#include <set>
#include <string>
#include <thread>
#include <variant>

#include <libcamera/base/span.h>
#include <libcamera/camera.h>
#include <libcamera/camera_manager.h>
#include <libcamera/control_ids.h>
#include <libcamera/controls.h>
#include <libcamera/formats.h>
#include <libcamera/framebuffer_allocator.h>
#include <libcamera/property_ids.h>

extern "C" {
/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    showDebugLog
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_showDebugLog
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    acquireCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_acquireCamera
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    releaseCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_releaseCamera
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    takePicture
 * Signature: (JLcom/github/ajsnarr98/hauntedgameboard/hardware/camera/RealLibCamera/RawPicture;)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_takePicture
  (JNIEnv *, jclass, jlong, jobject);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxConstruct
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_cxxConstruct
  (JNIEnv *, jclass);

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxDestroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_cxxDestroy
  (JNIEnv *, jclass, jlong);

}

namespace controls = libcamera::controls;
namespace properties = libcamera::properties;

class LibcameraUsage
{
public:
	using Stream = libcamera::Stream;
	using FrameBuffer = libcamera::FrameBuffer;
	using ControlList = libcamera::ControlList;
	using Request = libcamera::Request;
	using CameraManager = libcamera::CameraManager;
	using Camera = libcamera::Camera;
	using CameraConfiguration = libcamera::CameraConfiguration;
	using FrameBufferAllocator = libcamera::FrameBufferAllocator;
	using StreamRole = libcamera::StreamRole;
	using StreamRoles = libcamera::StreamRoles;
	using PixelFormat = libcamera::PixelFormat;
	using StreamConfiguration = libcamera::StreamConfiguration;
	using BufferMap = Request::BufferMap;
	using Size = libcamera::Size;
	using Rectangle = libcamera::Rectangle;

	// Error codes
	static constexpr int SUCCESS = 0;
	static constexpr int ERR_CAMERA_MANAGER_FAILED_TO_START = 1;
	static constexpr int ERR_NO_CAMERAS_AVAILABLE = 2;
	static constexpr int ERR_FAILED_TO_FIND_CAMERA = 3;
	static constexpr int ERR_FAILED_TO_ACQUIRE_CAMERA = 4;
	static constexpr int ERR_FAILED_TO_GENERATE_CAMERA_CONFIG = 5;
	static constexpr int ERR_FAILED_TO_VALIDATE_STREAM_CONFIGURATIONS = 6;
	static constexpr int ERR_FAILED_TO_CONFIGURE_STREAMS = 7;
	static constexpr int ERR_FAILED_TO_ALLOCATE_CAPTURE_BUFFERS = 8;
	static constexpr int ERR_REQUEST_CREATION_FAILED = 9;
	static constexpr int ERR_REQUEST_CONCURRENT_STREAMS_WITHOUT_MATCHING_NUMBER_OF_BUFFERS = 10;
	static constexpr int ERR_REQUEST_COULD_NOT_ADD_BUFFER_TO_REQUEST = 11;
	static constexpr int ERR_FAILED_TO_START_CAMERA = 12;
	static constexpr int ERR_CAMERA_FAILED_TO_QUEUE_REQUEST = 13;

	LibcameraUsage();

	std::string const &CameraId() const;

	int AcquireCamera();
	int ReleaseCamera();
	int Configure();

	void Teardown();
	int StartCapture();
	int CleanupAndStopCapture();
	void Wait();

	Stream *StillStream(StreamInfo *info = nullptr) const;

protected:
	std::unique_ptr<Options> options_;

private:
	std::unique_ptr<CameraManager> camera_manager_;
	std::shared_ptr<Camera> camera_;
	std::unique_ptr<CameraConfiguration> configuration_;
	bool camera_acquired_ = false;
	FrameBufferAllocator *allocator_ = nullptr;
	std::map<FrameBuffer *, std::vector<libcamera::Span<uint8_t>>> mapped_buffers_;
	std::map<Stream *, std::queue<FrameBuffer *>> frame_buffers_;
	std::vector<std::unique_ptr<Request>> requests_;
	std::set<CompletedRequest *> completed_requests_;
	bool camera_started_ = false;
	std::mutex completed_requests_mutex_;
	std::counting_semaphore<127> camera_requests_active(0);
	ControlList controls_;
	Stream still_stream_;

	int setupCapture();
	int makeRequests();
	void requestComplete(Request *request);
};

#endif
