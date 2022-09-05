#!/bin/bash

# this script should be run from the project home directory


# GPIO
# make shared lib
cd src/gpio/c
make

# move to location
cd ../../..
mv src/gpio/c/gpio.so src/lib/gpio.so

# LibCamera
# make shared lib
cd src/libcamera/c
make

# move to location
cd ../../..
mv src/libcamera/c/libcamera.so src/lib/libcamera.so
