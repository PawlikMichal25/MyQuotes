package com.example.quotes;


import android.content.Context;
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
                "name TEXT NOT NULL, "+
                "surname TEXT NOT NULL);");
        db.execSQL("CREATE TABLE Quotes (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "author_id INTEGER NOT NULL, " +
                "content TEXT NOT NULL, " +
                "favorite BOOLEAN NOT NULL" +
                "date DATE, " +
                "FOREIGN KEY(author_id) REFERENCES Authors(_id);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
