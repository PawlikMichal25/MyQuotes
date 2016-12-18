package com.example.quotes.model;

public class Quote {

    private Author author;
    private String content;
    private boolean isFavorite;

    public Quote(Author author, String content){
        this.author = author;
        this.content = content;
        isFavorite = false;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
