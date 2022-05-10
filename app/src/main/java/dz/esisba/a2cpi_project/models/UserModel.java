package dz.esisba.a2cpi_project.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String Name, Username, profilePictureUrl, bannerUrl, uid, bio;

    public UserModel(String Name, String Username, String profilePictureUrl) {
        this.Name = Name;
        this.profilePictureUrl = profilePictureUrl;
        this.Username = Username;
    }


    public UserModel(String name, String username, String profilePictureUrl, String bannerUrl, String uid, String bio) {
        Name = name;
        Username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.bannerUrl = bannerUrl;
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public UserModel(){};

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

}
