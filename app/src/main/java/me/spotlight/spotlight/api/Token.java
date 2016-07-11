package me.spotlight.spotlight.api;

import android.content.Context;
import android.content.SharedPreferences;

import me.spotlight.spotlight.base.BaseApplication;

/**
 * Created by Anatol on 7/11/2016.
 */
public class Token {

    public static void saveCredentials(String token) {
        SharedPreferences sharedPreferences =
                BaseApplication.getContext().getSharedPreferences("Token", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("token", token).commit();
    }

    public static String getToken() {
        SharedPreferences sharedPreferences =
                BaseApplication.getContext().getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token;
    }
}
