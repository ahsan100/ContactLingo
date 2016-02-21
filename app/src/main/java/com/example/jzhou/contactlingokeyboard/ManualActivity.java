package com.example.jzhou.contactlingokeyboard;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ManualActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    String[] items = new String[]{"ENGLISH", "FINNISH", "SWEDISH"};
    Spinner dropdown;
    Spinner dropdown2;
    String NUMBER = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dropdown = (Spinner) findViewById(R.id.spinner);
        dropdown2 = (Spinner) findViewById(R.id.spinner2);
    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown2.setAdapter(adapter);
        String DEBUG_TAG = "NOTHING ";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String phone = "";
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id},
                                null);
                        int phoneId = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(phoneId);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get phone data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        TextView nameEntry = (TextView) findViewById(R.id.textView4);
                        TextView phoneEntry = (TextView) findViewById(R.id.textView);
                        nameEntry.setText(getName(phone));
                        phoneEntry.setText(phone);
                        if (phone.length() == 0) {
                            Toast.makeText(this, "No phone found for contact.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            NUMBER = phone;
                            if (setLanguage(phone)){
                                addData(NUMBER);
                            }
                        }
                    }
                    break;
            }
        } else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    public String getName(String number) {
        String name = null;
        ContentResolver cr = this.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        if (name == null) {
            return null;
        } else {
            return name;
        }
    }

    public boolean setLanguage(String phone) {
        if (phone != null) {
            String[] projection = new String[]{Provider.BasicData.CONTACT, Provider.BasicData.FIRST_LANG, Provider.BasicData.SECOND_LANG};
            Cursor cursor = getContentResolver().query(Provider.BasicData.CONTENT_URI, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String contact = cursor.getString(0);
                    if (phone.equals(contact)) {
                        dropdown.setSelection(setDrop(cursor.getString(1)));
                        dropdown2.setSelection(setDrop(cursor.getString(2)));
                        return false;
                    }
                } while (cursor.moveToNext());
            }
        }
        return true;
    }

    public int setDrop(String Lang) {
        switch (Lang) {
            case "ENGLISH":
                return 0;
            case "FINNISH":
                return 1;
            case "SWEDISH":
                return 2;
            default:
                return 3;
        }
    }
    public void update(View view){
        if(NUMBER == null) {
            Toast.makeText(this, "NO CONTACT SELECTED.", Toast.LENGTH_LONG).show();
        }
        else {
            ContentValues new_data = new ContentValues();
            new_data.put(Provider.BasicData.FIRST_LANG, dropdown.getSelectedItem().toString());
            new_data.put(Provider.BasicData.SECOND_LANG, dropdown2.getSelectedItem().toString());
            getContentResolver().update(Provider.BasicData.CONTENT_URI, new_data, Provider.BasicData.CONTACT + "=?", new String[]{NUMBER});
        }
    }

    public void addData(String NUMBER) {
        ContentValues new_data = new ContentValues();
        new_data.put(Provider.BasicData.CONTACT, NUMBER);
        new_data.put(Provider.BasicData.FIRST_LANG, "ENGLISH");
        new_data.put(Provider.BasicData.SECOND_LANG, "FINNISH");
        getContentResolver().insert(Provider.BasicData.CONTENT_URI, new_data);
    }
}
