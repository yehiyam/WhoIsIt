package xyz.livneh_iot.whoisit;

import java.util.HashMap;

import xyz.livneh_iot.whoisit.IncomingCallService.ServiceHandler;

import android.content.Context;
import android.util.Log;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Speaker implements OnInitListener {
    private boolean ready;
    private final TextToSpeech talker;
    ServiceHandler m_handler;
    private final Object waitObject = new Object();

    public Speaker(final Context context) {
        talker = new TextToSpeech(context, this);
        talker.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String arg0) {
                // TODO Auto-generated method stub
                Log.v("Speaker", "onStart");
            }

            @Override
            public void onError(String arg0) {
                // TODO Auto-generated method stub
                Log.v("Speaker", "onError");
            }

            @Override
            public void onDone(String arg0) {
                Log.v("Speaker", "onDone");
                if (m_handler!=null)
                    m_handler.stopService();
                m_handler=null;
            }
        });
        Log.v("SMN", "tts created");
    }

    @Override
    public void onInit(final int status) {
        Log.v("SMN", "tts init");

        synchronized (waitObject) {
            ready = true;
            Log.e("SMN", "notify");

            waitObject.notify();
        }
    }

    public void shutdown() {
        if (ready) {
            talker.stop();
            talker.shutdown();

            ready = false;
        }
    }

    public void speak(final String text, ServiceHandler handler) {
        m_handler=handler;
        if (ready) {
            Log.v("SMN", "ready: "+text);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            talker.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        } else {
            do {
                synchronized (waitObject) {
                    try {
                        waitObject.wait(500);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (!ready);

            Log.e("SMN", "was not ready");

            talker.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}