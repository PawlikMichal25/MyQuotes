package com.example.quotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 1;

    public QuotesDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Authors (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                "FirstName TEXT NOT NULL, "+
                "LastName TEXT NOT NULL);");
        db.execSQL("CREATE TABLE Quotes (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Author_id INTEGER NOT NULL, " +
                "Content TEXT NOT NULL, " +
                "Favorite BOOLEAN NOT NULL," +
                "FOREIGN KEY(Author_id) REFERENCES Authors(_id));");
        setUpSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // TODO Delete inserting sample data
    private void setUpSampleData(SQLiteDatabase db) {
        long id1 = insertAuthor(db, "Jose", "Mourinho");
        long id2 = insertAuthor(db, "John", "Adams");
        long id3 = insertAuthor(db, "Peter", "Richards");
        insertQuote(db, id1, "I think I'm a special one", true);
        insertQuote(db, id2, "Be happy", false);
        insertQuote(db, id3, "Have a hobby", false);
    }


    private long insertAuthor(SQLiteDatabase db, String firstName, String lastName){
        ContentValues values = new ContentValues();
        values.put("FirstName", firstName);
        values.put("LastName", lastName);
        return db.insert("Authors", null, values);
    }

    private long insertQuote(SQLiteDatabase db, long authorId, String content, boolean isFavorite){
        ContentValues values = new ContentValues();
        values.put("Author_id", authorId);
        values.put("Content", content);
        values.put("Favorite", isFavorite);
        return db.insert("Quotes", null, values);
    }

    long findAuthorId(SQLiteDatabase db, String authorFirstName, String authorLastName){
        Cursor cursor = db.rawQuery(
                "SELECT _id FROM Authors WHERE FirstName = ? AND LastName = ?",
                new String[]{authorFirstName, authorLastName});
        if(cursor.moveToFirst())
            return cursor.getLong(0);
        return -1;
    }

    long findQuoteId(SQLiteDatabase db, long authorId, String content, boolean favorite){
        Cursor cursor = db.rawQuery(
                "SELECT _id, Content FROM Quotes WHERE Author_id = ? AND Content = ? AND Favorite = ?",
                new String[]{String.valueOf(authorId), content, String.valueOf(favorite?1:0)});
        if(cursor.moveToFirst()){
            return cursor.getLong(0);}
        return -1;
    }

    void addQuote(SQLiteDatabase db, String authorFirstName, String authorLastName, String quoteContent, boolean isFavorite){

        long authorId;

        // Check if author already exists:
        Cursor cursor = db.query("Authors",
                new String[]{"_id"},
                "FirstName = ? and LastName = ?",
                new String[]{authorFirstName, authorLastName},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())        // Author exists in the database
            authorId = cursor.getLong(0);
        else
            authorId = insertAuthor(db, authorFirstName, authorLastName);           // Create new author
        insertQuote(db, authorId, quoteContent, isFavorite);

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    /**
     * Changes existing quote, identified by quoteId with given values.
     */
    int editQuote(SQLiteDatabase db, long quoteId, long authorId, String content, boolean favorite){
        ContentValues cv = new ContentValues();
        cv.put("Author_id", authorId);
        cv.put("Content", content);
        cv.put("Favorite", favorite);
        return db.update("Quotes", cv, "_id=" + quoteId, null);
    }

    /**
     * Changes existing author, identified by authorId with given values.
     */
    int editAuthor(SQLiteDatabase db, long authorId, String firstName, String lastName){
        ContentValues cv = new ContentValues();
        cv.put("FirstName", firstName);
        cv.put("lastName", lastName);
        return db.update("Authors", cv, "_id=" + authorId, null);
    }
}