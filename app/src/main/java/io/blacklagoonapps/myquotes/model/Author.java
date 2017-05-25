package io.blacklagoonapps.myquotes.model;

public class Author {

    public static final String TABLE_NAME = "Authors";
    private static final String EMPTY_STRING = "";
    private String firstName;
    private String lastName;

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Author(String firstName) {
        this(firstName, EMPTY_STRING);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
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
