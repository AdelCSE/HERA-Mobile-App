package dz.esisba.a2cpi_project.models;

public class PostModel {
    String Name,Username,Question,Details,Likes,Answers,Date;
    int Image;

    public PostModel(int image, String name, String username, String question, String details, String likes, String answers,String date) {
        Image = image;
        Name = name;
        Username = username;
        Question = question;
        Details = details;
        Likes = likes;
        Answers = answers;
        Date = date;


    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getLikes() {
        return Likes;
    }

    public void setLikes(String likes) {
        Likes = likes;
    }

    public String getAnswers() {
        return Answers;
    }

    public void setAnswers(String answers) {
        Answers = answers;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
