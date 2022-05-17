package dz.esisba.a2cpi_project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostModel implements Serializable, Parcelable {
   private String askedBy,publisher,Username,question,body,postid,publisherPic,answerBy;
   private Timestamp Date;
   private int likesCount,answersCount, reportsCount;
   private ArrayList<String> tags, likes;

    public PostModel(String askedBy, String publisher, String username, String question, String body, String postid, Timestamp date, String publisherPic, int likesCount, int answersCount, ArrayList<String> tags) {
        this.askedBy = askedBy;
        this.publisher = publisher;
        Username = username;
        this.question = question;
        this.body = body;
        this.postid = postid;
        Date = date;
        this.publisherPic = publisherPic;
        this.likesCount = likesCount;
        this.answersCount = answersCount;
        this.tags = tags;
    }


    public PostModel(String askedBy, String publisher, String username, String question, String body, String postid, Timestamp date, String publisherPic, String answerBy, int likesCount, int answersCount, int reportsCount, ArrayList<String> tags, ArrayList<String> likes) {
        this.askedBy = askedBy;
        this.publisher = publisher;
        Username = username;
        this.question = question;
        this.body = body;
        this.postid = postid;
        Date = date;
        this.publisherPic = publisherPic;
        this.answerBy = answerBy;
        this.likesCount = likesCount;
        this.answersCount = answersCount;
        this.reportsCount = reportsCount;
        this.tags = tags;
        this.likes = likes;
    }

    public String ConvertDate()
    {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
        return  sfd.format(getDate().toDate());
    }

    protected PostModel(Parcel in) {
        askedBy = in.readString();
        publisher = in.readString();
        Username = in.readString();
        question = in.readString();
        body = in.readString();
        postid = in.readString();
        publisherPic = in.readString();
        answerBy = in.readString();
        Date = in.readParcelable(Timestamp.class.getClassLoader());
        likesCount = in.readInt();
        answersCount = in.readInt();
        reportsCount = in.readInt();
        tags = in.createStringArrayList();
        likes = in.createStringArrayList();
    }

    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel in) {
            return new PostModel(in);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void change(String text){
        question = text;
    }

    public String getPublisherPic() {
        return publisherPic;
    }

    public void setPublisherPic(String publisherPic) {
        this.publisherPic = publisherPic;
    }

    public PostModel() {
    }

    public String getAskedBy() {
        return askedBy;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUsername() {
        return Username;
    }

    public String getQuestion() {
        return question;
    }

    public String getBody() {
        return body;
    }

    public String getPostid() {
        return postid;
    }

    public Timestamp getDate() {
        return Date;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getAnswersCount() {
        return answersCount;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setAnswersCount(int answersCount) {
        this.answersCount = answersCount;
    }

    public String getAnswerBy() {
        return answerBy;
    }

    public int getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(askedBy);
        parcel.writeString(publisher);
        parcel.writeString(Username);
        parcel.writeString(question);
        parcel.writeString(body);
        parcel.writeString(postid);
        parcel.writeString(publisherPic);
        parcel.writeString(answerBy);
        parcel.writeParcelable(Date, i);
        parcel.writeInt(likesCount);
        parcel.writeInt(answersCount);
        parcel.writeInt(reportsCount);
        parcel.writeStringList(tags);
        parcel.writeStringList(likes);
    }
}
