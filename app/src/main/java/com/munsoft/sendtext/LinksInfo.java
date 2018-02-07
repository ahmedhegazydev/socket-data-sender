package com.munsoft.sendtext;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;

/**
 * Created by Prime on 14/01/2015.
 */
public class LinksInfo extends ActionBarActivity {
    TextView three,four;
    Button generate,rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linksinfo);
        three = (TextView)findViewById(R.id.editText3);
        four = (TextView)findViewById(R.id.editText10);
        rate = (Button)findViewById(R.id.rate);
        three.setMovementMethod(LinkMovementMethod.getInstance());
        four.setMovementMethod(LinkMovementMethod.getInstance());
        generate=(Button)findViewById(R.id.generateActivity);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(v.getContext(),SendAsset.class);
                startActivity(Intent);
            }
        });
        rate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMarket();
            }
        });
    }
    private void launchMarket() {
        //Uri uri2 = Uri.parse("https://play.google.com/store/apps/details?id=com.munsoft.simplevlcremote");
        Uri uri1 = Uri.parse("market://details?id=" + "com.munsoft.sendtext");
        //Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri1);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }
    public static class AdFragment extends Fragment {
        Context ctxt;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ctxt = activity.getApplication().getApplicationContext();
        }

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);


            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);
            {
                LocationManager locationManager = (LocationManager) ctxt.getSystemService(Context.LOCATION_SERVICE);

                String locationProvider = LocationManager.NETWORK_PROVIDER;
                Location lct = locationManager.getLastKnownLocation(locationProvider);
                AdRequest adRequest = new AdRequest.Builder().setLocation(lct).build();
                // Start loading the ad in the background.
                mAdView.loadAd(adRequest);
            }


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragmentad2, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();

            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }

}
