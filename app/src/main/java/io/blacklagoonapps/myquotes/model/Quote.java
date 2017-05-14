package io.blacklagoonapps.myquotes.model;

public class Quote {

    public static final String TABLE_NAME = "Quotes";
    private Author author;
    private String content;

    public Quote(Author author, String content){
        this.author = author;
        this.content = content;
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

    public static final class Columns {
        public static final String ID = "_id";
        public static final String AUTHOR_ID = "Author_Id";
        public static final String CONTENT = "Content";

        private Columns(){ }
    }
}
