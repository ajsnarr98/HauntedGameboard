#ifndef _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_c
#define _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_c

#include "com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_Companion.h"

#include <sys/mman.h>

#include <algorithm>
#include <condition_variable>
#include <cstring>
#include <fstream>
#include <iostream>
#include <memory>
#include <mutex>
#include <optional>
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

#include <fcntl.h>
#include <unistd.h>

#include <sys/ioctl.h>
#include <linux/videodev2.h>

bool showDebugLog = true;
bool isVerboseLog = true;

const std::string& LOG_PREFIX = "Libcamera: ";

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
	using ColorSpace = libcamera::ColorSpace;
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
	static constexpr int ERR_CAPTURE_STOPPED = 14;
	static constexpr int ERR_WIDTH_OR_HEIGHT_IS_NOT_EVEN = 14;
	static constexpr int ERR_NON_SINGLE_PLANE_YUV_FOUND = 14;
	static constexpr int ERR_NOT_IMPLEMENTED = 15;
	static constexpr int ERR_UNHANDLED_PIXEL_FORMAT = 16;

	LibcameraUsage();

	std::string const &CameraId() const;

	int AcquireCamera();
	int ReleaseCamera();
	int Configure();

	void Teardown();
	int StartCapture();
	int CleanupAndStopCapture();
	int Wait();

	Stream *StillStream() const;
	std::set<Request *> CompletedRequests() const;

	std::vector<libcamera::Span<uint8_t>> Mmap(FrameBuffer *buffer) const;

//protected:
//	std::unique_ptr<Options> options_;

private:
	std::unique_ptr<CameraManager> camera_manager_;
	std::shared_ptr<Camera> camera_;
	std::unique_ptr<CameraConfiguration> configuration_;
	bool camera_acquired_ = false;
	FrameBufferAllocator *allocator_ = nullptr;
	std::map<FrameBuffer *, std::vector<libcamera::Span<uint8_t>>> mapped_buffers_;
	std::map<Stream *, std::queue<FrameBuffer *>> frame_buffers_;
	std::vector<std::unique_ptr<Request>> requests_;
	std::set<Request *> completed_requests_;
	bool camera_started_ = false;
	bool camera_cleaned_up_ = false;
	std::mutex camera_stop_mutex_;
	std::mutex requests_mutex_;
	unsigned int num_requests_completed_ = 0;
	std::condition_variable requests_cond_;
	ControlList controls_;
	Stream *still_stream_;

	int setupCapture();
	int makeRequests();
	void requestComplete(Request *request);
};

static bool check_camera_stack();

static int yuv_to_bgr(jbyte *out, libcamera::PixelFormat pixelFormat, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input);

static int yuv420_to_bgr(jbyte *out, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input);
static int yuyv_to_bgr(jbyte *out, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input);

void logd(const std::string& input)
{
  if (showDebugLog) {
    std::cout << (LOG_PREFIX + input) << std::endl;
  }
}

void logv(const std::string& input)
{
  if (showDebugLog && isVerboseLog) {
    std::cout << (LOG_PREFIX + input) << std::endl;
  }
}

void loge(const std::string& input)
{
    fprintf(stderr, (LOG_PREFIX + input + "\n").c_str());
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    showDebugLog
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_showDebugLog
  (JNIEnv *env, jclass clz, jboolean shouldShowDebugLog, jboolean isVerbose) {

    showDebugLog = shouldShowDebugLog;
    isVerboseLog = isVerbose;
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    acquireCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_acquireCamera
  (JNIEnv *env, jclass clz, jlong thiz) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);

    if (!check_camera_stack()) {
        // return TODO
    }
    int err;

    err = libCameraUsage->AcquireCamera();
    err = libCameraUsage->Configure();
    if (err != libCameraUsage->SUCCESS) {
      // TODO handle errors
    }

    return libCameraUsage->SUCCESS;
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    releaseCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_releaseCamera
  (JNIEnv *env, jclass clz, jlong thiz) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);
    // TODO

    libCameraUsage->CleanupAndStopCapture();
    libCameraUsage->ReleaseCamera();
    libCameraUsage->Teardown();

    return libCameraUsage->SUCCESS;
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    takePicture
 * Signature: (JLcom/github/ajsnarr98/hauntedgameboard/hardware/camera/RealLibCamera/RawPicture;)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_takePicture
  (JNIEnv *env, jclass clz, jlong thiz, jobject jPicture) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);
    // TODO

    int err;
    err = libCameraUsage->StartCapture();
    if (err != libCameraUsage->SUCCESS) {
      // TODO handle error
      return err;
    }

    // wait on capture to finish
    err = libCameraUsage->Wait();
    if (err != libCameraUsage->SUCCESS) {
      // TODO handle error
      return err;
    }

    // get image params
    libcamera::Stream *stream = libCameraUsage->StillStream();
    libcamera::StreamConfiguration const &config = stream->configuration();
    unsigned int width = config.size.width;
    unsigned int height = config.size.height;
    unsigned int stride = config.stride;
    libcamera::PixelFormat pixelFormat = config.pixelFormat;
//    std::optional<libcamera::ColorSpace> colorSpace = config.colorSpace;

    if ((width & 1) || (height & 1)) {
       loge("Both width and height of image must be even");
       libCameraUsage->CleanupAndStopCapture();
       return libCameraUsage->ERR_WIDTH_OR_HEIGHT_IS_NOT_EVEN;
    }

    // load buffers
    // TODO do we only care about the first completed request?
    libcamera::Request *req;
    std::set<libcamera::Request *> completedRequests = libCameraUsage->CompletedRequests();
    logv("Number of completed requests: " + std::to_string(completedRequests.size()));
    std::set<libcamera::Request *>::iterator itr;
    for (itr = completedRequests.begin(); itr != completedRequests.end(); itr++) {
      req = *itr;
      break;
    }
    libcamera::Request::BufferMap buffers = req->buffers();
    logv("Buffermap size: " + std::to_string(buffers.size()));
    const std::vector<libcamera::Span<uint8_t>> mem = libCameraUsage->Mmap(buffers[stream]);

    // stop capture and reset capture data
    libCameraUsage->CleanupAndStopCapture();

    // TODO check if buffer is single plane YUV
    // loge("only single plane YUV supported");
    // return libCameraUsage->ERR_NON_SINGLE_PLANE_YUV_FOUND;

    // convert pixels and put data in return object
    auto jRawPictureClz = env->GetObjectClass(jPicture);
    auto jWidthFieldId = env->GetFieldID(jRawPictureClz, "width", "I");
    auto jHeightFieldId = env->GetFieldID(jRawPictureClz, "height", "I");
    auto jPixelsFieldId = env->GetFieldID(jRawPictureClz, "pixels", "[B");

    int bgrPixelsSize = width * height * 3;
    jbyte *nativeBGRPixels = new jbyte[bgrPixelsSize];

    logv("Mem span vector size: " + std::to_string(mem.size()));

    err = yuv_to_bgr(nativeBGRPixels, pixelFormat, width, height, stride, mem[0].data());

    logd("Finished converting to bgr format");

    jbyteArray jBGRPixels = env->NewByteArray(bgrPixelsSize);
    env->SetByteArrayRegion(jBGRPixels, 0, bgrPixelsSize, nativeBGRPixels);
    delete nativeBGRPixels;

    env->SetIntField(jPicture, jWidthFieldId, width);
    env->SetIntField(jPicture, jHeightFieldId, height);
    env->SetObjectField(jPicture, jPixelsFieldId, jBGRPixels);

    return err;
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxConstruct
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_cxxConstruct
  (JNIEnv *env, jclass clz) {
    return reinterpret_cast<jlong>(new LibcameraUsage());
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxDestroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_00024Companion_cxxDestroy
  (JNIEnv *env, jclass clz, jlong thiz) {
    delete reinterpret_cast<LibcameraUsage*>(thiz);
}

static uint8_t b(uint8_t y, uint8_t u, uint8_t v) {
  int b = y + (1.732446 * (u-128));
  // or fast integer computing with a small approximation
  // b = y + (443*(u-128))>>8;
  return std::clamp(b, 0, 255);
}
static uint8_t g(uint8_t y, uint8_t u, uint8_t v) {
  int g = y - (0.698001 * (v-128)) - (0.337633 * (u-128));
  // or fast integer computing with a small approximation
  // g = y - (179*(v-128) + 86*(u-128))>>8;
  return std::clamp(g, 0, 255);
}
static uint8_t r(uint8_t y, uint8_t u, uint8_t v) {
  int r = y + (1.370705 * (v-128));
  // or fast integer computing with a small approximation
  // r = y + (351*(v-128))>>8;
  return std::clamp(r, 0, 255);
}

static int yuv_to_bgr(jbyte *out, libcamera::PixelFormat pixelFormat, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input) {
  if (pixelFormat == libcamera::formats::YUYV) {
    logv("Converting from format YUYV...");
    return yuyv_to_bgr(out, width, height, stride, input);
  } else if (pixelFormat == libcamera::formats::YUV420) {
    logv("Converting from format YUV420...");
    return yuv420_to_bgr(out, width, height, stride, input);
  } else {
    loge("Unhandled Pixel format: " + std::to_string(pixelFormat));
    return LibcameraUsage::ERR_UNHANDLED_PIXEL_FORMAT;
  }
}

static int yuv420_to_bgr(jbyte *out, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input) {
  uint8_t *yBlock = input + 0;
  uint8_t *uBlock = input + (stride * height);
  uint8_t *vBlock = input + (stride * height) + (stride * height)/4;

  unsigned int i = 0;
  unsigned int outPos;
  unsigned int yStart;
  unsigned int yOffsets[] = {0, 1, stride, stride+1};
  int yPos;

  uint8_t y;
  uint8_t u;
  uint8_t v;

  // treat y block like a 2-dimensional array of size width x height
  for (unsigned int h=0; h<height; h+=2) {
    for (unsigned int w=0; w<width; w+=2) {
      u = uBlock[i];
      v = vBlock[i];
      yStart = (h * width) + w;
      // iterate through positions in next 2x2 in yBlock
      for (unsigned int j=0; j<4; j++) {
        yPos = yStart + yOffsets[j];
        outPos = yPos * 3;
        y = yBlock[yPos];

        out[outPos + 0] = b(y, u, v);
        out[outPos + 1] = g(y, u, v);
        out[outPos + 2] = r(y, u, v);
      }
      i++;
    }
  }

  return LibcameraUsage::SUCCESS;
}

static int yuyv_to_bgr(jbyte *out, unsigned int width, unsigned int height, unsigned int stride, uint8_t *input) {
  return LibcameraUsage::ERR_NOT_IMPLEMENTED;
}

/* If we definitely appear to be running the old camera stack, return false.
 * Everything else, Pi or not, we let through.
 */
static bool check_camera_stack()
{
	int fd = open("/dev/video0", O_RDWR, 0);
	if (fd < 0)
		return true;

	v4l2_capability caps;
	int ret = ioctl(fd, VIDIOC_QUERYCAP, &caps);
	close(fd);

	if (ret < 0 || strcmp((char *)caps.driver, "bm2835 mmal"))
		return true;

	loge("ERROR: the system appears to be configured for the legacy camera stack\n");
	return false;
}

LibcameraUsage::LibcameraUsage() {
}

int LibcameraUsage::AcquireCamera() {
  camera_manager_ = std::make_unique<CameraManager>();
	int ret = camera_manager_->start();
  if (ret) {
    loge("camera manager failed to start, code " + std::to_string(-ret));
    return ERR_CAMERA_MANAGER_FAILED_TO_START;
  }

  std::vector<std::shared_ptr<libcamera::Camera>> cameras = camera_manager_->cameras();
	// Do not show USB webcams
	auto rem = std::remove_if(cameras.begin(), cameras.end(),
							  [](auto &cam) { return cam->id().find("/usb") != std::string::npos; });
	cameras.erase(rem, cameras.end());

  if (cameras.size() == 0) {
		loge("no cameras available");
    return ERR_NO_CAMERAS_AVAILABLE;
  }

  // grab the first camera
  std::string const &cam_id = cameras[0]->id();
	camera_ = camera_manager_->get(cam_id);
	if (!camera_) {
		loge("failed to find camera " + cam_id);
    return ERR_FAILED_TO_FIND_CAMERA;
  }

  if (camera_->acquire()) {
		loge("failed to acquire camera " + cam_id);
    return ERR_FAILED_TO_ACQUIRE_CAMERA;
  }
	camera_acquired_ = true;
  logd("Acquired camera " + cam_id);
  return SUCCESS;
}

int LibcameraUsage::ReleaseCamera() {
  if (camera_acquired_) {
		camera_->release();
  }
	camera_acquired_ = false;

  camera_.reset();
	camera_manager_.reset();
	return SUCCESS;
}

int LibcameraUsage::Configure() {

  // todo: check on capturing raw stream as well for full resolution like libcamera-apps?

	StreamRoles stream_roles = { StreamRole::StillCapture };
  configuration_ = camera_->generateConfiguration(stream_roles);
	if (!configuration_) {
		loge("failed to generate configuration");
    return ERR_FAILED_TO_GENERATE_CAMERA_CONFIG;
  }

  int ret = setupCapture();
  if (ret == SUCCESS) {
    still_stream_ = configuration_->at(0).stream();
    logd("Still capture setup complete");
  }
  return ret;
}

std::string const &LibcameraUsage::CameraId() const {
	return camera_->id();
}

int LibcameraUsage::setupCapture() {
  CameraConfiguration::Status validation = configuration_->validate();
	if (validation == CameraConfiguration::Invalid) {
		loge("failed to validate stream configurations");
    return ERR_FAILED_TO_VALIDATE_STREAM_CONFIGURATIONS;
  } else if (validation == CameraConfiguration::Adjusted) {
		logd("Stream configuration adjusted");
  }

  if (camera_->configure(configuration_.get()) < 0) {
		loge("failed to configure streams");
    return ERR_FAILED_TO_CONFIGURE_STREAMS;
  }
	logd("Camera streams configured");

  logv("Available controls:");
	for (auto const &[id, info] : camera_->controls()) {
		logv("    " + id->name() + " : " + info.toString());
  }

  // Next allocate all the buffers we need, mmap them and store them on a free list.
  allocator_ = new FrameBufferAllocator(camera_);
	for (StreamConfiguration &config : *configuration_)
	{
		Stream *stream = config.stream();

		if (allocator_->allocate(stream) < 0) {
			loge("failed to allocate capture buffers");
      return ERR_FAILED_TO_ALLOCATE_CAPTURE_BUFFERS;
    }

		for (const std::unique_ptr<FrameBuffer> &buffer : allocator_->buffers(stream))
		{
			// "Single plane" buffers appear as multi-plane here, but we can spot them because then
			// planes all share the same fd. We accumulate them so as to mmap the buffer only once.
			size_t buffer_size = 0;
			logv("Num buffer planes: " + std::to_string(buffer->planes().size()));
			for (unsigned i = 0; i < buffer->planes().size(); i++)
			{
				const FrameBuffer::Plane &plane = buffer->planes()[i];
				buffer_size += plane.length;
				if (i == buffer->planes().size() - 1 || plane.fd.get() != buffer->planes()[i + 1].fd.get())
				{
				  logv("mmaped a plane at i=" + std::to_string(i));
					void *memory = mmap(NULL, buffer_size, PROT_READ | PROT_WRITE, MAP_SHARED, plane.fd.get(), 0);
					mapped_buffers_[buffer.get()].push_back(
						libcamera::Span<uint8_t>(static_cast<uint8_t *>(memory), buffer_size));
					buffer_size = 0;
				}
			}
			frame_buffers_[stream].push(buffer.get());
		}
	}
	logd("Buffers allocated and mapped");
  return SUCCESS;
}

int LibcameraUsage::StartCapture() {
  // This makes all the Request objects that we shall need.
	int ret = makeRequests();
  if (ret != SUCCESS) {
    return ret;
  }
  camera_cleaned_up_ = false;

  // Build a list of initial controls that we must set in the camera before starting it.
	// We don't overwrite anything the application may have set before calling us.
	
  // crop size
//  Rectangle sensor_area = *camera_->properties().get(properties::ScalerCropMaximum);
//  float roi_x, roi_y, roi_width, roi_height = 0;
//  int x = roi_x * sensor_area.width;
//  int y = roi_y * sensor_area.height;
//  int w = roi_width * sensor_area.width;
//  int h = roi_height * sensor_area.height;
//  Rectangle crop(x, y, w, h);
//  crop.translateBy(sensor_area.topLeft());
//  log("Using crop " + crop.toString());
//  controls_.set(controls::ScalerCrop, crop);

  // For stills capture we set it
	// as long as possible so that we get whatever the exposure profile wants.
  controls_.set(controls::FrameDurationLimits,
						  libcamera::Span<const int64_t, 2>({ INT64_C(100), INT64_C(1000000000) }));

	// controls_.set(controls::ExposureTime, options_->shutter);
	// controls_.set(controls::AnalogueGain, options_->gain);
	controls_.set(controls::AeMeteringMode, 0); // (centre, spot, average, custom)
	controls_.set(controls::AeExposureMode, 0); // (normal, sport)
	controls_.set(controls::ExposureValue, 0); // 0 means no change
	controls_.set(controls::AwbMode, 0); // (auto, incandescent, tungsten, fluorescent, indoor, daylight, cloudy, custom)
	// controls_.set(controls::ColourGains,
	// 				  libcamera::Span<const float, 2>({ options_->awb_gain_r, options_->awb_gain_b }));
	controls_.set(controls::Brightness, 0);
	controls_.set(controls::Contrast, 1.0);
	controls_.set(controls::Saturation, 1.0);
	controls_.set(controls::Sharpness, 1.0);

  // start camera

  if (camera_->start(&controls_)) {
		loge("failed to start camera");
    return ERR_FAILED_TO_START_CAMERA;
  }

  camera_->requestCompleted.connect(this, &LibcameraUsage::requestComplete);

  controls_.clear();
	camera_started_ = true;

  for (std::unique_ptr<Request> &request : requests_)
	{
		if (camera_->queueRequest(request.get()) < 0) {
			loge("Failed to queue request");
      CleanupAndStopCapture();
      return ERR_CAMERA_FAILED_TO_QUEUE_REQUEST;
    }
	}
	logd("Camera started!");
  
  return SUCCESS;
}

int LibcameraUsage::CleanupAndStopCapture() {
  {
		// We don't want QueueRequest to run asynchronously while we stop the camera.
		std::lock_guard<std::mutex> lock(camera_stop_mutex_);
		if (camera_started_)
		{
			if (camera_->stop()) {
				loge("failed to stop camera");
      }

			camera_started_ = false;
			logd("camera stopped");
		}
	}

  if (camera_cleaned_up_) {
    logd("Skipped camera stop/cleanup!");
    return SUCCESS;
  }
  camera_cleaned_up_ = true;

	if (camera_) {
		camera_->requestCompleted.disconnect(this, &LibcameraUsage::requestComplete);
  }

  logd("clearing completed_requests");
	completed_requests_.clear();

  {
    std::lock_guard<std::mutex> lock(requests_mutex_);
    requests_.clear();
    num_requests_completed_ = 0;
    requests_cond_.notify_one();
  }

	controls_.clear(); // no need for mutex here

  logd("Camera capture stopped/cleaned up!");

  return SUCCESS;
}

libcamera::Stream *LibcameraUsage::StillStream() const {
  return still_stream_;
}

std::set<libcamera::Request *> LibcameraUsage::CompletedRequests() const {
  return completed_requests_;
}

std::vector<libcamera::Span<uint8_t>> LibcameraUsage::Mmap(FrameBuffer *buffer) const
{

	auto item = mapped_buffers_.find(buffer);
	if (item == mapped_buffers_.end()) {
	  loge("Frame buffer not in map");
		return {};
  }
	return item->second;
}

int LibcameraUsage::Wait() {
  // wait on capture to finish
  std::unique_lock<std::mutex> L{requests_mutex_};
  requests_cond_.wait(L,[&]()
  {
    return num_requests_completed_ >= requests_.size();
  });

  // if the number of requests completed is 0, capture was canceled
  if (num_requests_completed_ <= 0) {
    return ERR_CAPTURE_STOPPED;
  }

  return SUCCESS;
}

void LibcameraUsage::requestComplete(Request *request) {

  std::lock_guard<std::mutex> lock(requests_mutex_);

  num_requests_completed_++;

  if (request->status() == Request::RequestCancelled)
		return;

  logd("Inserting request into completed requests");
  completed_requests_.insert(request);

  requests_cond_.notify_one();
}

int LibcameraUsage::makeRequests()
{
	auto free_buffers(frame_buffers_);
	while (true)
	{
		for (StreamConfiguration &config : *configuration_)
		{
			Stream *stream = config.stream();
			if (stream == configuration_->at(0).stream())
			{
				if (free_buffers[stream].empty())
				{
					logd("Requests created");
					return SUCCESS;
				}
				std::unique_ptr<Request> request = camera_->createRequest();
				if (!request) {
					loge("failed to make request");
          return ERR_REQUEST_CREATION_FAILED;
        }
        {
          std::lock_guard<std::mutex> lock(requests_mutex_);
				  requests_.push_back(std::move(request));
				}
			} else if (free_buffers[stream].empty()) {
				loge("concurrent streams need matching numbers of buffers");
        return ERR_REQUEST_CONCURRENT_STREAMS_WITHOUT_MATCHING_NUMBER_OF_BUFFERS;
      }

			FrameBuffer *buffer = free_buffers[stream].front();
			free_buffers[stream].pop();
			{
			  std::lock_guard<std::mutex> lock(requests_mutex_);
        if (requests_.back()->addBuffer(stream, buffer) < 0) {
          loge("failed to add buffer to request");
          return ERR_REQUEST_COULD_NOT_ADD_BUFFER_TO_REQUEST;
        }
      }
		}
	}
}

void LibcameraUsage::Teardown() {

  logd("Entering teardown...");

  // stop capture if camera is running
  CleanupAndStopCapture();

  for (auto &iter : mapped_buffers_)
	{
		for (auto &span : iter.second)
			munmap(span.data(), span.size());
	}
	mapped_buffers_.clear();

	delete allocator_;
	allocator_ = nullptr;

	configuration_.reset();

	frame_buffers_.clear();
}

#endif
