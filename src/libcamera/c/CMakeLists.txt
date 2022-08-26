project(libcamera)
cmake_minimum_required(VERSION 3.0)

set (CMAKE_EXPORT_COMPILE_COMMANDS ON)
set (CMAKE_CXX_STANDARD 17)
add_compile_options(-Wall -Wextra -pedantic -Wno-unused-parameter -faligned-new -Werror -Wfatal-errors)
add_definitions(-D_FILE_OFFSET_BITS=64)

# jni
find_package(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

# libcamera
find_package(PkgConfig REQUIRED)

pkg_check_modules(LIBCAMERA REQUIRED libcamera)
message(STATUS "libcamera library found:")
message(STATUS "    version: ${LIBCAMERA_VERSION}")
message(STATUS "    libraries: ${LIBCAMERA_LINK_LIBRARIES}")
message(STATUS "    include path: ${LIBCAMERA_INCLUDE_DIRS}")

include_directories(${LIBCAMERA_INCLUDE_DIRS})

add_library(libcamera SHARED com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera.cpp)
