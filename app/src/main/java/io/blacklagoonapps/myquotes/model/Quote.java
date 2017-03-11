package io.blacklagoonapps.myquotes.model;

public class Quote {

    public static final String TABLE_NAME = "Quotes";
    private Author author;
    private String content;
    private boolean isFavorite;

    public Quote(Author author, String content, boolean isFavorite){
        this.author = author;
        this.content = content;
        this.isFavorite = isFavorite;
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

    public static final class Columns {
        public static final String ID = "_id";
        public static final String AUTHOR_ID = "Author_Id";
        public static final String CONTENT = "Content";
        public static final String FAVORITE = "Favorite";

        private Columns(){ }
    }
}
