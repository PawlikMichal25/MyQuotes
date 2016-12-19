package com.example.quotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

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
                "Date DATE, " +
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


    long insertAuthor(SQLiteDatabase db, String name, String surname){
        ContentValues values = new ContentValues();
        values.put("FirstName", name);
        values.put("LastName", surname);
        return db.insert("Authors", null, values);
    }

    long insertQuote(SQLiteDatabase db, long authorId, String content, boolean isFavorite){
        ContentValues values = new ContentValues();
        values.put("Author_id", authorId);
        values.put("Content", content);
        values.put("Favorite", isFavorite);
        values.put("Date", String.valueOf(new Date()));
        return db.insert("Quotes", null, values);
    }
}
