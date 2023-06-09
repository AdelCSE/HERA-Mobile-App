package dz.esisba.a2cpi_project.models;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;

public class RequestModel {
    private String Username,Name,Question,Uid,RequestId,ProfilePictureUrl;
    private com.google.firebase.Timestamp Date;

    public RequestModel(String username, String name, String question, com.google.firebase.Timestamp date, String uid, String requestId, String profilePic) {
        Username = username;
        Name = name;
        Question = question;
        Date = date;
        Uid = uid;
        RequestId = requestId;
        ProfilePictureUrl = profilePic;
    }

    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sfd.format(getDate().toDate());
    }

    public RequestModel() {
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

    public com.google.firebase.Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getProfilePictureUrl() {
        return ProfilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePic) {
        ProfilePictureUrl = profilePic;
    }
}
