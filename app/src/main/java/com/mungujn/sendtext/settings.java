package com.mungujn.sendtext;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Nickson on 11/01/2015.
 */
public class settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        ActionBar actionBar = this.getSupportActionBar();
        CharSequence csq= "Settings";
        actionBar.setTitle(csq);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                {getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new Prefs())
                        .commit();}
            }
        });

    }
}
