#include <jni.h>
#include <string>
#include <opencv2/core/cvdef.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include<opencv2/opencv.hpp>

using namespace cv;
using namespace std;
extern "C" {
jstring
Java_com_jwx_patriarchsign_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

void getHistogram(Mat &src, int *valArryH, int *valArryV) {
    int h = src.rows;
    int w = src.cols;
    memset(valArryH, 0, (size_t) (w * 4));//必须初始化数组
    memset(valArryV, 0, (size_t) (h * 4));//必须初始化数组
    int perPixelValue;
    for (int col = 0; col < w; col++) {
        for (int row = 0; row < h; row++) {
            perPixelValue = src.at<uchar>(row, col);
            if (perPixelValue == 0)//如果是白底黑字
            {
                valArryH[col] += 1;
                valArryV[row] += 1;
            }
        }
    }
}

Rect getBounds(Mat &src) {
    int w = src.cols;
    int h = src.rows;
    int *valArryH = new int[w];//创建一个用于储存每列白色像素个数的数组
    int *valArryV = new int[h];//创建一个用于储存每行白色像素个数的数组
    getHistogram(src, valArryH, valArryV);
    int left = 0, top = 0, right = w, bottom = h;

    if (valArryH[0] == 0) {
        for (int i = 1; i < w; i++) {
            if (valArryH[i - 1] == 0 && valArryH[i] > 0) {
                left = i;
                break;
            }
        }
    }
    if (valArryH[w - 1] == 0) {
        for (int i = w - 1; i > 0; i--) {
            if (valArryH[i] == 0 && valArryH[i - 1] > 0) {
                right = i;
                break;
            }
        }
    }
    if (valArryV[0] == 0) {
        for (int i = 1; i < h; i++) {
            if (valArryV[i - 1] == 0 && valArryV[i] > 0) {
                top = i;
                break;
            }
        }
    }
    if (valArryV[h - 1] == 0) {
        for (int i = h - 1; i > 0; i--) {
            if (valArryV[i] == 0 && valArryV[i - 1] > 0) {
                bottom = i;
                break;
            }
        }
    }
    return Rect(left, top, right - left, bottom - top);
}

void alphaBackground(Mat &image) {
    int iChannels = image.channels();
    int iRows = image.rows;
    int iCols = image.cols * iChannels;

    if (image.isContinuous()) {
        iCols *= iRows;
        iRows = 1;
    }
    uchar *p;
    for (int i = 0; i < iRows; i++) {
        p = image.ptr<uchar>(i);
        for (int j = 0; j < iCols; j += 4) {
            if ((int) p[j] > 220 && (int) p[j + 1] > 220 && (int) p[j + 2] > 220) {
                p[j + 3] = 0;
            }
        }
    }
}

JNIEXPORT jintArray JNICALL
Java_com_jwx_patriarchsign_utils_BitmapUtils_removeBackground(JNIEnv *env, jclass type, jintArray bmpSrc_, jintArray srcSize_, jintArray dstSize_) {
    jint *bmpSrc = env->GetIntArrayElements(bmpSrc_, NULL);
    jint *srcSize = env->GetIntArrayElements(srcSize_, NULL);
    jint *dstSize = env->GetIntArrayElements(dstSize_, NULL);
    if (bmpSrc == NULL)
        return NULL;

    Mat srcImage(srcSize[1], srcSize[0], CV_8UC4, (unsigned char *) bmpSrc);
    Mat temp, dstImage;
    cvtColor(srcImage, srcImage, COLOR_BGRA2GRAY);
    adaptiveThreshold(srcImage, temp, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 25, 21);
    medianBlur(temp, temp, 5);

    Rect bounds = getBounds(temp);
    if (bounds.width < srcSize[0] / 2 || bounds.height < srcSize[1] / 2)
        return NULL;
    dstImage = srcImage(bounds);

    cvtColor(dstImage, dstImage, COLOR_GRAY2BGRA);

    //修改灰色背景为透明
    alphaBackground(dstImage);
    //降噪
//    medianBlur(dstImage, dstImage, 3);
    //上下翻转
    flip(dstImage, dstImage, 0);

    int dstW = dstImage.cols; //输出图像宽度
    int dstH = dstImage.rows;//输出图像高度
    int length = dstW * dstH;//输出数组长度
    jint *ptr = dstImage.ptr<jint>(0);
    jintArray bmpDst_ = env->NewIntArray(length);//创建要返回的数组
    dstSize[0] = dstW;
    dstSize[1] = dstH;
    env->SetIntArrayRegion(dstSize_, 0, 2, dstSize);//输出图像赋值
    env->SetIntArrayRegion(bmpDst_, 0, length, ptr);//输出size数组赋值

    env->ReleaseIntArrayElements(bmpSrc_, bmpSrc, 0);
    env->ReleaseIntArrayElements(srcSize_, srcSize, 0);
    env->ReleaseIntArrayElements(dstSize_, dstSize, 0);
    return bmpDst_;
}
}

