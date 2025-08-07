aarch64:
	mkdir -p macosx-aarch64
	g++ -dynamiclib -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/darwin -framework Foundation -framework AVFoundation videoDeviceUtils.mm -o macosx-aarch64/videoDeviceUtils.dylib

x86_64:
	mkdir -p macosx-x86_64
	g++ -dynamiclib -arch x86_64 -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/darwin -framework Foundation -framework AVFoundation videoDeviceUtils.mm -o macosx-x86_64/videoDeviceUtils.dylib

all: aarch64 x86_64
