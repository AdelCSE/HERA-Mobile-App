package dz.esisba.a2cpi_project.models;

public class NotificationModel {
    String Username, NotificationText, Date;
    int Image;

    public NotificationModel(String username, String notificationText, String date, int image) {
        Username = username;
        NotificationText = notificationText;
        Date = date;
        Image = image;
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
