package dz.esisba.a2cpi_project.models;

public class RequestModel {
    String Username,Question,Date;
    int Image;

    public RequestModel(String username, String question,String date, int image) {
        Username = username;
        Question = question;
        Date = date;
        Image = image;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }
}
