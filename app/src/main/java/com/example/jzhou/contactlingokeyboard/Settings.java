package com.example.jzhou.contactlingokeyboard;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;


public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference myPrefs;
    private Preference mLetterCasePreference;
    private Preference mSeekBarPrefrence;
    private SharedPreferences mPreferences;
    private Preference mDefaultKeyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }

        mDefaultKeyboard = findPreference("choose_keyboard");
        //myPrefs = findPreference("choose_feature");
        mDefaultKeyboard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
                return false;
            }
        });
       /* myPrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch (newValue.toString()){
                    case "1":
                        Intent intent = new Intent(getBaseContext(), ManualActivity.class);
                        startActivity(intent);
                        break;
                    case "2":
                        break;
                }
                return false;
            }
        });*/
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if( !isAccessibilityEnabled(this) ) {
            Intent accessibilitySettings = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            accessibilitySettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accessibilitySettings);
        }
    }

    private static boolean isAccessibilityEnabled(Context c) {
        boolean enabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) c.getSystemService(ACCESSIBILITY_SERVICE);
        if( ! enabled ) {
            try {
                List<AccessibilityServiceInfo> enabledServices = AccessibilityManagerCompat.getEnabledAccessibilityServiceList(accessibilityManager, AccessibilityEventCompat.TYPES_ALL_MASK);
                if( ! enabledServices.isEmpty() ) {
                    for( AccessibilityServiceInfo service : enabledServices ) {
                        if( service.getId().contains(c.getPackageName()) ) {
                            enabled = true;
                            break;
                        }
                    }
                }
            } catch ( NoSuchMethodError e ) {}
        }
        if( ! enabled ) {
            try{
                List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
                if ( ! enabledServices.isEmpty()) {
                    for (AccessibilityServiceInfo service : enabledServices) {
                        if (service.getId().contains(c.getPackageName())) {
                            enabled = true;
                            break;
                        }
                    }
                }
            }catch (NoSuchMethodError e) {}
        }
        return enabled;
    }
}

