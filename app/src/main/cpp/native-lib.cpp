#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

void antiRed(Mat &matInput, Mat &matResult, jlong filterValue);
void antiGreen(Mat &matInput, Mat &matResult, jlong filterValue);
void antiBlue(Mat &matInput, Mat &matResult, jlong filterValue);
void antiScreen(Mat &matInput, Mat &matResult, jlong filterValue);


extern "C"
JNIEXPORT void JNICALL
Java_com_example_locationlog_102_Activitys_ETC_OpenCV_1Camera_ConvertRGBtoGray(JNIEnv *env,
                                                                           jobject instance,
                                                                           jlong matAddrInput,
                                                                           jlong matAddrResult,
                                                                           jlong filterMod,
                                                                           jlong filterValue
                                                                           ) {
    //전달받은 주소를 Mat객체에 연결한다.
    Mat &matInput = *(Mat *)matAddrInput; //인풋객체
    Mat &matResult = *(Mat *)matAddrResult; //아웃풋객체

    //cvtColor(matInput, matResult, COLOR_RGBA2GRAY); //cvtColor 함수를 사용하여 그레이스케일 영상으로 변환합니다. 참고로 API가 사용하는 색 공간이 다릅니다. Java RGBA / C++ BGR
    //변환된 그레이스케일 영상은 matResult 객체에 담겨 자바 코드에서 사용하게 됩니다.

    switch (filterMod) {
        case 0:
            matResult = matInput;
            break;
        case 1:
//            cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
//            Canny(matResult,matResult,30,30);
            antiRed(matInput,matResult,filterValue);
            break;
        case 2:
//            cvtColor(matInput, matResult, COLOR_BGR2HSV);
            antiGreen(matInput,matResult,filterValue);
            break;
        case 3:
//            cvtColor(matInput, matResult, COLOR_BGR2HSV_FULL);
            antiBlue(matInput,matResult,filterValue);
            break;
        case 4:
//            cvtColor(matInput, matResult, COLOR_BGR2Luv);
            antiScreen(matInput,matResult,filterValue);
            break;
        case 5:
//            GaussianBlur(matInput,matResult,Size(5,5),100,100);
            break;
        case 6:

            break;
        case 7:

            break;
        case 8:

            break;
        case 9:

            break;

    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_locationlog_102_Activitys_ETC_OpenCV_1Camera_RotateImage(JNIEnv *env, jobject instance,
                                                                      jlong matAddrInput,
                                                                      jlong matAddrResult,
                                                                      jint angle) {
    //전달받은 주소를 Mat객체에 연결한다.
        Mat &matInput = *(Mat *)matAddrInput; //인풋객체
        Mat &matResult = *(Mat *)matAddrResult; //아웃풋객체

        //입력받은 값으로 방향을 바꿔준다
        CV_Assert(angle % 90 == 0 && angle <= 360 && angle >= -360);
        if(angle == 270 || angle == -90){
            // Rotate clockwise 270 degrees
            cv::transpose(matInput, matResult);
            cv::flip(matResult, matResult, 0);
        }else if(angle == 180 || angle == -180){
            // Rotate clockwise 180 degrees
            cv::flip(matInput, matResult, -1);
    }else if(angle == 90 || angle == -270){
        // Rotate clockwise 90 degrees
        cv::transpose(matInput, matResult);
        cv::flip(matResult, matResult, 1);
    }else if(angle == 360 || angle == 0 || angle == -360){
        if(matInput.data != matResult.data){
            matInput.copyTo(matResult);
        }
    }
}

void antiRed(Mat &matInput, Mat &matResult, jlong filterValue){
    int height = matInput.rows;
    int width = matInput.cols;
    long tempValue;

    for(int y =0; y<height; y++){
        uchar* pointer_input = matInput.ptr<uchar>(y);
        uchar* pointer_ouput = matResult.ptr<uchar>(y);
        for(int x=0;x<width;x++){
            tempValue = filterValue-pointer_input[x*4+0]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+0];
            pointer_ouput[x*4+0] = tempValue; //r
            pointer_ouput[x*4+1] = pointer_input[x*4+1];     //g
            pointer_ouput[x*4+2] = pointer_input[x*4+2];     //b
            pointer_ouput[x*4+3] = pointer_input[x*4+3];

        }
    }
}

void antiGreen(Mat &matInput, Mat &matResult, jlong filterValue){
    int height = matInput.rows;
    int width = matInput.cols;
    long tempValue;

    for(int y =0; y<height; y++){
        uchar* pointer_input = matInput.ptr<uchar>(y);
        uchar* pointer_ouput = matResult.ptr<uchar>(y);
        for(int x=0;x<width;x++){
            pointer_ouput[x*4+0] = pointer_input[x*4+0];
            tempValue = filterValue-pointer_input[x*4+1]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+1];
            pointer_ouput[x*4+1] = tempValue;
            pointer_ouput[x*4+2] = pointer_input[x*4+2];
            pointer_ouput[x*4+3] = pointer_input[x*4+3];

        }
    }
}
void antiBlue(Mat &matInput, Mat &matResult, jlong filterValue){
    int height = matInput.rows;
    int width = matInput.cols;
    long tempValue;

    for(int y =0; y<height; y++){
        uchar* pointer_input = matInput.ptr<uchar>(y);
        uchar* pointer_ouput = matResult.ptr<uchar>(y);
        for(int x=0;x<width;x++){
            pointer_ouput[x*4+0] = pointer_input[x*4+0];
            pointer_ouput[x*4+1] = pointer_input[x*4+1];

            tempValue = filterValue-pointer_input[x*4+2]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+2];
            pointer_ouput[x*4+2] = tempValue;
            pointer_ouput[x*4+3] = pointer_input[x*4+3];

        }
    }
}

void antiScreen(Mat &matInput, Mat &matResult, jlong filterValue){
    int height = matInput.rows;
    int width = matInput.cols;
    long tempValue;

    for(int y =0; y<height; y++){
        uchar* pointer_input = matInput.ptr<uchar>(y);
        uchar* pointer_ouput = matResult.ptr<uchar>(y);
        for(int x=0;x<width;x++){
            tempValue = filterValue-pointer_input[x*4+0]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+0];
            pointer_ouput[x*4+0] = tempValue;
            tempValue = filterValue-pointer_input[x*4+1]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+1];
            pointer_ouput[x*4+1] = tempValue;
            tempValue = filterValue-pointer_input[x*4+2]; //음수를 확인
            if(tempValue<0) tempValue=pointer_input[x*4+2];
            pointer_ouput[x*4+2] = tempValue;
            pointer_ouput[x*4+3] = pointer_input[x*4+3];

        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_locationlog_102_Activitys_MyMapLog_fragment_DetailImageFragment_detectEdgeJNI(
        JNIEnv *env, jobject instance, jlong inputImage, jlong outputImage, jint th1, jint th2) {

    // TODO

}