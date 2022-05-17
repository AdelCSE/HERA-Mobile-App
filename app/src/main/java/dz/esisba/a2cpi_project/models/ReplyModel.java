package dz.esisba.a2cpi_project.models;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;

public class ReplyModel {
    private String Username,Name,Question,Reply,Uid,ReplyId,ProfilePictureUrl;
    private Timestamp Date;

    public ReplyModel(String username, String name, String question, String reply, String uid, String requestId, String profilePictureUrl, Timestamp date) {
        Username = username;
        Name = name;
        Question = question;
        Reply = reply;
        Uid = uid;
        ReplyId = requestId;
        ProfilePictureUrl = profilePictureUrl;
        Date = date;
    }

    public ReplyModel() {
    }

    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sfd.format(getDate().toDate());
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getReply() {
        return Reply;
    }

    public void setReply(String reply) {
        Reply = reply;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getReplyId() {
        return ReplyId;
    }

    public void setReplyId(String requestId) {
        ReplyId = requestId;
    }

    public String getProfilePictureUrl() {
        return ProfilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) { ProfilePictureUrl = profilePictureUrl;}

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }
}
