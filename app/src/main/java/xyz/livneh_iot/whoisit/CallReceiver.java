package xyz.livneh_iot.whoisit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CallReceiver extends PhoneStateListener {
    private static final String TAG = "WhoIsIt";
    private Context m_context;
    public CallReceiver(Context context)
    {
        m_context=context;
    }
    public void onCallStateChanged(int state,String incomingNumber){
        switch(state){
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d(TAG, "IDLE");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d(TAG, "OFFHOOK");
                break;
            case TelephonyManager.CALL_STATE_RINGING:

                // define the columns I want the query to return
                String[] projection = new String[] {
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup._ID};
                // encode the phone number and build the filter URI
                Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));

                // query time
                Cursor cursor = m_context.getContentResolver().query(contactUri, projection, null, null, null);
                if (cursor.moveToFirst()) {

                    String name = null;
                    String contactId = null;
                    // Get values from contacts database:
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                    Log.d(TAG, "RINGING "+name);
                    Intent intent = new Intent(m_context, IncomingCallService.class);
                    intent.putExtra("Name", name);
                    m_context.startService(intent);
                }
                Log.d(TAG, "RINGING "+incomingNumber);
                break;
        }
    }
}
