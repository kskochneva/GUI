package com.flashcard;
//адрес папки

public class Flashcard {
    private String question;
    private String answer;

    //конструктор
    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}