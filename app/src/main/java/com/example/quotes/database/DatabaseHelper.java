package com.example.quotes.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quotes.model.Author;
import com.example.quotes.model.Quote;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Quotes";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL);",
                Author.TABLE_NAME, Author.Columns.ID, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME));
        db.execSQL(String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s BOOLEAN NOT NULL," +
                "FOREIGN KEY(%s) REFERENCES %s(%s));",
                Quote.TABLE_NAME, Quote.Columns.ID, Quote.Columns.AUTHOR_ID, Quote.Columns.CONTENT,
                Quote.Columns.FAVORITE, Quote.Columns.AUTHOR_ID, Author.TABLE_NAME, Author.Columns.ID));
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
        long id4 = insertAuthor(db, "Ravulagaluhabudaba", "Ravindrababu");
        long id5 = insertAuthor(db, "Sokrates", "");
        long id6 = insertAuthor(db, "C2C", "Netik");

        insertQuote(db, id1, "I think I'm a special one", true);

        insertQuote(db, id2, "Be happy", false);

        insertQuote(db, id3, "Have a hobby", false);
        insertQuote(db, id3, "Have a hobby", false);
        insertQuote(db, id3, "Have a hobby", true);

        insertQuote(db, id4, "Long author, short quote", true);
        insertQuote(db, id4, "Long author and very, absolutely overwhelming quote, with best greetings to all night owls. " +
                "To be perfectly honest with you, it\'s not that late yet.", false);

        insertQuote(db, id5, "The only true wisdom is in knowing you know nothing.", true);

        insertQuote(db, id6, "Some people see things how they are and ask why, I dream things that never were and ask why not?", true);
    }

    public long insertAuthor(SQLiteDatabase db, String firstName, String lastName){
        ContentValues values = new ContentValues();
        values.put(Author.Columns.FIRST_NAME, firstName);
        values.put(Author.Columns.LAST_NAME, lastName);
        return db.insert(Author.TABLE_NAME, null, values);
    }

    private long insertQuote(SQLiteDatabase db, long authorId, String content, boolean isFavorite){
        ContentValues values = new ContentValues();
        values.put(Quote.Columns.AUTHOR_ID, authorId);
        values.put(Quote.Columns.CONTENT, content);
        values.put(Quote.Columns.FAVORITE, isFavorite);
        return db.insert(Quote.TABLE_NAME, null, values);
    }

    public long findAuthorId(SQLiteDatabase db, String authorFirstName, String authorLastName){
        String query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?",
                Author.Columns.ID, Author.TABLE_NAME, Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME);
        Cursor cursor = db.rawQuery(query, new String[]{authorFirstName, authorLastName});
        if(cursor.moveToFirst()) {
            long result =  cursor.getLong(0);
            cursor.close();
            return result;
        }
        return -1;
    }

    public long findQuoteId(SQLiteDatabase db, long authorId, String content, boolean favorite){
        String query = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                Quote.Columns.ID, Quote.Columns.CONTENT, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID,
                Quote.Columns.CONTENT, Quote.Columns.FAVORITE);
        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(authorId), content, String.valueOf(favorite ? 1 : 0)});
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
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s = ?",
                Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(authorId)});
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
        Cursor cursor = db.query(Author.TABLE_NAME,
                new String[]{Author.Columns.ID},
                String.format("%s = ? AND %s = ?", Author.Columns.FIRST_NAME, Author.Columns.LAST_NAME),
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
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", Quote.Columns.AUTHOR_ID, Quote.TABLE_NAME,
                Quote.Columns.ID);
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quoteId)});

        // Check if this author has other quotes
        if(cursor.moveToFirst()){
            long authorId =  cursor.getLong(0);
            String countQuery = String.format("SELECT COUNT(%s) AS Ids FROM %s WHERE %s = ?",
                    Quote.Columns.ID, Quote.TABLE_NAME, Quote.Columns.AUTHOR_ID);
            Cursor othersCursor = db.rawQuery(countQuery, new String[]{String.valueOf(authorId)});

            if(othersCursor.moveToFirst() && othersCursor.getInt(0) < 2)
                db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
            othersCursor.close();
        }
        cursor.close();
        return db.delete(Quote.TABLE_NAME, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes existing quote, identified by quoteId with given values.
     */
    public int editQuote(SQLiteDatabase db, long quoteId, long authorId, String content, boolean favorite){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        cv.put(Quote.Columns.CONTENT, content);
        cv.put(Quote.Columns.FAVORITE, favorite);
        return db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes Author_id of quote with given quoteId
     */
    public void editSingleQuotesAuthor(SQLiteDatabase db, long quoteId, long authorId){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, authorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.ID + "=?", new String[]{String.valueOf(quoteId)});
    }

    /**
     * Changes Author_id of quotes with given oldAuthorId to newAuthorId and deletes author with oldAuthorId if there
     * are no remaining quotes by him/her.
     */
    public void editQuotesAuthor(SQLiteDatabase db, long oldAuthorId, long newAuthorId){
        ContentValues cv = new ContentValues();
        cv.put(Quote.Columns.AUTHOR_ID, newAuthorId);
        db.update(Quote.TABLE_NAME, cv, Quote.Columns.AUTHOR_ID + "=?", new String[]{String.valueOf(oldAuthorId)});
        if (countQuotes(db, oldAuthorId) == 0) deleteAuthor(db, oldAuthorId);
    }

    /**
     * Changes existing author, identified by authorId with given values.
     */
    public int editAuthor(SQLiteDatabase db, long authorId, String firstName, String lastName){
        ContentValues cv = new ContentValues();
        cv.put(Author.Columns.FIRST_NAME, firstName);
        cv.put(Author.Columns.LAST_NAME, lastName);
        return db.update(Author.TABLE_NAME, cv, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }

    public int deleteAuthor(SQLiteDatabase db, long authorId){
        return db.delete(Author.TABLE_NAME, Author.Columns.ID + "=?", new String[]{String.valueOf(authorId)});
    }
}