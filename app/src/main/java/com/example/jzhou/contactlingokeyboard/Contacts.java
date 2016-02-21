package com.example.jzhou.contactlingokeyboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Keyboard_Provider;

/**
 * Created by jzhou on 05/02/2016.
 */
public class Contacts extends Activity {

    public static IntentFilter filter;
    public double package_name=0.11;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.setSetting(this, Aware_Preferences.STATUS_KEYBOARD, true);
        Aware.startSensor(this, Aware_Preferences.STATUS_KEYBOARD);

        filter = new IntentFilter();
        filter.addAction(com.aware.Keyboard.ACTION_AWARE_KEYBOARD);
        registerReceiver(keyboardReceiver, filter);

    }



    datareceiver keyboardReceiver = new datareceiver();
    public class datareceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(com.aware.Keyboard.ACTION_AWARE_KEYBOARD)){
                Cursor keyboard = context.getContentResolver().query
                        (Keyboard_Provider.Keyboard_Data.CONTENT_URI, null, null, null, Keyboard_Provider.Keyboard_Data.TIMESTAMP + " DESC LIMIT 1");
                if (keyboard != null && keyboard.moveToFirst()){
                    package_name = keyboard.getDouble(keyboard.getColumnIndex(Keyboard_Provider.Keyboard_Data.PACKAGE_NAME));
                }
            }
        }
    }


    public double getpackageName(){
        Log.d("111", package_name+"nihao");
        return package_name;
    }

}
