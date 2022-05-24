package dz.esisba.a2cpi_project.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;

public class NotificationModel {
    String Username, postId , UserId , Image ,notifId;
    Timestamp Date;
    int Type;
    boolean Seen;//true if notification is seen before


//    @Exclude
//    private String key;

    public NotificationModel(String username, int type, Timestamp date, String postId, String userId, String image,String notifId) {
        Username = username;
        Type = type;
        Date = date;
        this.postId = postId;
        UserId = userId;
        Image = image;
        this.notifId = notifId;
    }



    public NotificationModel() {
    }


    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
        return  sfd.format(getDate().toDate());
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
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getNotifId() {
        return notifId;
    }

    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        this.Seen = seen;
    }
}
