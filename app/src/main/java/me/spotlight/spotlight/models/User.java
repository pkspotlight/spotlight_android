package me.spotlight.spotlight.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

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

    @SerializedName("username")
    String username;

    @SerializedName("password")
    String password;

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    @SerializedName("email")
    String email;


    /*
        Temp fields for Parse.com objects
     */

    String objectId;
    boolean emailVerified;
    List<Friend> friends;
    String phone;
    ProfilePictureMedia profilePic;
    Date createdAt;
    Date updatedAt;
    String family;
    String favoriteSport;
    String homeTown;
    List<Child> children;
    List<Team> teams;
    String avatarUrl;



    /*
        Temp Getter/Setters for Parse.com objects
     */

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ProfilePictureMedia getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ProfilePictureMedia profilePic) {
        this.profilePic = profilePic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getFavoriteSport() {
        return favoriteSport;
    }

    public void setFavoriteSport(String favoriteSport) {
        this.favoriteSport = favoriteSport;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    /*
        Utility methods for "current user" functionality after leaving Parse.com
     */


    public void save() {
        save(this);
    }

    public static void deleteCurrent() {
        save(null);
    }

    public static void save(User user) {
        String json = null;
        if (null != user) {
//            json = GsonConfig.buildDefaultJson().toJson(user);
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
//            return GsonConfig.buildDefaultJson().fromJson(json, User.class);
        }
        return new User();
    }


    /*
        Getter/Setters for serialized fields (after leaving Parse.com)
     */

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
