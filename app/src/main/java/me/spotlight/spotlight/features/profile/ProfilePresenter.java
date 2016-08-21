package me.spotlight.spotlight.features.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import me.spotlight.spotlight.utils.ParseConstants;

/**
 * Created by Anatol on 8/19/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public class ProfilePresenter {

    public static final String TAG = "ProfilePresenter";
    private ProfileContract contract;

    public ProfilePresenter(ProfileContract contract) {
        this.contract = contract;
    }

    public void fetchProfile() {
        //
    }

    public void updateProfile(Bundle bundle) {
        //
    }

    public void updateAvatar(byte[] bytes) {
        try {
//            contract.showProgress(true);
            ParseFile pictureFile = new ParseFile("image.png", bytes);
            ParseObject pictureObject = new ParseObject(ParseConstants.OBJECT_PROFILE_PIC);
            pictureObject.put(ParseConstants.FIELD_OBJECT_MEDIA_FILE, pictureFile);
            ParseUser.getCurrentUser().put(ParseConstants.FIELD_USER_PIC, pictureObject);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (null == e) {
//                        contract.showProgress(false);
                        contract.onAvatarUpdated(true);
                    } else {
//                        contract.showProgress(false);
                        contract.onAvatarUpdated(false);
                        Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
                    }
                }
            });
        } catch (Exception e){
//            contract.showProgress(false);
            Log.d(TAG, (null != e.getMessage()) ? e.getMessage() : "");
        }
    }

    public void loadAvatar() {
        //
    }
}
