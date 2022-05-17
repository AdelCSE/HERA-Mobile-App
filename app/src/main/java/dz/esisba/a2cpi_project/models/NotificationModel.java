package dz.esisba.a2cpi_project.models;

import com.google.firebase.firestore.Exclude;

public class NotificationModel {
    String Username, NotificationText, Date;
    int Image;

    @Exclude
    private String key;

    public NotificationModel(String username, String notificationText, String date, int image) {
        Username = username;
        NotificationText = notificationText;
        Date = date;
        Image = image;
    }

    public NotificationModel() {
    }

    public <T extends NotificationModel> T withId(final String id) {
        this.key = id;
        return (T) this;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getNotificationText() {
        return NotificationText;
    }

    public void setNotificationText(String notificationText) { NotificationText = notificationText; }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getImage() { return Image; }

    public void setImage(int image) {
        Image = image;
    }
}
