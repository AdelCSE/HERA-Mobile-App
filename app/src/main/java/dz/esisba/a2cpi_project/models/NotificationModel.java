package dz.esisba.a2cpi_project.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;

public class NotificationModel {
    String Username, postId , userId , Image;
    Timestamp Date;
    int Type;

    @Exclude
    private String key;

    public NotificationModel(String username, int type, Timestamp date, String postId, String userId, String image) {
        Username = username;
        Type = type;
        Date = date;
        this.postId = postId;
        this.userId = userId;
        Image = image;
    }

    public NotificationModel() {
    }

    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
        return  sfd.format(getDate().toDate());
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

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
