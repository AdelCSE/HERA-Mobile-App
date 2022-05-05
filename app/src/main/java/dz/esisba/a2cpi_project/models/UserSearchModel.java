package dz.esisba.a2cpi_project.models;

public class UserSearchModel {
    private String Name, Username, profilePictureUrl ;

    public UserSearchModel(String Name, String Username, String profilePictureUrl) {
        this.Name = Name;
        this.profilePictureUrl = profilePictureUrl;
        this.Username = Username;
    }

    public UserSearchModel(){};

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
