package com.opriday.islamicquiz.model;

public class User {
    String email;
    String username;
    String image;
    String age;
    String total_attempt;
    String score;
    String total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTotal_attempt() {
        return total_attempt;
    }

    public void setTotal_attempt(String total_attempt) {
        this.total_attempt = total_attempt;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}
