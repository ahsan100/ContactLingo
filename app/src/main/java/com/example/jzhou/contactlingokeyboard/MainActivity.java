package com.example.jzhou.contactlingokeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.aware.Aware;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onResume() {
        super.onResume();
        startActivity(new Intent(getApplicationContext(), Settings.class));
        finish();
    }
}
