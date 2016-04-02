package com.example.jzhou.contactlingokeyboard;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodSubtype;

public class MyKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {


    private  KeyboardView kv;
    private Keyboard keyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard symbolsShift;
    private SharedPreferences keyboardtypePreference;
    private boolean caps = false;
    public Contacts contacts;
    public  double packagename;
    public  static String NUMBER;
    public  static String EMAIL;
    public  static String WHATSAPPNUMBER;
    public  static String PACKAGE;
    public static  String  SECONDLANG;
    private Keyboard keyboardEng;
    private Keyboard keyboardFin;
    private Keyboard keyboardSwe;
    private Keyboard keyboardNor;
    private Keyboard keyboardDan;


    @Override
    public View onCreateInputView() {
        contacts = new Contacts();
        kv = (KeyboardView)  getLayoutInflater().inflate(R.layout.keyboard, null);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {

        super.onStartInputView(info, restarting);
        String language = "DEFAULT";
        keyboardtypePreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String keyboardPreference = keyboardtypePreference.getString("keyboardType", "2");
        //System.out.println("CHECK SECOND" + PACKAGE);
        switch (PACKAGE){
            case "SMS":
                language = this.getLanguage();
                break;
            case "EMAIL":
                language = this.getEMAILLanguage();
                break;
            case "WHATSAPP":
                language = this.getWhatappLanguage();
                break;
        }
        switch (language){
            case "ENGLISH":
                keyboardPreference = "3";
                break;
            case "FINNISH":
                keyboardPreference = "2";
                break;
            case "SWEDISH":
                keyboardPreference = "4";
                break;
            case "DANISH":
                keyboardPreference = "1";
                break;
            case "NORWEGIAN":
                keyboardPreference = "5";
                break;
            default:
                keyboardPreference = "1";
                break;
        }

        switch (keyboardPreference) {
            case "1":
                keyboard = new Keyboard(this, R.xml.danish);
                symbolsKeyboard = new Keyboard(this, R.xml.symbols);
                symbolsShift = new Keyboard(this, R.xml.symbols_shift);
                break;
            case "2":
                keyboard = new Keyboard(this, R.xml.finnish);
                symbolsKeyboard = new Keyboard(this, R.xml.symbols);
                symbolsShift = new Keyboard(this, R.xml.symbols_shift);
                break;
            case "3":
                keyboard = new Keyboard(this, R.xml.english);
                symbolsKeyboard = new Keyboard(this, R.xml.symbols);
                symbolsShift = new Keyboard(this, R.xml.symbols_shift);
                break;
            case "4":
                keyboard = new Keyboard(this, R.xml.swedish);
                symbolsKeyboard = new Keyboard(this, R.xml.symbols);
                symbolsShift = new Keyboard(this, R.xml.symbols_shift);
                break;
            case "5":
                keyboard = new Keyboard(this, R.xml.norwegian);
                symbolsKeyboard = new Keyboard(this, R.xml.symbols);
                symbolsShift = new Keyboard(this, R.xml.symbols_shift);
                break;
        }
        keyboardEng = new Keyboard(this, R.xml.english);
        keyboardFin = new Keyboard(this, R.xml.finnish);
        keyboardDan = new Keyboard(this, R.xml.danish);
        keyboardNor = new Keyboard(this, R.xml.norwegian);
        keyboardSwe = new Keyboard(this, R.xml.swedish);
        kv.setKeyboard(keyboard);
        kv.setPreviewEnabled(false);
        kv.closing();
        NUMBER =null;
        packagename = contacts.getpackageName();
        Log.d("111", packagename + "packagename");

    }

    public String getWhatappLanguage(){
        if ( WHATSAPPNUMBER != null)
        {
            System.out.println("CONTACT LINGO 3 " + WHATSAPPNUMBER);
            String[] projection = new String[]{ Provider_Whatsapp.BasicData.CONTACT, Provider_Whatsapp.BasicData.FIRST_LANG,Provider_Whatsapp.BasicData.SECOND_LANG};
            Cursor cursor = getContentResolver().query(Provider_Whatsapp.BasicData.CONTENT_URI, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String contact = cursor.getString(0);
                    if (WHATSAPPNUMBER.equals(contact))
                    {
                        SECONDLANG = cursor.getString(2);
                        return cursor.getString(1);
                    }
                } while (cursor.moveToNext());
            }
        }
        return "DEFAULT";
    }

    public String getEMAILLanguage(){
        if ( EMAIL != null)
        {
            System.out.println("CONTACT LINGO 3 " + EMAIL);
            String[] projection = new String[]{ Provider_Email.BasicData.EMAIL, Provider_Email.BasicData.FIRST_LANG, Provider_Email.BasicData.SECOND_LANG};
            Cursor cursor = getContentResolver().query(Provider_Email.BasicData.CONTENT_URI, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String contact = cursor.getString(0);
                    if (EMAIL.equals(contact))
                    {
                        SECONDLANG = cursor.getString(2);
                        return cursor.getString(1);
                    }
                } while (cursor.moveToNext());
            }
        }
        return "DEFAULT";
    }

    public String getLanguage (){
        if ( NUMBER != null)
        {
        System.out.println("CONTACT LINGO 3 " + NUMBER);
        String[] projection = new String[]{ Provider.BasicData.CONTACT, Provider.BasicData.FIRST_LANG, Provider.BasicData.SECOND_LANG};
        Cursor cursor = getContentResolver().query(Provider.BasicData.CONTENT_URI, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String contact = cursor.getString(0);
                if (NUMBER.equals(contact))
                {
                    SECONDLANG = cursor.getString(2);
                    return cursor.getString(1);
                }
            } while (cursor.moveToNext());
        }
        }
        return "DEFAULT";
    }

    @Override
    protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype newSubtype) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype);

    }

    private  void playClick(int keyCode){

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode){
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
 	    case 900:
                if(kv.getKeyboard()== keyboard) {
                    kv.setKeyboard(symbolsKeyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                else{
                    kv.setKeyboard(keyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                break;
            case 901:
                if(kv.getKeyboard()== keyboard) {
                    kv.setKeyboard(symbolsKeyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                else{
                    kv.setKeyboard(keyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                break;
            case 902:
                if(kv.getKeyboard()== keyboard) {
                    kv.setKeyboard(symbolsKeyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                else{
                    kv.setKeyboard(keyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                break;
            case 905:
                if(kv.getKeyboard()== symbolsKeyboard) {
                    kv.setKeyboard(symbolsShift);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                else{
                    kv.setKeyboard(symbolsKeyboard);
                    kv.setPreviewEnabled(false);
                    kv.closing();
                }
                break;
            case 800:
                changeview();
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                }
                else
                    ic.commitText(String.valueOf(code), 1);
        }
    }


    public void changeview(){
        switch (SECONDLANG){
            case "ENGLISH":
                kv.setKeyboard(keyboardEng);
                kv.setPreviewEnabled(false);
                kv.closing();
                break;
            case "SWEDISH":
                kv.setKeyboard(keyboardSwe);
                kv.setPreviewEnabled(false);
                kv.closing();
                break;
            case "FINNISH":
                kv.setKeyboard(keyboardFin);
                kv.setPreviewEnabled(false);
                kv.closing();
                break;
            case "DANISH":
                kv.setKeyboard(keyboardDan);
                kv.setPreviewEnabled(false);
                kv.closing();
                break;
            case "NORWEGIAN":
                kv.setKeyboard(keyboardNor);
                kv.setPreviewEnabled(false);
                kv.closing();
                break;
        }

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
