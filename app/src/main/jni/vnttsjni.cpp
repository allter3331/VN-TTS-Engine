
#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <android/asset_manager.h>

extern "C" {
    JNIEXPORT jboolean JNICALL
     Java_com_hust_hddv_vietnamesettsengine_Util_initTTS(JNIEnv* env, jobject obj){
         __android_log_print(ANDROID_LOG_DEBUG, "HDDV", "Init VN TTS in native\n");
         return JNI_TRUE;
    }

    JNIEXPORT void JNICALL
     Java_com_hust_hddv_vietnamesettsengine_Util_synthTextTTS(JNIEnv* env, jobject thizObj,
                jstring text, jint textLength, jint maxBufferSize, jint rate, jint pitch, jobject myCallback, jbyteArray bytes, jint byteLength){

            __android_log_print(ANDROID_LOG_DEBUG, "HDDV", "call synthTextTTS\n");

            if(myCallback == NULL) return;
            jclass clsMyCallBack = env->GetObjectClass(myCallback);
            if (NULL == clsMyCallBack) return;
            jmethodID methodJNIStartCallBack = env->GetMethodID(clsMyCallBack, "audioAvailable", "([BII)I");
            if (NULL == methodJNIStartCallBack) {
                __android_log_print(ANDROID_LOG_DEBUG, "HDDV", "NULL == methodJNIStartCallBack\n");
                return;
            }

            jboolean isCopy;
            jbyte* b = env->GetByteArrayElements(bytes, &isCopy);
            char* tempChar = (char*) b;

            jbyteArray array = env->NewByteArray (byteLength);
            env->SetByteArrayRegion (array, 0, byteLength, reinterpret_cast<jbyte*>(tempChar));

            jint offset = 70;

            while (offset < byteLength) {
                jint bytesToWrite = maxBufferSize < (byteLength - offset) ? maxBufferSize : (byteLength - offset);

                env ->CallIntMethod(myCallback, methodJNIStartCallBack, array, offset, bytesToWrite);

                offset += bytesToWrite;
            }


            //jbyteArray array = env->NewByteArray (size);
            //env->SetByteArrayRegion (array, 0, size, reinterpret_cast<jbyte*>(buffer));
            // get mảng byte từ thư viện

            //env ->CallIntMethod(myCallback, methodJNIStartCallBack, bytes, 0, maxBufferSize);
            env->ReleaseByteArrayElements(bytes, b, 0);

    }

    JNIEXPORT void JNICALL
     Java_com_hust_hddv_vietnamesettsengine_Util_shutDownTTS(JNIEnv* env, jobject obj){
         __android_log_print(ANDROID_LOG_DEBUG, "HDDV", "ShutDown VN TTS in native\n");
    }

    JNIEXPORT void JNICALL
      Java_com_hust_hddv_vietnamesettsengine_Util_stopAnyWork(JNIEnv* env, jobject obj){
          __android_log_print(ANDROID_LOG_DEBUG, "HDDV", "Stop VN TTS in native\n");
    }
}
