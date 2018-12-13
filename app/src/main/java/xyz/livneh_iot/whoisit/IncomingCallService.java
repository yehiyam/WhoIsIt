package xyz.livneh_iot.whoisit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class IncomingCallService extends Service {
    private static final String TAG = "WhoIsIt";

    public final class ServiceHandler extends Handler {

        Speaker m_speakerInstance;

        private int previousVolume=-1;
        private AudioManager audio=null;
        int id=-1;
        public ServiceHandler(Looper looper, Speaker speaker) {
            super(looper);
            m_speakerInstance=speaker;

        }
        @Override
        public void handleMessage(Message msg) {
            id = msg.arg1;
            Bundle bundle = msg.getData();
            if (bundle != null)
            {
//	  			if (previousVolume!=-1)
//	  			{
//	  				Log.v(TAG,"in progress. Cancel");
//	  				return;
//	  			}
                Log.v(TAG,"handleMessage: "+ bundle.getString("Name"));
                if (m_speakerInstance!=null)
                {

                    audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    if (audio.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                        stopSelf(msg.arg1);
                        return;
                    }

                    Log.v("Start",Integer.toString(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));
                    if (previousVolume==-1)
                    {
                        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamVolume(AudioManager.STREAM_RING) * 2, 0);
                        Log.v("After",Integer.toString(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));
                    }
                    m_speakerInstance.speak(bundle.getString("Name"),this);
                    //audio.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);

                    return;

                }
                else
                {
                    Log.v(TAG,"mTts is null");
                }
            }
            stopSelf(msg.arg1);
        }
        public void stopService() {
            Log.v("Prev",Integer.toString(previousVolume));
            if (audio !=null && previousVolume!=-1){
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                Log.v("Prev",Integer.toString(previousVolume));
                Log.v("Actual",Integer.toString(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));

            }
            previousVolume=-1;
            if (id!=-1)
                stopSelf(id);
            id=-1;
        }

    }

    Speaker m_speakerInstance;
    private int previousVolume=-1;
    private AudioManager audio=null;
    int id=-1;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;


    private Speaker m_speaker;



    public IncomingCallService() {
        //	super("IncomingCallService");
        // TODO Auto-generated constructor stub
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent==null)
            return Service.START_STICKY;
        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return Service.START_STICKY;


        if (mServiceHandler==null)
            return Service.START_STICKY;

        Message msg = mServiceHandler.obtainMessage();

        msg.arg1 = startId;
        msg.setData(bundle);
        mServiceHandler.sendMessage(msg);


        return Service.START_STICKY;
    }
    @Override
    public void onCreate()
    {

        Log.v(TAG, "onCreate");
        if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            stopSelf();
            return;
        }
        m_speaker = new Speaker(this);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper,m_speaker);

        super.onCreate();
    }
    @Override
    public void onDestroy() {
        if (m_speaker!=null)
        {
            m_speaker.shutdown();
            //audio.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
            m_speaker=null;
        }
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
