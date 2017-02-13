package com.example.quotes.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context){
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


    public long insertAuthor(SQLiteDatabase db, String firstName, String lastName){
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

    public long findAuthorId(SQLiteDatabase db, String authorFirstName, String authorLastName){
        Cursor cursor = db.rawQuery(
                "SELECT _id FROM Authors WHERE FirstName = ? AND LastName = ?",
                new String[]{authorFirstName, authorLastName});
        if(cursor.moveToFirst()) {
            long result =  cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public long findQuoteId(SQLiteDatabase db, long authorId, String content, boolean favorite){
        Cursor cursor = db.rawQuery(
                "SELECT _id, Content FROM Quotes WHERE Author_id = ? AND Content = ? AND Favorite = ?",
                new String[]{String.valueOf(authorId), content, String.valueOf(favorite?1:0)});
        if(cursor.moveToFirst()){
            long result =  cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    /**
     * Returns number of quotes with given Author_id
     */
    public int countQuotes(SQLiteDatabase db, long authorId){
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(_id) FROM Quotes WHERE Author_id = ?",
                new String[]{String.valueOf(authorId)});
        if(cursor.moveToFirst()){
            int result =  cursor.getInt(0);
            cursor.close();
            return result;
        }
        return 0;
    }

    public void addQuote(SQLiteDatabase db, String authorFirstName, String authorLastName, String quoteContent,
                         boolean isFavorite){

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

    public int deleteQuote(SQLiteDatabase db, long quoteId){

        Cursor cursor = db.rawQuery(
                "SELECT Author_id FROM Quotes WHERE _id = ?",
                new String[]{String.valueOf(quoteId)});

        // Check if this author has other quotes
        if(cursor.moveToFirst()){
            long authorId =  cursor.getLong(0);
            Cursor othersCursor = db.rawQuery(
                    "SELECT COUNT(_id) AS Ids FROM Quotes WHERE Author_id = ?",
                    new String[]{String.valueOf(authorId)});

            if(othersCursor.moveToFirst() && othersCursor.getInt(0) < 2)
                db.delete("Authors", "_id=?", new String[]{String.valueOf(authorId)});
            othersCursor.close();
        }
        cursor.close();
        return db.delete("Quotes", "_id=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes existing quote, identified by quoteId with given values.
     */
    public int editQuote(SQLiteDatabase db, long quoteId, long authorId, String content, boolean favorite){
        ContentValues cv = new ContentValues();
        cv.put("Author_id", authorId);
        cv.put("Content", content);
        cv.put("Favorite", favorite);
        return db.update("Quotes", cv, "_id=" + quoteId, null);
    }

    /**
     * Changes Author_id of quote with given quoteId
     */
    public void editSingleQuotesAuthor(SQLiteDatabase db, long quoteId, long authorId){
        ContentValues cv = new ContentValues();
        cv.put("Author_id", authorId);
        db.update("Quotes", cv, "_id=" + quoteId, null);
    }

    /**
     * Changes Author_id of quotes with given oldAuthorId to newAuthorId and deletes author with oldAuthorId if there
     * are no remaining quotes by him/her.
     */
    public void editQuotesAuthor(SQLiteDatabase db, long oldAuthorId, long newAuthorId){
        ContentValues cv = new ContentValues();
        cv.put("Author_id", newAuthorId);
        db.update("Quotes", cv, "Author_id=" + oldAuthorId, null);
        if (countQuotes(db, oldAuthorId) == 0) deleteAuthor(db, oldAuthorId);
    }

    /**
     * Changes existing author, identified by authorId with given values.
     */
    public int editAuthor(SQLiteDatabase db, long authorId, String firstName, String lastName){
        ContentValues cv = new ContentValues();
        cv.put("FirstName", firstName);
        cv.put("lastName", lastName);
        return db.update("Authors", cv, "_id=" + authorId, null);
    }

    public int deleteAuthor(SQLiteDatabase db, long authorId){
        return db.delete("Authors", "_id=?", new String[]{String.valueOf(authorId)});
    }
}