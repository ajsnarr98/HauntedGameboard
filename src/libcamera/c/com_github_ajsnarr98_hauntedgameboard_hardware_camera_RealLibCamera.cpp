#ifndef _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_c
#define _Included_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_c

#include "com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera.hpp"

bool showDebugLog = true;

std::string const &LOG_PREFIX = "Libcamera: ";

void log(const std::string& input)
{
  if (showDebugLog) {
    std::cout << (LOG_PREFIX + input) << std::endl;
  }
}

void loge(const std::string& input)
{
    fprintf(stderr, LOG_PREFIX + input);
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    showDebugLog
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_showDebugLog
  (JNIEnv *env, jclass clz, jboolean shouldShowDebugLog) {

    showDebugLog = shouldShowDebugLog
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    acquireCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_acquireCamera
  (JNIEnv *env, jclass clz, jlong thiz) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);
    // TODO
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    releaseCamera
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_releaseCamera
  (JNIEnv *env, jclass clz, jlong thiz) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);
    // TODO
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    takePicture
 * Signature: (JLcom/github/ajsnarr98/hauntedgameboard/hardware/camera/RealLibCamera/RawPicture;)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_takePicture
  (JNIEnv *env, jclass clz, jlong thiz, jobject jPicture) {
  
    LibcameraUsage* libCameraUsage = reinterpret_cast<LibcameraUsage*>(thiz);
    // TODO
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxConstruct
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_cxxConstruct
  (JNIEnv *env, jclass clz) {
    return reinterpret_cast<jlong>(new LibcameraUsage());
}

/*
 * Class:     com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera
 * Method:    cxxDestroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera_cxxDestroy
  (JNIEnv *env, jclass clz, jlong thiz) {
    delete reinterpret_cast<LibcameraUsage*>(thiz);
}

/* If we definitely appear to be running the old camera stack, return false.
 * Everything else, Pi or not, we let through.
 */
static boolean check_camera_stack()
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
  log("Acquired camera " + cam_id);
  return SUCCESS
}

int LibcameraUsage::ReleaseCamera() {
  if (camera_acquired_) {
		camera_->release();
  }
	camera_acquired_ = false;

  camera_.reset();
	camera_manager_.reset();
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
    log("Still capture setup complete");
  }
  return ret;
}

std::string const &LibcameraApp::CameraId() const {
	return camera_->id();
}

int LibcameraUsage::setupCapture() {
  CameraConfiguration::Status validation = configuration_->validate();
	if (validation == CameraConfiguration::Invalid) {
		loge("failed to validate stream configurations");
    return ERR_FAILED_TO_VALIDATE_STREAM_CONFIGURATIONS;
  } else if (validation == CameraConfiguration::Adjusted) {
		log("Stream configuration adjusted");
  }

  if (camera_->configure(configuration_.get()) < 0) {
		loge("failed to configure streams");
    return ERR_FAILED_TO_CONFIGURE_STREAMS;
  }
	log("Camera streams configured");

  log("Available controls:");
	for (auto const &[id, info] : camera_->controls()) {
		log("    " + id->name() + " : " + info.toString());
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
			for (unsigned i = 0; i < buffer->planes().size(); i++)
			{
				const FrameBuffer::Plane &plane = buffer->planes()[i];
				buffer_size += plane.length;
				if (i == buffer->planes().size() - 1 || plane.fd.get() != buffer->planes()[i + 1].fd.get())
				{
					void *memory = mmap(NULL, buffer_size, PROT_READ | PROT_WRITE, MAP_SHARED, plane.fd.get(), 0);
					mapped_buffers_[buffer.get()].push_back(
						libcamera::Span<uint8_t>(static_cast<uint8_t *>(memory), buffer_size));
					buffer_size = 0;
				}
			}
			frame_buffers_[stream].push(buffer.get());
		}
	}
	log("Buffers allocated and mapped");
  return SUCCESS;
}

int LibcameraUsage::StartCapture() {
  // This makes all the Request objects that we shall need.
	int ret = makeRequests();
  if (ret != SUCCESS) {
    return ret;
  }

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
    return ERR_FAILED_TO_START_CAMERA
  }

  camera_->requestCompleted.connect(this, &LibcameraUsage::requestComplete);

  controls_.clear();
	camera_started_ = true;

  for (std::unique_ptr<Request> &request : requests_)
	{
		if (camera_->queueRequest(request.get()) < 0) {
			loge("Failed to queue request");
      CleanupAndStopCapture()
      return ERR_CAMERA_FAILED_TO_QUEUE_REQUEST;
    }
	}
	log("Camera started!");
  
  return SUCCESS;
}

int CleanupAndStopCapture() {
  {
		// We don't want QueueRequest to run asynchronously while we stop the camera.
		std::lock_guard<std::mutex> lock(camera_stop_mutex_);
		if (camera_started_)
		{
			if (camera_->stop()) {
				loge("failed to stop camera");
      }

			camera_started_ = false;
		}
	}

	if (camera_) {
		camera_->requestCompleted.disconnect(this, &LibcameraUsage::requestComplete);
  }

	completed_requests_.clear();

	requests_.clear();
	num_requests_completed_ = 0;

	controls_.clear(); // no need for mutex here

  log("Camera capture stopped/cleaned up!");
}

void LibcameraUsage::requestComplete(Request *request) {

  std::lock_guard<std::mutex> lock(requests_mutex_);

  num_requests_completed_++;

  if (request->status() == Request::RequestCancelled)
		return;

  completed_requests_.insert(request);
}

void LibcameraUsage::makeRequests()
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
					log("Requests created");
					return SUCCESS;
				}
				std::unique_ptr<Request> request = camera_->createRequest();
				if (!request) {
					loge("failed to make request");
          return ERR_REQUEST_CREATION_FAILED;
        }
				requests_.push_back(std::move(request));
			} else if (free_buffers[stream].empty()) {
				loge("concurrent streams need matching numbers of buffers");
        return ERR_REQUEST_CONCURRENT_STREAMS_WITHOUT_MATCHING_NUMBER_OF_BUFFERS;
      }

			FrameBuffer *buffer = free_buffers[stream].front();
			free_buffers[stream].pop();
			if (requests_.back()->addBuffer(stream, buffer) < 0) {
				loge("failed to add buffer to request");
        return ERR_REQUEST_COULD_NOT_ADD_BUFFER_TO_REQUEST;
      }
		}
	}
}

void LibcameraUsage::Teardown() {

  if (camera_started_) {
    camera_->stop();
    camera_started_ = false;
  }

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

	streams_.clear();
}

#endif
