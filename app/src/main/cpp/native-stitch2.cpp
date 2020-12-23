//
// Created by Ye on 22/12/2020.
//
#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/base.hpp>
#import <opencv2/stitching.hpp>
#import "include/opencv2/imgcodecs.hpp"

#define BORDER_GRAY_LEVEL 0

#include <android/log.h>

#define LOG_TAG    "DDLog-jni"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)
using namespace cv;
using namespace std;
char filepath1[100] = "/storage/emulated/0/panorama_stitched.jpg";


Mat finalMat;


extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_try2_ImageStitchNative_stitchMats(JNIEnv *env, jclass clazz, jlong mat1,
                                                   jlong mat2) {
    Mat* m1 = (Mat*) mat1;
    Mat* m2 = (Mat*) mat2;

    vector<Mat> mats;
    mats.push_back(m1->clone());
    mats.push_back(m2->clone());

    Stitcher::Mode mode = Stitcher::PANORAMA;
    Ptr<Stitcher> stitcher = cv::Stitcher::create(mode);

    //stitcher.setRegistrationResol(0.6);
    // stitcher.setWaveCorrection(false);
    /*=match_conf defaults to 0.65, I choose 0.8, if there is too much feature, there will be no feature points, and 0.8 will fail*/
    detail::BestOf2NearestMatcher *matcher = new detail::BestOf2NearestMatcher(false, 0.5f);
    stitcher->setFeaturesMatcher(matcher);
    stitcher->setBundleAdjuster(new detail::BundleAdjusterRay());
    stitcher->setSeamFinder(new detail::NoSeamFinder);
    stitcher->setExposureCompensator(new detail::NoExposureCompensator());//exposure compensation
    stitcher->setBlender(new detail::FeatherBlender());

    Stitcher::Status state = stitcher->stitch(mats, finalMat);
    // Stitcher::Status state = stitcher.composePanorama(mats, finalMat);

    LOGI ("splicing result: %d", state);
//        finalMat = clipping(finalMat);
    jintArray jint_arr = env->NewIntArray(3);
    jint *elems = env->GetIntArrayElements(jint_arr, NULL);
    elems[0] = state;//status code
    elems[1] = finalMat.cols;//wide
    elems[2] = finalMat.rows;//high

    if (state == Stitcher::OK){
        LOGI ("splicing success: OK");
    }else{
        LOGI ("splicing failure: fail code %d", state);
    }
    //Synchronize
    env->ReleaseIntArrayElements(jint_arr, elems, 0);
//    bool isSave  = cv::imwrite(filepath1, finalMat);
    // LOGI ("whether it is stored successfully: %d", isSave);
    return jint_arr;

}
