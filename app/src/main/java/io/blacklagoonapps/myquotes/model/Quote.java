package io.blacklagoonapps.myquotes.model;

public class Quote {

    public static final String TABLE_NAME = "Quotes";
    public Author author;
    public String content;

    public Quote(Author author, String content) {
        this.author = author;
        this.content = content;
    }

    public static final class Columns {
        public static final String ID = "_id";
        public static final String AUTHOR_ID = "Author_Id";
        public static final String CONTENT = "Content";

        private Columns() {
        }
    }
}
