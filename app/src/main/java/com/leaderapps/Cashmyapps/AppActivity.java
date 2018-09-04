package com.leaderapps.Cashmyapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

import com.leaderapps.Cashmyapps.app.App;
import com.leaderapps.Cashmyapps.common.ActivityBase;
import com.leaderapps.Cashmyapps.util.CustomRequest;


public class AppActivity extends ActivityBase {

    Button loginBtn, signupBtn;
    private PrefManager prefManager;
    RelativeLayout loadingScreen;
    LinearLayout contentScreen;
    private Tracker tracker;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.

    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(Config.analytics_property_id);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_app);

        contentScreen = (LinearLayout) findViewById(R.id.contentScreen);
        loadingScreen = (RelativeLayout) findViewById(R.id.loadingScreen);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {



            Intent skip = new Intent(this, IntroActivity.class);
            startActivity(skip);
            finish();
        }

        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // do nothing here
/*
        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {
                String autoDeeplinkedValue = Branch.getInstance().getLatestReferringParams().getString("product_picture");
                //             launch_mode_txt.setText("Launched by Branch on auto deep linking!" + "\n\n" + autoDeeplinkedValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            //          launch_mode_txt.setText("Launched by normal application flow");
        }*/
    }
    @Override
    protected void onStart() {

        super.onStart();







        // ---------------------- ANALYTICS ---------------------

        GoogleAnalytics.getInstance(this).newTracker(Config.analytics_property_id);
        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = getTracker(TrackerName.APP_TRACKER);
        tracker.setScreenName("SplashActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        if (!App.getInstance().isConnected()) {

            showLoadingScreen();

            ActivityCompat.finishAffinity(AppActivity.this);
            Intent i = new Intent(getBaseContext(), ErrorActivity.class);
            startActivity(i);
        }

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            showLoadingScreen();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_AUTHORIZE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (App.getInstance().authorize(response)) {

                                if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {

                                    ActivityCompat.finishAffinity(AppActivity.this);

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    Globals g = Globals.getInstance();
                                    try {
                                        JSONArray js = response.getJSONArray("account");
                            //            String b = js.getString("ip_addr");
                                        String a = js.getJSONObject(0).getString("urlBranch");
                                        g.setBranchURL(a);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {

                                    showContentScreen();
                                    App.getInstance().logout();
                                }

                            } else {

                                showContentScreen();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    showContentScreen();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);

        } else {
            showContentScreen();
        }
    }



    public void showContentScreen() {

        loadingScreen.setVisibility(View.GONE);

        contentScreen.setVisibility(View.VISIBLE);
    }

    public void showLoadingScreen() {

        contentScreen.setVisibility(View.GONE);

        loadingScreen.setVisibility(View.VISIBLE);
    }
}
