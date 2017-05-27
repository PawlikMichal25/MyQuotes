package io.blacklagoonapps.myquotes.model;

public class Author {

    public static final String TABLE_NAME = "Authors";
    public String firstName;
    public String lastName;

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(lastName).append(" ").append(firstName).toString();
    }

    public static final class Columns {
        public static final String ID = "_id";
        public static final String FIRST_NAME = "FirstName";
        public static final String LAST_NAME = "LastName";

        private Columns() {
        }
    }
}
