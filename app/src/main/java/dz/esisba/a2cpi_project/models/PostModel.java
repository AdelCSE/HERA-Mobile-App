package dz.esisba.a2cpi_project.models;

import java.io.Serializable;

public class PostModel implements Serializable {
   private String askedBy,publisher,Username,question,body,postid,Date,publisherPic;
   private int likesCount,answersCount;

    public PostModel(String askedBy, String publisher, String username, String question, String body, String postid, String date, String publisherPic, int likesCount, int answersCount) {
        this.askedBy = askedBy;
        this.publisher = publisher;
        this.Username = username;
        this.question = question;
        this.body = body;
        this.postid = postid;
        this.Date = date;
        this.publisherPic = publisherPic;
        this.likesCount = likesCount;
        this.answersCount = answersCount;
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

    public String getDate() {
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

    public void setDate(String date) {
        Date = date;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setAnswersCount(int answersCount) {
        this.answersCount = answersCount;
    }
}
