package me.spotlight.spotlight.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseActivity;
import me.spotlight.spotlight.features.login.LoginFragment;
import me.spotlight.spotlight.features.signup.SignupFragment;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/11/2016.
 */
public class LoginActivity extends BaseActivity {

    CallbackManager callbackManager;

    /*
        Intent manufacturing
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // TODO:
                fetchFacebookUser();
            }

            @Override
            public void onCancel() {
                //
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }



    @OnClick(R.id.btn_signup)
    public void openSignup() {
        FragmentUtils.changeFragment(this, R.id.content_login, SignupFragment.newInstance(), true);
    }

    @OnClick(R.id.btn_login)
    public void openLogin() {
        FragmentUtils.changeFragment(this, R.id.content_login, LoginFragment.newInstance(), true);
    }

    @OnClick(R.id.btn_fb)
    public void openFb() {
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public profile"));
    }


    protected void fetchFacebookUser() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        if (user != null) {
                            onFacebookUserCompleted(user, graphResponse);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    protected void onFacebookUserCompleted(JSONObject user, GraphResponse graphResponse) {

        String email = "";

        try {
            email = user.getString("email");
        } catch (JSONException e) {
            //
        }

        try {
            final String id = user.getString("id");
            final String firstName = user.getString("first_name");
            final String lastName = user.getString("last_name");
            String picture = "https://graph.facebook.com/" + id + "/picture?type=large";

            // sign-up
            final ParseUser mUser = new ParseUser();
            mUser.setUsername(id);
            mUser.setPassword(id+firstName);
            mUser.put("firstName", firstName);
            mUser.put("lastName", lastName);
            mUser.setEmail(email);
            mUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
                        // success
                        startActivity(MainActivity.getStartIntent(LoginActivity.this));
                    } else {
//                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mUser.logInInBackground(id, id + firstName, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (null == e) {
                                    Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_LONG).show();
                                    startActivity(MainActivity.getStartIntent(LoginActivity.this));
                                } else {
                                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            // TODO:

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "err", Toast.LENGTH_LONG).show();
        }
    }

}

