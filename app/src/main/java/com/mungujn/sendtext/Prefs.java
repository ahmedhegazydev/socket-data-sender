package com.mungujn.sendtext;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.SpinnerAdapter;

/**
 * Created by Prime on 11/01/2015.
 */
public class Prefs extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // addContentView;

        addPreferencesFromResource(R.xml.prefs);
    }
}
