package com.leaderapps.Cashmyapps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.leaderapps.Cashmyapps.app.App;
import com.leaderapps.Cashmyapps.common.ActivityBase;
import com.leaderapps.Cashmyapps.util.CustomRequest;
import com.leaderapps.Cashmyapps.util.Helper;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.ServerRequest;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.LinkProperties;

public class SignupActivity extends ActivityBase {

    private BranchUniversalObject branchUniversalObject;
    private LinkProperties linkProperties;
    EditText signupUsername, signupFullname, signupPassword, signupEmail;
    Button signupJoinBtn;

    private String username, fullname, password, email,branchurl;
    private static final String TAG = "MyActivity";
    @Override
    protected void onStart(){
        super.onStart();
 //       Branch.getInstance(getApplicationContext()).userCompletedAction(BranchEvent.SHARE_STARTED);
        Branch branch = Branch.getInstance(this);
 //       branch.setIdentity("aaaa");

        branch.initSession(new Branch.BranchReferralInitListener() {
               @Override
               public void onInitFinished(JSONObject referringParams, BranchError error) {
                   Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                   AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SignupActivity.this);
                   dlgAlert.setMessage(referringParams.toString());
                   dlgAlert.setTitle("App Title");
                   dlgAlert.setPositiveButton("OK", null);
                   dlgAlert.setCancelable(true);
                   dlgAlert.setPositiveButton("Ok",
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   //dismiss the dialog
                               }
                           });
 //                  dlgAlert.create().show();
                   if (error == null) {


                       try {
                           String isbranchclicked = referringParams.getString("+clicked_branch_link");
                           String clickedbranchurl = referringParams.getString("~referring_link");

                           AlertDialog.Builder dlgAlert1 = new AlertDialog.Builder(SignupActivity.this);
                           dlgAlert1.setMessage(isbranchclicked+clickedbranchurl);
                           dlgAlert1.setTitle("App Title");
                           dlgAlert1.setPositiveButton("OK", null);
                           dlgAlert1.setCancelable(true);
                           dlgAlert1.setPositiveButton("Ok",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           //dismiss the dialog
                                       }
                                   });
  //                         dlgAlert1.create().show();
                           Globals g = Globals.getInstance();
                           g.setData(isbranchclicked,clickedbranchurl);
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                       // params will be empty if no data found
                       // ... insert custom logic here ...
                   } else {
                       Log.i("MyApp", error.getMessage());
                   }
               }
           }
        , this.getIntent().getData(), this);
 //       branch.getFirstReferringParams();
           JSONObject linkedparams=Branch.getInstance().getLatestReferringParams();

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


        branchUniversalObject.generateShortUrl( this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Log.i("MyApp", "Got my Branch link to share: " + url);
                branchurl = url;
                Globals g = Globals.getInstance();
                g.setBranchURL(url);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {
                String autoDeeplinkedValue = Branch.getInstance().getLatestReferringParams().getString("picurl");
                Log.i("BranchConfigTest", "Clicked! " + autoDeeplinkedValue);
//                launch_mode_txt.setText("Launched by Branch on auto deep linking!"
  //                      + "\n\n" + autoDeeplinkedValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
//            launch_mode_txt.setText("Launched by normal application flow");
      //      Log.i("Launched by normal application flow ");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       Branch.getAutoInstance(this);







        Branch.enableCookieBasedMatching("Cashmyapps.app.link");
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        signupUsername = (EditText) findViewById(R.id.signupUsername);
        signupFullname = (EditText) findViewById(R.id.signupFullname);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupEmail = (EditText) findViewById(R.id.signupEmail);

        signupJoinBtn = (Button) findViewById(R.id.signupJoinBtn);

        signupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = signupUsername.getText().toString();
                fullname = signupFullname.getText().toString();
                password = signupPassword.getText().toString();
                email = signupEmail.getText().toString();
 //               branchurl = "111";

                if (verifyRegForm()) {

                    if (App.getInstance().isConnected()) {

                        showpDialog();

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SIGNUP, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                                        if (App.getInstance().authorize(response)) {

                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(i);

                                            ActivityCompat.finishAffinity(SignupActivity.this);

                                        } else {

                                            switch (App.getInstance().getErrorCode()) {

                                                case 300 : {

                                                    signupUsername.setError(getString(R.string.error_login_taken));
                                                    break;
                                                }

                                                case 301 : {

                                                    signupEmail.setError(getString(R.string.error_email_taken));
                                                    break;
                                                }

                                                default: {

                                                    Log.e("Profile", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                                                    break;
                                                }
                                            }
                                        }

                                        hidepDialog();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("Profile", "Malformed JSON: \"" + error.getMessage() + "\"");

                                hidepDialog();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("username", username);
                                params.put("fullname", fullname);
                                params.put("password", password);
                                params.put("email", email);
                                params.put("clientId", CLIENT_ID);
                                params.put("branchurl", branchurl);
                                return params;
                            }
                        };

                        App.getInstance().addToRequestQueue(jsonReq);

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    public Boolean verifyRegForm() {

        signupUsername.setError(null);
        signupFullname.setError(null);
        signupPassword.setError(null);
        signupEmail.setError(null);

        Helper helper = new Helper();

        if (username.length() == 0) {

            signupUsername.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (username.length() < 5) {

            signupUsername.setError(getString(R.string.error_small_username));

            return false;
        }

        if (!helper.isValidLogin(username)) {

            signupUsername.setError(getString(R.string.error_wrong_format));

            return false;
        }

        if (fullname.length() == 0) {

            signupFullname.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() == 0) {

            signupPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            signupPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            signupPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        if (email.length() == 0) {

            signupEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            signupEmail.setError(getString(R.string.error_wrong_format));

            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed(){

        finish();
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up utton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
