package com.leaderapps.Cashmyapps;

import java.io.OutputStream;
import java.util.Properties;
import java.util.Calendar;

import android.Manifest;
import android.net.Uri;
import android.util.Log;

import android.app.*;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import android.content.*;

import net.adxmi.android.AdManager;
import net.adxmi.android.os.EarnPointsOrderInfo;
import net.adxmi.android.os.EarnPointsOrderList;
import net.adxmi.android.os.OffersManager;
import net.adxmi.android.os.PointsChangeNotify;
import net.adxmi.android.os.PointsEarnNotify;
import net.adxmi.android.os.PointsManager;
import net.adxmi.android.video.VideoAdManager;
import net.adxmi.android.video.VideoAdRequestListener;
import net.adxmi.android.video.VideoRewardsListener;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.supersonic.adapters.supersonicads.SupersonicConfig;
import com.supersonic.mediationsdk.logger.SupersonicError;
import com.supersonic.mediationsdk.model.Placement;
import com.supersonic.mediationsdk.sdk.OfferwallListener;
import com.supersonic.mediationsdk.sdk.RewardedVideoListener;
import com.supersonic.mediationsdk.sdk.Supersonic;
import com.supersonic.mediationsdk.sdk.SupersonicFactory;
import com.supersonicads.sdk.agent.SupersonicAdsAdvertiserAgent;
import com.tjeannin.apprate.AppRate;
import com.onesignal.OneSignal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.*;
import java.text.SimpleDateFormat;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import com.leaderapps.Cashmyapps.app.App;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.*;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

//Import the Supersonic Class

class YourActivity extends Activity
{
    //Declare the Supersonic Mediation Agent
    private Supersonic mMediationAgent;
    //For example, add the following inside “onCreate()” method:
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


  /*      Branch.getAutoTestInstance(this);
        Branch branch = Branch.getTestInstance(this);

        branch.initSession(new Branch.BranchReferralInitListener()
                           {
                               @Override
                               public void onInitFinished(JSONObject referringParams, BranchError error) {
                                   if (error == null) {
                                       // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                                       // params will be empty if no data found
                                       // ... insert custom logic here ...
                                   } else {
                                       Log.i("MyApp", error.getMessage());
                                   }
                               }
                           }
                , this.getIntent().getData(), this);
        branch.getFirstReferringParams();*/
              //Get the mediation publisher instance
        mMediationAgent = SupersonicFactory.getInstance();
    }


}
public class MainActivity extends BaseActivity implements PointsChangeNotify,
        PointsEarnNotify, VideoRewardsListener, RewardedVideoListener, BaseSliderView.OnSliderClickListener, OfferwallListener {

    private final static int REQUEST_READ_PHONE_STATE = 1;
    private BranchUniversalObject branchUniversalObject;
    private LinkProperties linkProperties;
    public boolean doubleBackToExitPressedOnce = false;
    private Supersonic mSupersonicInstance;
    private final String TAG = "MainActivity";
    ListView listView;
    private PrefManager prefManager;
    public static final String PREFS_NAME = "MyApp_Settings";
    private TextView pointTextView;
    int totalPoints;
    Properties properties;
    String vc_name = "credits";
    private GoogleApiClient client;
    private String isClicked;
    private String branchClicked;

    private SliderLayout mDemoSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Branch.getAutoTestInstance(this);
        Globals g = Globals.getInstance();
        isClicked = g.getData1();
        String strTrue="true";

        if(isClicked==strTrue && g.getData3()==0){
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage(isClicked+branchClicked);
            dlgAlert.setTitle("App Title");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
 //           dlgAlert.create().show();
            g.setData3(1);
            BranchClickedPoint(30);
            BranchInvitePoint(25);
        }
        branchClicked = g.getData2();

        Log.e("tag", "init usedMemory: " + Debug.getNativeHeapAllocatedSize() / 1024L);
        setContentView(R.layout.activity_main);

        initView();

        invalidateOptionsMenu();

        // application rating
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(Config.rate_title)
                .setMessage(String.format(Config.rate_message, getString(R.string.app_name)))
                .setPositiveButton(Config.rate_yes, null)
                .setNegativeButton(Config.rate_never, null)
                .setNeutralButton(Config.rate_later, null);

        new AppRate(this)
                .setShowIfAppHasCrashed(false)
                .setMinDaysUntilPrompt(1)
                .setMinLaunchesUntilPrompt(2)
                .setCustomDialog(builder)
                .init();

        // OneSignal Debugging, used at the time of app creation in Onsesignal.com
        //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);

        // initializing OneSignal
        OneSignal.startInit(this).init();

        // you can use this to get the current points
        totalPoints = PointsManager.getInstance(this).queryPoints();

        //If you only use Video Ad,you need to save the points by yourself ,we provide an example for you
        //totalPoints = diyLoadVideoReward();
        pointTextView.setText(getResources().getString(R.string.text_current_points) + totalPoints);
        // (Optional) Close Adxmi log output. It is suggested that developers shouldn't close it during integration,
        // because developers can catch the real-time output information to locate the issue when issue occurs.
        // AdManager.getInstance(Context context).setEnableDebugLog(false);

        // Common Initialization, invoke it when application launches. Parameters: appId, appSecret.
        AdManager.getInstance(this).init(Config.AppId, Config.AppSecret);

        initOfferWall();

        initVideoAd();




        // String uniqueID = UUID.randomUUID().toString();
        prefManager = new PrefManager(this);
        if (prefManager.isUserId()) {

            UUID uid = UUID.fromString(Config.UserId);

            String id = uid.randomUUID().toString();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            // Writing data to SharedPreferences
            Editor editor = settings.edit();
            editor.putString("user_id", id);
            editor.apply();

            prefManager.setUserId(false);
        }
        // Reading from SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = settings.getString("user_id", Config.UserId);
        String appKey = Config.AppKey;

        // create the supersonic instance - this should be called when the activity starts
        mSupersonicInstance = SupersonicFactory.getInstance();

        // Supersonic Advertiser SDK call
        SupersonicAdsAdvertiserAgent.getInstance().reportAppStarted(this);
        // Be sure to set a listener to each product that is being initiated
        // set the Supersonic rewarded video listener
        mSupersonicInstance.setRewardedVideoListener(this);
        // init the supersonic rewarded video
        mSupersonicInstance.initRewardedVideo(this, appKey, userId);
        // set the Supersonic offerwall listener
        mSupersonicInstance.setOfferwallListener(this);
        // init the supersonic offerwall
        // set client side callbacks for the offerwall
        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
        mSupersonicInstance.initOfferwall(this, appKey, userId);

        //checking wheather the permission of READ_PHONE_STATE is granted
        checkReadPhoneStatePermission();

        fcm_id();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasGetReadPhoneStatePermission()) {
                requestReadPhoneStatePermission();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // do nothing here


    }

    private boolean hasGetReadPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadPhoneStatePermission() {
        //You can choose a more friendly notice text. And you can choose any view you like, such as dialog.
        Toast.makeText(this, "Only grant the permission, can you start the mission!", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
    }

    //Callback for requestPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case REQUEST_READ_PHONE_STATE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "The Permission has granted,you can get your mission now", Toast.LENGTH_SHORT).show();
                    } else {
                        //had not get the permission
                        Toast.makeText(this, "Not get the permission,so you cannot get your mission", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enabling Double Backpress..
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);

        //  super.onBackPressed();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        // TODO Auto-generated method stub
        // Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(slider.getBundle().getString("extra"))));
    }

    private void initOfferWall() {
        // (Optional) Adxmi Android OfferWall can customize OfferWall Browser Top Title Config
        // setOfferBrowserConfig();

        // If using rewarded OfferWall advertisement, remember to invoke the initialization of rewarded offerwall advertisement:
        OffersManager.getInstance(this).onAppLaunch();

        // (Optional) Register listener for OfferWall currency points, to get notification of currency points changing
        PointsManager.getInstance(this).registerNotify(this);

        // (Optional) Register listener for earning OfferWall currency points
        PointsManager.getInstance(this).registerPointsEarnNotify(this);

        // (Optional) If showing the successfully earning  OfferWall  currency points hint in notification bar. The default value is
        // true, which means enable. False means disable.
        // PointsManager.setEnableEarnPointsNotification(false);

        // (Optional) If showing the Toast hint of earning  OfferWall currency points. The default value is true, which means
        // enable. False means disable.
        // PointsManager.setEnableEarnPointsToastTips(false);
    }

    private void initVideoAd() {

        // (Optional) Register listener for OfferWall currency points, to get notification of currency points earn
        VideoAdManager.getInstance(this).registerRewards(this);

        // (Optional)If jump to WebView page when click close btn.The default value is true,which means
        //enable.False means disable
        //VideoAdManager.setCloseBtnToDetail(false);

        // (Optional) If showing the Toast hint of earning  Video currency points. The default value is true, which means
        // enable. False means disable.
        // VideoAdManager.setEnableRewardsToastTips(true);

        //Loading video ads
        VideoAdManager.getInstance(this).requestVideoAd(new VideoAdRequestListener() {

            @Override
            public void onRequestSucceed() {
                Log.e("adxmi", "video request succeed");
            }

            @Override
            public void onRequestFail(int errorCode) {
                // Interpretation of the error code: -1 for the network connection fails,please check the network. -2007 for no ads, -3312
                //for the device number of plays of the day has been completed, additional error codes generally equipment problems.
                Log.e("adxmi", "video request fail");
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.leaderapps.Cashmyapps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyOfferWall();

        destroyVideoAd();

    }

    private void destroyOfferWall() {
        // Noticed: If register listener in OnCreate, please remember to cancel the listener in onDestroy by invoking
        // unRegisterNotify.
        PointsManager.getInstance(this).unRegisterNotify(this);

        // (Optional) Cancel listener for earning currency points. If register listener in onCreate, remember to cancel
        // it.
        PointsManager.getInstance(this).unRegisterPointsEarnNotify(this);

        // Remember to invoke the below code on application exit, to tell SDK that the application is closed, which can
        // make SDK release some resource.
        OffersManager.getInstance(this).onAppExit();
    }

    // function to award the points
    void award(int points, final String type){

        String awr = Config.Base_Url+"get/award.php";
        final String awrds = Integer.toString(points);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
        final String Current_Date = dd.format(c.getTime());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, awr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Enable this only on testing - yash
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Server Problem!",Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("points",awrds);
                params.put("type",type);
                params.put("date",Current_Date);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    void daily_award(int points, final String type){

        String awr = Config.Base_Url+"get/daily.php";
        final String awrds = Integer.toString(points);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
        final String Current_Date = dd.format(c.getTime());

        final String v1 ="1";
        final String v0 ="0";
        final String v2 ="2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, awr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Enable this only on testing - yash
                        if(response.intern() == v1.intern()) {
                            // Displaying Success dialog
                            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                            alert.setTitle("Great !!");
                            alert.setMessage(Config.daily_reward + " Points Successfully Received");
                            alert.setCanceledOnTouchOutside(false);

                            alert.setIcon(R.drawable.custom_img);

                            alert.setButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
                            alert.show();

                        }
                        if(response.intern() == v0.intern()){
                            // Displaying error dialog if already used

                            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();

                            alert.setTitle("Taken Already..");
                            alert.setMessage("Try After SomeTime!!");
                            alert.setCanceledOnTouchOutside(false);

                            alert.setButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing here
                                }

                            });
                            alert.show();

                        }
                        if(response.intern() == v2.intern()){

                            Toast.makeText(MainActivity.this,"server problem! Try Again after some time",Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("points",awrds);
                params.put("type",type);
                params.put("date",Current_Date);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    void BranchClickedPoint(int points){

        String awr = Config.Base_Url+"get/banchpoint.php";
        final String awrds = Integer.toString(points);


        final String v1 ="1";
        final String v0 ="0";
        final String v2 ="2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, awr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Enable this only on testing - yash
                        if(response.intern() == v1.intern()) {
                            // Displaying Success dialog
                            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                            alert.setTitle("Great !!");
                            alert.setMessage("30" + " Points Successfully Received from inviting.");
                            alert.setCanceledOnTouchOutside(false);

                            alert.setIcon(R.drawable.custom_img);

                            alert.setButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
/*
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);*/
                                }
                            });
                            alert.show();

                        }
                        if(response.intern() == v0.intern()){
                            // Displaying error dialog if already used

                            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();

                            alert.setTitle("Taken Already..");
                            alert.setMessage("Try After SomeTime!!");
                            alert.setCanceledOnTouchOutside(false);

                            alert.setButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing here
                                }

                            });
                            alert.show();

                        }
                        if(response.intern() == v2.intern()){

                            Toast.makeText(MainActivity.this,"server problem! Try Again after some time",Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("points",awrds);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    void BranchInvitePoint(int points){

        String awr = Config.Base_Url+"get/banchinvite.php";
        final String awrds = Integer.toString(points);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, awr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Enable this only on testing - yash

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                params.put("points",awrds);
                params.put("branchurl",branchClicked);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private void destroyVideoAd() {
        // (Optional) Cancel listener for earning currency points. If register listener in onCreate, remember to cancel
        // it.
        VideoAdManager.getInstance(this).unRegisterRewards(this);

        // Remember to invoke the below code on application exit, to tell SDK that the application is closed, which can
        // make SDK release some resource about video.
        VideoAdManager.getInstance(this).onDestroy();
    }


    private void initView() {

        pointTextView = (TextView) findViewById(R.id.tv_current_points);

        listView = (ListView) findViewById(R.id.listView);

        CustomAdapter adapter = new CustomAdapter(this, Config.titles, Config.images, Config.description);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {

                    // Daily Checkin
                    case 0:
                        // Granting Rewards seted in Config file
                        daily_award(Config.daily_reward,"Daily Checkin Credit");

                        break;
                    //Invite Friends.
                    case 1:
  /*                      ShareSheetStyle shareSheetStyle = new ShareSheetStyle(MainActivity.this, "Check this out!", "This is Cashmyapp Link... ")
                                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                                .setAsFullWidthStyle(true)
                                .setSharingTitle("Share With");

                        branchUniversalObject.showShareSheet(MainActivity.this,linkProperties, shareSheetStyle, new Branch.BranchLinkShareListener(){
                            @Override
                            public void onShareLinkDialogLaunched(){
                            }
                            @Override
                            public void onShareLinkDialogDismissed(){

                            }
                            @Override
                            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error){

                            }
                            @Override
                            public void onChannelSelected(String channelName){

                            }
                        });*/
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        Globals g = Globals.getInstance();
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is Cashmyapps link..."+g.getBranchURL());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);


                        break;
                    // Instructions
                    case 2:
                        Intent i = new Intent(getBaseContext(), InstructionsActivity.class);
                        startActivity(i);

                        break;

                    // Video ads SuperSonic
                    case 3:

                        if (mSupersonicInstance.isRewardedVideoAvailable())
                        //show rewarded video
                        {
                            mSupersonicInstance.showRewardedVideo();
                        } else {
                            Toast.makeText(MainActivity.this, "No Videos Available ! \n Try after SomeTime", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    // SuperSonic OfferWall
                    case 4:

                        //show the offerwall
                        if (mSupersonicInstance.isOfferwallAvailable()) {
                            mSupersonicInstance.showOfferwall();
                        } else {
                            Toast.makeText(MainActivity.this, "Loading....Try after Sometime !", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    // Offer Wall ads Adxmi(Apps)
                    case 5:
                        //checking the permission first
                        if (hasGetReadPhoneStatePermission()) {
                            OffersManager.getInstance(MainActivity.this).showOffersWall();
                        } else {
                            requestReadPhoneStatePermission();
                        }
                        break;


                    // Redeem
                    case 6:
                        Intent redeem = new Intent(getBaseContext(), RedeemActivity.class);
                        startActivity(redeem);
                        break;

                    // About
                    case 7:
                        Intent about = new Intent(getBaseContext(),AboutActivity.class);
                        startActivity(about);
                        break;
                }
            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.leaderapps.Cashmyapps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

/*
        Branch branch = Branch.getInstance(this);

        branch.initSession(new Branch.BranchReferralInitListener()
                           {
                               @Override
                               public void onInitFinished(JSONObject referringParams, BranchError error) {
                                   if (error == null) {
                                       // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                                       // params will be empty if no data found
                                       // ... insert custom logic here ...
                                   } else {
                                       Log.i("MyApp", error.getMessage());
                                   }
                               }
                           }
                , this.getIntent().getData(), this);
        branch.getFirstReferringParams();

*/


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("article/12345")
                .setTitle("Check out this article!")
                .setContentDescription("It’s really entertaining...")
                .setContentImageUrl("https://mysite.com/article_logo.png")
                .addContentMetadata("read_progress", "17%");



        linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$fallback_url", "https://play.google.com/store/apps/details?id=com.leaderapps.Cashmyapps");

/*
        branchUniversalObject.generateShortUrl( this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Log.i("MyApp", "Got my Branch link to share: " + url);
            }
        });*/





    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    class CustomAdapter extends ArrayAdapter<String> {
        Context context;

        int image[];

        String[] title;

        String[] description;

        public CustomAdapter(Context context, String[] titles, int[] imgs, String[] desc) {

            super(context, R.layout.single_row, titles);

            this.context = context;

            this.image = imgs;

            this.title = titles;

            this.description = desc;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.single_row, parent, false);

            ImageView myimage = (ImageView) row.findViewById(R.id.imageView);

            TextView mytitle = (TextView) row.findViewById(R.id.textView);

            TextView mydesc = (TextView) row.findViewById(R.id.textView2);

            myimage.setImageResource(image[position]);

            mytitle.setText(title[position]);

            mydesc.setText(description[position]);

            return row;
        }
    }

    /**
     * It will call back this method when OfferWall currency points remaining balance changing (This call-back method will be executed in UI
     * thread)
     */
    @Override
    public void onPointBalanceChange(int pointsBalance) {
        pointTextView.setText(getResources().getString(R.string.text_current_points) + pointsBalance);
        award(pointsBalance,"Adxmi OfferWall Credit");

        PointsManager.getInstance(this).spendPoints(pointsBalance);

    }

    /**
     * It will call back this method when successfully earning OfferWall currency points (This method will be executed in UI thread)
     */
    @Override
    public void onPointEarn(Context context, EarnPointsOrderList list) {
        for (int i = 0; i < list.size(); ++i) {
            EarnPointsOrderInfo info = list.get(i);
            Log.i("Adxmi", info.getMessage());
        }
    }

    /**
     * It will call back this method when successfully earning video rewards (This call-back method will be executed in UI
     * thread)
     */
    @Override
    public void onVideoRewards(int reward) {
        //If you also use OfferWall,you can use this method to save the video reward
        PointsManager.getInstance(this).awardPoints(reward);

        //Otherwise,If you only use Video Ad,you need to save the points by yourself ,we provide an example for you
        // diySaveVideoReward(reward);

    }

    // --------- Supersonic Rewarded Video Listener ---------
    @Override
    public void onRewardedVideoInitSuccess() {
        // called on init success of rewarded video
        Log.d(TAG, "onRewardedVideoInitSuccess");
    }

    @Override
    public void onRewardedVideoInitFail(SupersonicError supersonicError) {
        // called on init fail of rewarded video
        // you can get the error data by accessing the supersonicError object
        // supersonicError.getErrorCode();
        // supersonicError.getErrorMessage();
        Log.d(TAG, "onRewardedVideoInitFail" + " " + supersonicError);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        // called when the video is opened
        Log.d(TAG, "onRewardedVideoAdOpened");
        Toast.makeText(MainActivity.this, "Watch till the End to get Points", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // called when the video is closed
        Log.d(TAG, "onRewardedVideoAdClosed");
        // here we show a dialog to the user if he was rewarded
        // if the user was rewarded
    }


    @Override
    public void onVideoAvailabilityChanged(final boolean available) {
        // called when the video availbility has changed
        Log.d(TAG, "onVideoAvailabilityChanged" + " " + available);
        // Toast.makeText(MainActivity.this,"Videos Available !", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onVideoStart() {
        // called when the video has started
        Log.d(TAG, "onVideoStart");
    }

    @Override
    public void onVideoEnd() {
        // called when the video has ended
        Log.d(TAG, "onVideoEnd");
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
        // called when the video has been rewarded and a reward can be given to the user
        Log.d(TAG, "onRewardedVideoAdRewarded" + " " + placement);
        String rewardName = placement.getRewardName();
        int rewardAmount = placement.getRewardAmount();
        Toast.makeText(MainActivity.this, rewardAmount + " " + rewardName + " Received !", Toast.LENGTH_LONG).show();
        award(rewardAmount,"SuperSonic Video Ad Credit");


    }

    @Override
    public void onRewardedVideoShowFail(SupersonicError supersonicError) {
        // called when the video has failed to show
        // you can get the error data by accessing the supersonicError object
        // supersonicError.getErrorCode();
        // supersonicError.getErrorMessage();
        Log.d(TAG, "onRewardedVideoShowFail" + " " + supersonicError);
    }

    // --------- Supersonic Offerwall Listener ---------

    @Override
    public void onOfferwallInitSuccess() {
        // called when the offerwall has initiated successfully
        Log.d(TAG, "onOfferwallInitSuccess");
    }

    @Override
    public void onOfferwallInitFail(SupersonicError supersonicError) {
        // called when the offerwall has failed to init
        // you can get the error data by accessing the supersonicError object
        // supersonicError.getErrorCode();
        // supersonicError.getErrorMessage();
        Log.d(TAG, "onOfferwallInitFail" + " " + supersonicError);
    }

    @Override
    public void onOfferwallOpened() {
        // called when the offerwall has opened
        Log.d(TAG, "onOfferwallOpened");
    }

    @Override
    public void onOfferwallShowFail(SupersonicError supersonicError) {
        // called when the offerwall failed to show
        // you can get the error data by accessing the supersonicError object
        // supersonicError.getErrorCode();
        // supersonicError.getErrorMessage();
        Log.d(TAG, "onOfferwallShowFail" + " " + supersonicError);
    }


    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        // Log.d(TAG, "onOfferwallAdCredited" + " credits:" + credits + " totalCredits:" + totalCredits + " totalCreditsFlag:" + totalCreditsFlag);
        int c_amount = totalCredits;
        // Reading from SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int t_amount = settings.getInt("tamount", 0);

        if (c_amount == t_amount) {
            // do nothing
            // Toast.makeText(this, "OfferWall Closed", Toast.LENGTH_LONG).show();
        } else if (c_amount > t_amount) {
            // adding reward and saving
            int t = t_amount;
            int q = c_amount - t_amount;
            int n_amount = t + q;
            // Writing data to SharedPreferences
            Editor editor = settings.edit();
            editor.putInt("tamount", n_amount);
            editor.commit();

            Toast.makeText(MainActivity.this, q + "Points Received !", Toast.LENGTH_LONG).show();
            award(q,"SuperSonic OfferWall Credit");
            // Toast.makeText(this, "New Amount :" +n_amount +"Total :" +c_amount, Toast.LENGTH_LONG).show();

        } else {
            // Toast.makeText(this, "OfferWall Closed", Toast.LENGTH_LONG).show();

        }
        return false;
    }

    @Override
    public void onGetOfferwallCreditsFail(SupersonicError supersonicError) {
        // you can get the error data by accessing the supersonicError object
        // supersonicError.getErrorCode();
        // supersonicError.getErrorMessage();
        Log.d(TAG, "onGetOfferwallCreditsFail" + " " + supersonicError);
    }

    @Override
    public void onOfferwallClosed() {
        // called when the offerwall has closed
        Log.d(TAG, "onOfferwallClosed");

        Toast.makeText(MainActivity.this, "OfferWall Closed !", Toast.LENGTH_SHORT).show();
    }


    // Showing Points on ToolBar Menu
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // executing retreival task here
        String URL = Config.Base_Url+"get/gtuspo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println(response);
                        // Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                        menu.findItem(R.id.points).setTitle("Points :" + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",App.getInstance().getUsername());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.points:
                openredeem();
                return true;
            case R.id.sync:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void openredeem() {

        Intent redeem = new Intent(getBaseContext(), RedeemActivity.class);
        startActivity(redeem);
    }

    public void fcm_id(){
        String fcm_server = Config.Base_Url+"get/token.php";
        final String fcm_token = FirebaseInstanceId.getInstance().getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fcm_server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Enable this only on testing - yash
                        // Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",App.getInstance().getUsername());
                params.put("fcm_id",fcm_token);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    void diySaveVideoReward(int reward) {
        totalPoints += reward;
        pointTextView.setText(getResources().getString(R.string.text_current_points) + totalPoints);
        properties.setProperty("vc_name", vc_name);
        properties.setProperty("total_amount", "" + totalPoints);
        try {
            OutputStream outfile = openFileOutput("points.properties", 0);
            properties.store(outfile, "vc info");
            outfile.close();
        } catch (Exception err) {
        }

    }

    int diyLoadVideoReward() {
        properties = new Properties();
        int rewards;
        try {
            properties.load(openFileInput("points.properties"));
            vc_name = properties.getProperty("vc_name", "credits");
            rewards = Integer.parseInt(properties.getProperty("total_amount", "0"));
        } catch (Exception err) {
            vc_name = "credits";
            rewards = 0;
        }
        return rewards;

    }

}