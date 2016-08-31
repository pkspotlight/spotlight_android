package me.spotlight.spotlight.features.teams.details;

import java.util.List;

/**
 * Created by Anatol on 8/31/2016.
 * Copyright (c) 2016 Spotlight Partners, Inc. All rights reserved.
 */
public interface TeamMembersContract {

    void onFriendIdsFetched(List<String> data);

    void showProgress(boolean show);
}
