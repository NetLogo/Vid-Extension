#import "../videoDeviceUtils.h"

#import <AVFoundation/AVCaptureDevice.h>

jobjectArray Java_org_nlogo_extensions_vid_util_VideoDeviceUtils_getDeviceNames(JNIEnv* env, jobject obj)
{
    AVCaptureDeviceDiscoverySession* session = [AVCaptureDeviceDiscoverySession discoverySessionWithDeviceTypes: @[AVCaptureDeviceTypeBuiltInWideAngleCamera, AVCaptureDeviceTypeExternal] mediaType:AVMediaTypeVideo position:AVCaptureDevicePositionUnspecified];

    NSArray<AVCaptureDevice*>* devices = [session devices];

    jobjectArray names = (jobjectArray)env->NewObjectArray((int)[devices count], env->FindClass("java/lang/String"), env->NewStringUTF(""));

    for (unsigned int i = 0; i < [devices count]; i++)
    {
        env->SetObjectArrayElement(names, i, env->NewStringUTF([[devices[i] localizedName] cStringUsingEncoding: NSUTF8StringEncoding]));
    }

    return names;
}
