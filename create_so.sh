#!/bin/bash

# this script should be run from the project home directory

mkdir -p src/lib/

# GPIO
echo ''
echo '--------------------------------'
echo 'Compiling GPIO Interface'
echo '--------------------------------'

# make shared lib
cd src/gpio/c
make

# move to location
cd ../../..
mv src/gpio/c/gpio.so src/lib/gpio.so

# LibCamera
echo ''
echo '--------------------------------'
echo 'Compiling LibCamera Interface'
echo '--------------------------------'

# make shared lib
cd src/libcamera/c
make

# move to location
cd ../../..
mv src/libcamera/c/camera.so src/lib/camera.so
