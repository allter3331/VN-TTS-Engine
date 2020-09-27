package com.hust.hddv.vietnamesettsengine;


import android.media.AudioFormat;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.text.TextUtils;
import android.util.Log;

import com.hust.hddv.vietnamesettsengine.texttospeechcore.GetSpeechService;

import java.io.IOException;
import java.io.InputStream;

public class VietnameseTTSService extends TextToSpeechService {
    private static final String TAG = "HDDVVnTTSService";
    /*
     * This is the sampling rate of our output audio. This engine outputs
     * audio at 16khz 16bits per sample PCM audio.
     */
    private static int SAMPLING_RATE_HZ = 16000;

    private volatile String[] mCurrentLanguage = null;
    private volatile boolean mStopRequested = false;

    private MyCallback jniCallback = new MyCallback();
    private boolean mIsInitialized = false;

    @Override
    public void onCreate() {
        super.onCreate();
        onLoadLanguage("vie", "VN", "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroy Engine");
    }

    @Override
    protected String[] onGetLanguage() {
        // Note that mCurrentLanguage is volatile because this can be called from
        // multiple threads.
        return mCurrentLanguage;
    }


    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {
        if ("vie".equals(lang)) {
            if ("VN".equals(country)) {
                // If the engine supported a specific variant, we would have
                // something like.
                //
                // if ("android".equals(variant)) {
                //     return TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;
                // }
                return TextToSpeech.LANG_COUNTRY_AVAILABLE;
            }
            // We support the language, but not the country.
            return TextToSpeech.LANG_AVAILABLE;
        }
        return TextToSpeech.LANG_NOT_SUPPORTED;
    }

    /*
     * Note that this method is synchronized, as is onSynthesizeText because
     * onLoadLanguage can be called from multiple threads (while onSynthesizeText
     * is always called from a single thread only).
     */
    @Override
    protected synchronized int onLoadLanguage(String lang, String country, String variant) {
        final int isLanguageAvailable = onIsLanguageAvailable(lang, country, variant);
        if (isLanguageAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
            return isLanguageAvailable;
        }

        String loadCountry = country;

        if (isLanguageAvailable == TextToSpeech.LANG_AVAILABLE) {
            loadCountry = "VN";
        }

        // If we've already loaded the requested language, we can return early.
        if (mCurrentLanguage != null) {
            if (mCurrentLanguage[0].equals(lang) && mCurrentLanguage[1].equals(country)) {
                return isLanguageAvailable;
            }
        }

        if (!mIsInitialized) {
            mIsInitialized = Util.initTTS();
            Log.i(TAG, "InitHTS ok : " + mIsInitialized);
        }

        mCurrentLanguage = new String[]{lang, loadCountry, ""};
        return isLanguageAvailable;

    }


    @Override
    protected synchronized void onSynthesizeText(SynthesisRequest request,
                                                 SynthesisCallback callback) {
        // Note that we call onLoadLanguage here since there is no guarantee
        // that there would have been a prior call to this function.
        int load = onLoadLanguage(request.getLanguage(), request.getCountry(),
                request.getVariant());
        // We might get requests for a language we don't support - in which case
        // we error out early before wasting too much time.
        if (load == TextToSpeech.LANG_NOT_SUPPORTED) {
            callback.error();
            return;
        }


        final String text = request.getText().toLowerCase();


        callback.start(SAMPLING_RATE_HZ,
                AudioFormat.ENCODING_PCM_16BIT, 1 /* Number of channels. */);

        if (TextUtils.isEmpty(text)) {
            Log.i(TAG, "Break! Text is empty");
            callback.done();
            return;
        }

        int pitch = request.getPitch();
        int rate = request.getSpeechRate();


        Log.e(TAG, "SpeechRate : " + request.getSpeechRate() +"");


        int maxBufferSize = callback.getMaxBufferSize();
        if (maxBufferSize > 16000) {
            maxBufferSize = 16000;
        }
        jniCallback.setCallback(callback);
        jniCallback.setStop(false);

        float speechRateRequest = valudateSpeechRate(request.getSpeechRate());

        String textRequest = text.replace("\n", " ");

        Log.e(TAG, "CharSequenceText : " + request.getCharSequenceText());
        Log.e(TAG, "text validate : " + textRequest);

        final String datajson = "{\"text\":\""+textRequest+"\"," +
                "\"voice\":\"doanngocle\"," +
                "\"id\":\"3\"," +
                "\"without_filter\":false," +
                "\"speed\":"+speechRateRequest+"," +
                "\"tts_return_option\":2}";

        try {
            InputStream inputStream = GetSpeechService.getSpeech("https://viettelgroup.ai/voice/api/tts/v1/rest/syn", datajson);
            byte[] bytes = GetSpeechService.inputStreamToByteArray(inputStream);
            Util.synthTextTTS(text, text.length(), maxBufferSize, rate, pitch, jniCallback, bytes, bytes.length);

        } catch (IOException e) {
            callback.error();
            e.printStackTrace();
        }

//        int offset = 0;
//        while (offset < bytes.length) {
//            int bytesToWrite = Math.min(maxBufferSize, bytes.length - offset);
//            callback.audioAvailable(bytes, offset, bytesToWrite);
//            offset += bytesToWrite;
//        }

        callback.done();
    }

    private float valudateSpeechRate(int speechRate){
        if(speechRate > 130) return 1.3f;
        if(speechRate < 70) return 0.7f;
        return (float) (speechRate*1.0 / 100);
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        mStopRequested = true;
        jniCallback.setStop(mStopRequested);
        Util.stopAnyWork();
    }
}
