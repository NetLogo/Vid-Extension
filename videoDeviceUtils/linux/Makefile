include64 := $(shell find /usr/lib/jvm -name jni.h -exec dirname {} \; | grep amd64)
include32 := $(shell find /usr/lib/jvm -name jni.h -exec dirname {} \; | grep i386)

64:
	mkdir -p linux-amd64
	g++ -shared -fPIC -I $(include64) -I $(include64)/linux videoDeviceUtils.cpp -o linux-amd64/videoDeviceUtils.so

32:
	mkdir -p linux-i386
	g++ -shared -fPIC -m32 -I $(include32) -I $(include32)/linux videoDeviceUtils.cpp -o linux-i386/videoDeviceUtils.so

all: 64 32
