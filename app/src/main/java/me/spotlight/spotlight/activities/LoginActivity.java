package me.spotlight.spotlight.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.spotlight.spotlight.R;
import me.spotlight.spotlight.base.BaseActivity;

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
    }
}
