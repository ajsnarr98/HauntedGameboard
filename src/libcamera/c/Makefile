
# MAKEFILE for creating JNI shared library

CC = g++  # C++ compiler
CFLAGS = -fPIC -Wall -Wextra -pedantic -faligned-new -Werror -Wfatal-errors -std=gnu++17  # C flags
LDFLAGS = -shared -Wl, -llibcamera-base  -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I/usr/include/libcamera   # linking flags
RM = rm   # rm command
TARGET_LIB = libcamera.so  # target lib

SRCS = com_github_ajsnarr98_hauntedgameboard_hardware_camera_RealLibCamera.cpp # source files

.PHONY: all
all: ${TARGET_LIB}

$(TARGET_LIB): $(SRCS)
	$(CC) ${LDFLAGS} ${CFLAGS} ${SRCS} -o ${TARGET_LIB}

clean:
	-${RM} ${TARGET_LIB} $(SRCS:.c=.d)
