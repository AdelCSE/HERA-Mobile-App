package dz.esisba.a2cpi_project.models;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationModel {
    String Username, PostId , UserId , Image ,notifId;
    Timestamp Date;
    int Type;
    boolean Seen;//true if notification is seen before


//    @Exclude
//    private String key;

    public NotificationModel(String username, int type, Timestamp date, String postId, String userId, String image,String notifId) {
        Username = username;
        Type = type;
        Date = date;
        PostId = postId;
        UserId = userId;
        Image = image;
        this.notifId = notifId;
    }

    public NotificationModel() {
    }

    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
        /**/
        Date now = new Date();
        Date then = getDate().toDate();
        try {
            then = sfd.parse("11/06/2022"/*sfd.format(getDate().toDate())*/);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long duration = now.getTime()-then.getTime();
        long min = TimeUnit.MILLISECONDS.toMinutes(duration);
        if(min<1){return " Just Now";}
        if(min<60){
            return min+" minutes ago";
        }
        long hours =  TimeUnit.MILLISECONDS.toHours(duration);
        if(hours<24){
            return hours+" hours ago";
        }
        long day =  TimeUnit.MILLISECONDS.toDays(duration);
        if(day<7){
            return day+" days ago";
        }
        return day /~ 7+" weeks ago" ;
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
        return PostId;
    }

    public void setPostId(String postId) {
        this.PostId = postId;
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
