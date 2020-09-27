package com.hust.hddv.vietnamesettsengine;

import android.speech.tts.SynthesisCallback;
import android.speech.tts.TextToSpeech;

public class MyCallback {
    private SynthesisCallback callback;
    private boolean mStopRequested = false;

    public MyCallback() {
    }

    public MyCallback(SynthesisCallback callback) {
        this.callback = callback;

    }

    public void setCallback(SynthesisCallback callback) {
        this.callback = callback;
        mStopRequested = false;
    }

    public void setStop(boolean state) {
        this.mStopRequested = state;
    }

    //return 0 to stop, 1 to continue
    public int audioAvailable(byte[] buff, int off, int len) {
       // Log.e("HDDV", "native code C/C++ call audioAcailable");
        if (mStopRequested) {
            Util.stopAnyWork();
            return 0;
        } else {
            if (callback.audioAvailable(buff, off, len) == TextToSpeech.ERROR) {
                mStopRequested = true;
                Util.stopAnyWork();
                return 0;
            }
            return 1;
        }
    }
}
