package com.example.jzhou.contactlingokeyboard;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ahsanmanzoor on 13/02/2016.
 */
public class ContactAccess extends AccessibilityService {
    public String NUMBER;
    private SharedPreferences languagePreference;
    private String LASTMESSAGE;
    public String PACKAGENAME;


    public ContactAccess() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        languagePreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Log.d("CONTACT_LINGO", event.toString());
        if (event.getPackageName() != null) {
            PACKAGENAME = event.getPackageName().toString();
        }
        List<CharSequence> text = event.getText();
        if (PACKAGENAME != null) {
            switch (PACKAGENAME) {
                case "com.android.mms":
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                        newMessageFunction(text);
                    } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                        messageFunction(text);
                    }
                    break;
                case "com.google.android.gm":
                    System.out.println("EMAIL");
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                        if ((text.get(0).toString()).contains("<")) {
                            int start = (text.get(0).toString()).indexOf("<") + 1;
                            int end = (text.get(0).toString()).indexOf(">");
                            String email = (text.get(0).toString()).substring(start, end);
                            Log.d("CHECK", email);
                            addEmail(email);
                        }
                        //System.out.println("DEKHO" +(text.get(0).toString()).indexOf("<"));
                    }
                    break;
                case "com.whatsapp":
                    System.out.println("WHATSAPP");
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED){
                        String NAME = null;
                        if (text.size() != 0){
                            NAME = text.get(0).toString();
                            if(text.size() >= 3) {
                                LASTMESSAGE = text.get(3).toString();
                            }
                            Log.d("CONTACT_LINGO :", NAME);
                        }
                        if (NAME == null){
                        }
                        else {
                            NUMBER = getContactData(NAME);
                            if (NUMBER != null) {
                                System.out.println("INSIDE WHATSAPP" + NUMBER);
                                //lingoFeature();
                            } else System.out.println("NO SAVED CONTACT");
                        }
                    }
                    break;
            }
        }
    }
    // GET CONTACT INFORMATION FROM NEW CREATE MESSAGE
    public void newMessageFunction(List<CharSequence> text){
            if((text.toString().charAt(1) == '+' || text.toString().charAt(1) == '0')  && text.toString().length() >= 7) {
                int length = text.toString().length();
                NUMBER = text.toString().substring(1, length - 1);
                Log.d("CONTACT_LINGO 1", NUMBER);
                lingoFeature();
            }
    }
    // GET CONTACT INFORMATION FROM ALRAEDY MESSAGE LIST
    public void messageFunction(List<CharSequence> allText) {
        String NAME = null;
        if (allText.size() != 0){
            NAME = allText.get(0).toString();
            if(allText.size() >= 3) {
                LASTMESSAGE = allText.get(3).toString();
            }
            Log.d("CONTACT_LINGO :", NAME);
        }
        if (NAME == null){
        } else {
            NUMBER = getContactData(NAME);
            if (NUMBER != null) {
                lingoFeature();
            } else System.out.println("NO SAVED CONTACT");
        }

    }

    // SEARCHES FOR NUMBER FROM NAME
    private String getContactData(String NAME) {
        String value = null;
        Cursor cursor = getContentResolver()
                .query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
        cursor.moveToFirst();
        ArrayList contactsNumber = new ArrayList<String>();
        ArrayList contactsName = new ArrayList<String>();
        int position =0;
        while (cursor.moveToNext()) {
            contactsName.add(cursor.getString(1));
            contactsNumber.add(cursor.getString(0));
        }

        for(position=0; position < contactsName.size(); position++){
            if(contactsName.get(position).equals(NAME)){
                System.out.println(contactsNumber.get(position));
                value =  String.valueOf(contactsNumber.get(position));
                break;
            }
        }
        return value;
    }

    // SAVES THE NUMBER DATA IN THE DATABASE
    public void addData(String NUMBER) {
        ContentValues new_data = new ContentValues();
        new_data.put(Provider.BasicData.CONTACT, NUMBER);
        new_data.put(Provider.BasicData.FIRST_LANG, "ENGLISH");
        new_data.put(Provider.BasicData.SECOND_LANG, "FINNISH");
        getContentResolver().insert(Provider.BasicData.CONTENT_URI, new_data);
    }

    // CHECKS FOR CONTACT SAVED IN THE DATABASE
    public boolean ifSaved(String NUMBER){
        String[] projection = new String[]{ Provider.BasicData.CONTACT};
        Cursor cursor = getContentResolver().query(Provider.BasicData.CONTENT_URI, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String contact = cursor.getString(0);
                if (NUMBER.equals(contact))
                {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    // CHECK FOR FEATURE SELECTED & SAVES NUMBER IN THE DATABASE IF NOT SAVED
    public void lingoFeature(){
        if ( ifSaved(NUMBER))
        {
            MyKeyboard.NUMBER = NUMBER;
            //Log.d("CONTACT_LINGO 1", NUMBER);
            System.out.println("ALREADY EXIST");
        }
        else {
            addData(NUMBER);
            languagePreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if ((languagePreference.getString("choose_feature", "1")).equals("2")){
                System.out.println("predictive" + LASTMESSAGE);
                new langdetect().execute(LASTMESSAGE);
            }
            else {
                Intent intent = new Intent(getApplicationContext(),PopUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("number", NUMBER);
                startActivity(intent);
                //System.out.println("manual");
            }
        }

    }

    public void update(String firstLang, String secondLang){
            ContentValues new_data = new ContentValues();
            new_data.put(Provider.BasicData.FIRST_LANG,firstLang );
            new_data.put(Provider.BasicData.SECOND_LANG, secondLang);
            getContentResolver().update(Provider.BasicData.CONTENT_URI, new_data, Provider.BasicData.CONTACT + "=?", new String[]{NUMBER});
    }

    String langResult;

    private class langdetect extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            DetectLanguage.apiKey = "b639e403c5d8d00541050d8b6daad6ab";
            try {
                List<Result> results = DetectLanguage.detect(LASTMESSAGE);
                Result result = results.get(0);
                langResult = result.language.toString();
                System.out.println("Language: " + result.language);
                System.out.println("Is reliable: " + result.isReliable);
                System.out.println("Confidence: " + result.confidence);
            } catch (APIError e) {
                System.out.println("Error" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            decodeLang();
        }
    }

    public void decodeLang(){
        String firstLang = null , secondLang =  null;
        switch (langResult){
            case "en":
                firstLang = "ENGLISH";
                secondLang = "FINNISH";
                break;
            case "fi":
                firstLang = "FINNISH";
                secondLang = "ENGLISH";
                break;
            case "ar":
                firstLang = "ARABIC";
                secondLang = "ENGLISH";
        }
        update(firstLang, secondLang);
        Toast.makeText(this, firstLang + " LANGUAGE ", Toast.LENGTH_LONG).show();
    }

    // SAVES THE EMAIL DATA IN THE DATABASE
    public void addEmail(String EMAIL) {
        ContentValues new_data = new ContentValues();
        new_data.put(Provider_Email.BasicData.EMAIL, EMAIL);
        new_data.put(Provider_Email.BasicData.FIRST_LANG, "ENGLISH");
        new_data.put(Provider_Email.BasicData.SECOND_LANG, "FINNISH");
        getContentResolver().insert(Provider_Email.BasicData.CONTENT_URI, new_data);
    }

    @Override
    public void onInterrupt() {

    }
}
