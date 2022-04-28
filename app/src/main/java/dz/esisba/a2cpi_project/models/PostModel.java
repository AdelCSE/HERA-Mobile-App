package dz.esisba.a2cpi_project.models;

public class PostModel {
    String Name,Username,Question,Details,Likes,Answers;
    int Image;

    public PostModel(int image, String name, String username, String question, String details, String likes, String answers) {
        Image = image;
        Name = name;
        Username = username;
        Question = question;
        Details = details;
        Likes = likes;
        Answers = answers;


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
}
