package com.hust.hddv.vietnamesettsengine;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    static {
        System.loadLibrary("synthtsvn");
    }

    public static native boolean initTTS();

//    public static native void synthTextTTS(String text, int textLength, int maxBufferSize, int rate, int pitch, MyCallback jniCallback);
    public static native void synthTextTTS(String text, int textLength, int maxBufferSize, int rate, int pitch, MyCallback jniCallback, byte[] bytes, int byteLeng);

    public static native void shutDownTTS();

    public static native void stopAnyWork();

    public static String truncateIfNeeded(String text) {
        int maxTextSize = TextToSpeech.getMaxSpeechInputLength();
        if (text.length() > maxTextSize) {
            return text.substring(0, maxTextSize);
        }
        return text;
    }
}
