package me.spotlight.spotlight.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.annotations.SerializedName;

import me.spotlight.spotlight.api.GsonConfig;
import me.spotlight.spotlight.base.BaseApplication;

/**
 * Created by Anatol on 7/11/2016.
 */
public class User {

    @SerializedName("logo")
    String logo;

    @SerializedName("id")
    String id;

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    @SerializedName("email")
    String email;

    public void save() {
        save(this);
    }

    public static void deleteCurrent() {
        save(null);
    }

    public static void save(User user) {
        String json = null;
        if (null != user) {
            json = GsonConfig.buildDefaultJson().toJson(user);
        }
        SharedPreferences sharedPreferences =
                BaseApplication.getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user", json).commit();
    }

    public static User getCurrent() {
        SharedPreferences sharedPreferences =
                BaseApplication.getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("user", null);
        if (null != json) {
            return GsonConfig.buildDefaultJson().fromJson(json, User.class);
        }
        return new User();
    }
}
