package me.spotlight.spotlight.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseActivity;

/**
 * Created by Anatol on 7/11/2016.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.login_username)
    EditText loginUsername;
    @Bind(R.id.login_password)
    EditText loginPassword;
    @Bind(R.id.login_checkbox)
    CheckBox checkBox;

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
    }

    @OnClick(R.id.login_submit)
    public void submitLogin() {
        if (validate()) {
            if (checkBox.isChecked()) {
                // sign-up
                ParseUser mUser = new ParseUser();
                mUser.setUsername(loginUsername.getText().toString());
                mUser.setPassword(loginPassword.getText().toString());
                mUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (null == e) {
                            // success
//                            Toast.makeText(getApplication(), "Success!", Toast.LENGTH_SHORT).show();
                            startActivity(MainActivity.getStartIntent(getApplicationContext()));
                        } else {
                            Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                // sign-in
                ParseUser mUser = new ParseUser();
                mUser.setUsername(loginUsername.getText().toString());
                mUser.setPassword(loginPassword.getText().toString());
                mUser.logInInBackground(loginUsername.getText().toString(),
                        loginPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (null != user) {
//                                    Toast.makeText(getApplication(), "Success!", Toast.LENGTH_SHORT).show();
                                    startActivity(MainActivity.getStartIntent(getApplicationContext()));
                                } else {
                                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }



    public boolean validate() {

        boolean valid = true;
        if (TextUtils.isEmpty(loginUsername.getText().toString()))
            valid = false;
        if (TextUtils.isEmpty(loginPassword.getText().toString()))
            valid = false;
        return valid ;
    }
}

