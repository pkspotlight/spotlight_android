package me.spotlight.spotlight.features.friends.details;

import java.util.List;

/**
 * Created by Anatol on 8/31/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public interface FriendTeamsContract {

    void showProgress(boolean show);

    void onTeamsIdsFetched(List<String> data);
}
