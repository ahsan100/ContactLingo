package com.example.jzhou.contactlingokeyboard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by ahsanmanzoor on 19/02/2016.
 */
public class PopUp extends Activity {
    String[] items = new String[]{"ENGLISH", "FINNISH", "SWEDISH"};
    Spinner dropdown;
    Spinner dropdown2;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        intent = getIntent();
        getWindow().setLayout((int)(width * .8),(int)(height *.6));
        dropdown = (Spinner) findViewById(R.id.spinner3);
        dropdown2 = (Spinner) findViewById(R.id.spinner4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown2.setAdapter(adapter);
    }

    public void onClick(View view){
        String NUMBER = intent.getStringExtra("number");
        ContentValues new_data = new ContentValues();
        new_data.put(Provider.BasicData.FIRST_LANG, dropdown.getSelectedItem().toString());
        new_data.put(Provider.BasicData.SECOND_LANG, dropdown2.getSelectedItem().toString());
        getContentResolver().update(Provider.BasicData.CONTENT_URI, new_data, Provider.BasicData.CONTACT + "=?", new String[]{NUMBER});
        Toast.makeText(this, "LANGUAGE SAVED.", Toast.LENGTH_LONG).show();
        this.finish();

    }

}
