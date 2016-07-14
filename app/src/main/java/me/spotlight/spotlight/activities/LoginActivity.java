package me.spotlight.spotlight.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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
import me.spotlight.spotlight.features.login.LoginFragment;
import me.spotlight.spotlight.features.login.SignupFragment;
import me.spotlight.spotlight.utils.FragmentUtils;

/**
 * Created by Anatol on 7/11/2016.
 */
public class LoginActivity extends BaseActivity {

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



}

