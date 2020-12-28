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
#include <android/bitmap.h>

#define LOG_TAG    "DDLog-jni"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)
using namespace cv;
using namespace std;
char filepath1[100] = "/storage/emulated/0/panorama_stitched.jpg";


class StitchC{
public:
    Ptr<Stitcher> Stitcher;
    bool Success;
};


Mat finalMat;

StitchC GStitcher;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_try2_ImageStitchNative_initStitcher(JNIEnv *env, jclass clazz) {
    Stitcher::Mode mode = Stitcher::PANORAMA;
    Ptr<Stitcher> stitcher = cv::Stitcher::create(mode);
    cv::Mat m;


    // stitcher.setRegistrationResol(0.6);
    // stitcher.setWaveCorrection(false);
    /*=match_conf defaults to 0.65, I choose 0.8, if there is too much feature, there will be no feature points, and 0.8 will fail*/
    detail::BestOf2NearestMatcher *matcher = new detail::BestOf2NearestMatcher(false, 0.25f);
    stitcher->setFeaturesMatcher(matcher);
    stitcher->setBundleAdjuster(new detail::BundleAdjusterRay());
    stitcher->setSeamFinder(new detail::NoSeamFinder);
    stitcher->setExposureCompensator(new detail::NoExposureCompensator());//exposure compensation
    stitcher->setBlender(new detail::FeatherBlender());

    StitchC s;
    s.Stitcher = stitcher;
    s.Success = false;
    GStitcher = s;
    return 0;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_try2_ImageStitchNative_stitchMats2(JNIEnv *env, jclass clazz, jlong mat1,
                                                    jlong mat2, jlong stitched, jlong masked1, jlong masked2, jboolean isleft) {
    Mat* pm1 = (Mat*) mat1;
    Mat* pm2 = (Mat*) mat2;
    Mat* pstitched = (Mat*) stitched;
    Mat* pmasked1 = (Mat*) masked1;
    Mat* pmasked2 = (Mat*) masked2;

    vector<Mat> mats;
    mats.push_back(*pm1);
    mats.push_back(*pm2);
    // mats.push_back(pm1->clone());
    // mats.push_back(pm2->clone());

    // Ptr<Stitcher> stitcher = new Ptr<Stitcher>(pstitcher);

    Stitcher::Mode mode = Stitcher::PANORAMA;
    Ptr<Stitcher> stitcher = cv::Stitcher::create(mode);

    stitcher->setRegistrationResol(0.6);
    stitcher.setWaveCorrection(false);
    // =match_conf defaults to 0.65, I choose 0.8, if there is too much feature, there will be no feature points, and 0.8 will fail
    detail::BestOf2NearestMatcher *matcher = new detail::BestOf2NearestMatcher(false, 0.25f);
    stitcher->setFeaturesMatcher(matcher);
    stitcher->setBundleAdjuster(new detail::BundleAdjusterRay());
    stitcher->setSeamFinder(new detail::NoSeamFinder);
    stitcher->setExposureCompensator(new detail::NoExposureCompensator());//exposure compensation
    stitcher->setBlender(new detail::FeatherBlender());

    vector<Mat> masks;
    // get img1 and img2's size
    int row1 = pm1->rows;
    int row2 = pm2->rows;
    int col1 = pm1->cols;
    int col2 = pm2->cols;
    int offset = 0;
    // int offset = 200;

    // img1's mask is a left/right clear mask
    Mat mask1 = Mat::zeros(row1, col1, CV_8UC1);

    int roi1col = 0;
    int roi1width = col1;
    // img1's ROI is same col size as img2's if longer
    if(col1>=(col2+offset)){
        if(isleft) // img1 is left to img2, open the ending window
            roi1col = col1 - col2 - offset;
        else // img1 is right to img2, open the starting window
            roi1col = 0;
        roi1width = col2 + offset;
    }

    Rect roi = Rect(roi1col, 0, roi1width, row1);
    mask1(roi).setTo(1);

    // img2's mask is a full clear mask
    Mat mask2 = Mat::ones(row2, col2, CV_8UC1);
    masks.push_back(mask1);
    masks.push_back(mask2);

    // get masked image 1 and 2
    pm1->copyTo(*pmasked1, mask1);
    pm2->copyTo(*pmasked2, mask2);

    // Stitcher::Status state = (&pstitcher)->stitch(mats, masks, *pstitched);
    // Stitcher::Status state = (pGStitcher2)->stitch(mats, masks, *pstitched);
    Stitcher::Status state = stitcher->stitch(mats, masks, *pstitched);

    LOGI ("splicing result: %d", state);
//        finalMat = clipping(finalMat);
    jintArray jint_arr = env->NewIntArray(3);
    jint *elems = env->GetIntArrayElements(jint_arr, NULL);
    elems[0] = state;//status code
    elems[1] = (*pstitched).cols;//wide
    elems[2] = (*pstitched).rows;//high

    if (state == Stitcher::OK){
        LOGI ("splicing success: OK");
    }else{
        LOGI ("splicing failure: fail code %d", state);
    }
    //Synchronize
    env->ReleaseIntArrayElements(jint_arr, elems, 0);

    mask1.release();
    mask2.release();
//    bool isSave  = cv::imwrite(filepath1, finalMat);
    // LOGI ("whether it is stored successfully: %d", isSave);
    return jint_arr;
}


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

    // vector<Mat> masks;
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


// Convert mat to bitmap
void MatToBitmap(JNIEnv *env, Mat &mat, jobject &bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &src = mat;


    try {

        LOGD("nMatToBitmap");
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        LOGD("nMatToBitmap1");

        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        LOGD("nMatToBitmap2 :%d  : %d  :%d", src.dims, src.rows, src.cols);

        CV_Assert(src.dims == 2 && info.height == (uint32_t) src.rows &&
                  info.width == (uint32_t) src.cols);
        LOGD("nMatToBitmap3");
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        LOGD("nMatToBitmap4");
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        LOGD("nMatToBitmap5");
        CV_Assert(pixels);
        LOGD("nMatToBitmap6");


        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
//            Mat tmp(info.height, info.width, CV_8UC3, pixels);
            if (src.type() == CV_8UC1) {
                LOGD("nMatToBitmap: CV_8UC1 -> RGBA_8888");
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if (src.type() == CV_8UC3) {
                LOGD("nMatToBitmap: CV_8UC3 -> RGBA_8888");
//                cvtColor(src, tmp, COLOR_RGB2RGBA);
//                cvtColor(src, tmp, COLOR_RGB2RGBA);
                cvtColor(src, tmp, COLOR_BGR2RGBA);
//                src.copyTo(tmp);
            } else if (src.type() == CV_8UC4) {
                LOGD("nMatToBitmap: CV_8UC4 -> RGBA_8888");
                if (needPremultiplyAlpha)
                    cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else
                    src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if (src.type() == CV_8UC1) {
                LOGD("nMatToBitmap: CV_8UC1 -> RGB_565");
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if (src.type() == CV_8UC3) {
                LOGD("nMatToBitmap: CV_8UC3 -> RGB_565");
//                src.copyTo(tmp);
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if (src.type() == CV_8UC4) {
                LOGD("nMatToBitmap: CV_8UC4 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched Exception: %s", e.what());
        jclass je = env->FindClass("org/opencv/core/CvException");
        if (!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap catched unknown exception (...)");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_try2_ImageStitchNative_getBitmap(JNIEnv *env, jclass type, jobject bitmap) {

    if (finalMat.dims != 2){
        return -1;
    }

    MatToBitmap(env,finalMat,bitmap,false);

    return 0;

}
