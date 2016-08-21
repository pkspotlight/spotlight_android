package me.spotlight.spotlight.features.profile;

/**
 * Created by Anatol on 8/19/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public interface ProfileContract {

    void onProfileFetched(boolean ok);

    void onProfileUpdated(boolean ok);

    void onAvatarFetched(String url);

    void onAvatarUpdated(boolean ok);

//    void showProgress(boolean show);

}
